package moteur.Graphique;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;


/**
 *
 */
public class UniformsMap {

    private int idProgramme;

    private HashMap<String, Integer> uniforms;

    public UniformsMap(int idProgramme) {
        this.idProgramme = idProgramme;
        uniforms = new HashMap<>();
    }

    public void creerUniform(String nom) {
        int posUniform = glGetUniformLocation(idProgramme,nom);
        if (posUniform < 0)
            throw new IllegalArgumentException("Problème avec l'initialisation de l'uniform " + nom);
        uniforms.put(nom,posUniform);
    }

    public void setUniform(String uniform, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Integer location = uniforms.get(uniform);
            if (location == null)
                throw new IllegalArgumentException(uniform + " est inexistant");
            glUniformMatrix4fv(location.intValue(),false,value.get(stack.mallocFloat(16)));
        }
    }
}
