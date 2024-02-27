package moteur.Graphique;


import moteur.scene.Entite;
import moteur.scene.Scene;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

/**
 *
 */
public class RenduScene {

    private ShaderChargeur shaderChargeur;

    private UniformsMap uniformsMap;

    public RenduScene() {

        //inclure tous les shaders
        List<ShaderChargeur.donneeShader> shaders = new ArrayList<>();
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/scene.vert",GL_VERTEX_SHADER));
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/scene.frag",GL_FRAGMENT_SHADER));
        //créer le programme pour charger les shaders
        shaderChargeur = new ShaderChargeur(shaders);

        //creer l'uniforms
        uniformsMap = new UniformsMap(shaderChargeur.getIdProgramme());
        uniformsMap.creerUniform("matriceVue");
        uniformsMap.creerUniform("txtSample");
        uniformsMap.creerUniform("matriceProjection");
        uniformsMap.creerUniform("matriceModel");
        uniformsMap.creerUniform("material.diffuse");
    }

    public void detruireProgramme() {
        shaderChargeur.detruireProgramme();
    }

    public void rendre(Scene scene) {
        shaderChargeur.utiliser();

        uniformsMap.setUniform("matriceProjection", scene.getProjection().getMatriceProjection());
        uniformsMap.setUniform("matriceVue",scene.getCamera().getMatriceVue());
        uniformsMap.setUniform("txtSample",0);

        Collection<Model> models = scene.getDicoModel().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {

            List<Entite> entites = model.getEntites();

            for (Material material : model.getMateriaux()) {
                uniformsMap.setUniform("material.diffuse",material.getCouleurDiffuse());
                Texture texture = textureCache.getTexture(material.getCheminTexture());
                glActiveTexture(GL_TEXTURE0);
                texture.lier();

                for (Mesh mesh : material.getMeshList()) {
                    glBindVertexArray(mesh.getIdVao());
                    for (Entite entite : entites) {
                        uniformsMap.setUniform("matriceModel",entite.getMatriceModel());
                        glDrawElements(GL_TRIANGLES, mesh.getNumVerticles(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        glBindVertexArray(0);

        shaderChargeur.delier();


    }
}
