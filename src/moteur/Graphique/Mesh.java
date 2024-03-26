package moteur.Graphique;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;


/**
 *
 */
public class Mesh {

    private int numVerticles;

    private int idVao;

    private List<Integer> vbosId;

    private Vector3f aabbMin;

    private Vector3f aabbMax;

    /**
     *
     * @param positions liste des positions des verticles
     * @param textCoords liste des positions des textures
     * @param indices liste des indices pour relier les verticles entre eux
     */
    public Mesh(float[] positions, float[] textCoords, int[] indices, Vector3f aabbMin, Vector3f aabbMax) {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            //mettre les collisions
            this.aabbMax = aabbMax;
            this.aabbMin = aabbMin;


            numVerticles = indices.length;

            vbosId = new ArrayList<>();

            //creer le vao et le lier
            idVao = glGenVertexArrays();
            glBindVertexArray(idVao);

            //creer les vbos de positions et d'index
            int idVbo = glGenBuffers();
            vbosId.add(idVbo);
            FloatBuffer bufferPosition = stack.callocFloat(positions.length);
            bufferPosition.put(0,positions);
            glBindBuffer(GL_ARRAY_BUFFER,idVbo);
            glBufferData(GL_ARRAY_BUFFER,bufferPosition,GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0,3,GL_FLOAT,false,0,0);


            //buffer de Texture
            idVbo = glGenBuffers();
            vbosId.add(idVbo);
            FloatBuffer bufferText = stack.callocFloat(textCoords.length);
            bufferText.put(0,textCoords);
            glBindBuffer(GL_ARRAY_BUFFER,idVbo);
            glBufferData(GL_ARRAY_BUFFER,bufferText, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1,2,GL_FLOAT,false,0,0);


            //créer la meme chose avec le vbo d'indices
            idVbo = glGenBuffers();
            vbosId.add(idVbo);
            IntBuffer bufferDindices = stack.callocInt(indices.length);
            bufferDindices.put(0,indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,idVbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,bufferDindices,GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER,0);
            glBindVertexArray(0);

        }


    }

    public void detruireProgramme() {
        vbosId.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(idVao);
    }

    public int getNumVerticles() {
        return numVerticles;
    }

    public final int getIdVao() {
        return idVao;
    }

    public Vector3f getAabbMax() {
        return aabbMax;
    }

    public Vector3f getAabbMin() {
        return aabbMin;
    }
}
