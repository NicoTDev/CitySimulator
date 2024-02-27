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
        uniformsMap.creerUniform("matriceProjection");
        uniformsMap.creerUniform("matriceModel");
    }

    public void detruireProgramme() {
        shaderChargeur.detruireProgramme();
    }

    public void rendre(Scene scene) {
        shaderChargeur.utiliser();

        uniformsMap.setUniform("matriceProjection", scene.getProjection().getMatriceProjection());

        Collection<Model> models = scene.getDicoModel().values();
        for (Model model : models) {
            model.getMeshes().forEach(mesh -> {
                glBindVertexArray(mesh.getIdVao());
                List<Entite> entites = model.getEntites();
                for (Entite entite : entites) {
                    uniformsMap.setUniform("matriceModel", entite.getMatriceModel());
                    glDrawElements(GL_TRIANGLES, mesh.getNumVerticles(),GL_UNSIGNED_INT,0);
                }
            });
        }

        glBindVertexArray(0);

        shaderChargeur.delier();


    }
}
