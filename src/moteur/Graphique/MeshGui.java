package moteur.Graphique;

import imgui.ImDrawData;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
public class MeshGui {

    private int idVao;
    private int verticlesVbo;
    private int indicesVbo;

    public MeshGui() {

        //créer le vao
        idVao = glGenVertexArrays();
        glBindVertexArray(idVao);

        //créer le vbo de vertices
        verticlesVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, verticlesVbo);
        glEnableVertexAttribArray(0);

        //mettre les paramètres
        glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.SIZEOF_IM_DRAW_VERT, 16);

        //créer le vbo d'indices
        indicesVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }

    public void detruireProgramme() {
        glDeleteBuffers(verticlesVbo);
        glDeleteBuffers(indicesVbo);
        glDeleteVertexArrays(idVao);
    }

    public int getIdVao() {
        return idVao;
    }

    public int getVerticlesVbo() {
        return verticlesVbo;
    }

    public int getIndicesVbo() {
        return indicesVbo;
    }
}
