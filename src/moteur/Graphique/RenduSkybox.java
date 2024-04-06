package moteur.Graphique;

import moteur.scene.Entite;
import moteur.scene.Scene;
import moteur.scene.Skybox;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class RenduSkybox {

    private ShaderChargeur shaderChargeur;

    private UniformsMap uniformsMap;

    private Matrix4f matriceVue;

    public RenduSkybox() {
        ArrayList<ShaderChargeur.donneeShader> shaders = new ArrayList<>();
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/skybox/skybox.vert", GL_VERTEX_SHADER));
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/skybox/skybox.frag", GL_FRAGMENT_SHADER));
        shaderChargeur = new ShaderChargeur(shaders);
        matriceVue = new Matrix4f();


        uniformsMap = new UniformsMap(shaderChargeur.getIdProgramme());
        uniformsMap.creerUniform("matriceProjection");
        uniformsMap.creerUniform("matriceVue");
        uniformsMap.creerUniform("matriceModel");
        uniformsMap.creerUniform("diffuse");
        uniformsMap.creerUniform("txtSampler");
        uniformsMap.creerUniform("hasTexture");

    }
    public void rendre(Scene scene) {

        Skybox skybox = scene.getSkyBox();
        if (skybox == null) {
            return;
        }
        shaderChargeur.utiliser();

        uniformsMap.setUniform("matriceProjection", scene.getProjection().getMatriceProjection());
        matriceVue.set(scene.getCamera().getMatriceVue());
        matriceVue.m30(0);
        matriceVue.m31(0);
        matriceVue.m32(0);
        uniformsMap.setUniform("matriceVue", matriceVue);
        uniformsMap.setUniform("txtSampler", 0);

        Model skyBoxModel = skybox.getModelSkybox();
        Entite skyBoxEntity = skybox.getEntiteSkybox();
        TextureCache textureCache = scene.getTextureCache();
        for (Material material : skyBoxModel.getMateriaux()) {
            Texture texture = textureCache.getTexture(material.getCheminTexture());
            glActiveTexture(GL_TEXTURE0);
            texture.lier();

            uniformsMap.setUniform("diffuse", material.getCouleurDiffuse());
            uniformsMap.setUniform("hasTexture", texture.getCheminTexture().equals(TextureCache.TEXTURE_DEFAUT) ? 0 : 1);

            for (Mesh mesh : material.getMeshList()) {
                glBindVertexArray(mesh.getIdVao());

                uniformsMap.setUniform("matriceModel", skyBoxEntity.getMatriceModel());
                glDrawElements(GL_TRIANGLES, mesh.getNumVerticles(), GL_UNSIGNED_INT, 0);
            }
        }

        glBindVertexArray(0);

        shaderChargeur.delier();


    }
}
