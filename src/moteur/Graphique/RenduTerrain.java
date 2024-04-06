package moteur.Graphique;

import moteur.scene.Scene;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class RenduTerrain {

    private ShaderChargeur shaderChargeur;

    private Matrix4f matriceVue;

    private UniformsMap uniformsMap;

    public RenduTerrain(Scene scene) {

        ArrayList<ShaderChargeur.donneeShader> shaders = new ArrayList<>();
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/skybox/skybox.vert", GL_VERTEX_SHADER));
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/skybox/skybox.frag", GL_FRAGMENT_SHADER));
        shaderChargeur = new ShaderChargeur(shaders);
        matriceVue = new Matrix4f();




    }
}
