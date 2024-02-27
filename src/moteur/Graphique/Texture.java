package moteur.Graphique;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import moteur.scene.Scene;
import org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int idTexture;

    private String cheminTexture;

    private static final String TEXTURE_MANQUANTE = "ressources/models/defaut/texture_manquante.jpg";


    public Texture(int largeur, int hauteur, ByteBuffer buffer) {

        this.cheminTexture = "";

        genererTexture(largeur,hauteur,buffer);
    }

    public Texture(String cheminTexture) {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            this.cheminTexture = cheminTexture;

            IntBuffer l = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            IntBuffer canaux = stack.mallocInt(1);

            ByteBuffer buf = stbi_load(cheminTexture, l, h, canaux,4);
            if (buf == null) {
                System.err.println("[ATTENTION] Erreur lors du chargement de la texture : " + cheminTexture);
                buf = stbi_load(TEXTURE_MANQUANTE,l,h,canaux,4);
            }

            int largeur = l.get();
            int hauteur = h.get();

            genererTexture(largeur,hauteur,buf);

            try {
                stbi_image_free(buf);
            } catch (Exception ignored ) {
                System.err.println("Erreur avec la création de la texture");
            }



        }

    }

    /**
     * méthode pour uploader les textures dans le GPU
     * @param largeur
     * @param hauteur
     * @param buffer
     */
    public void genererTexture(int largeur, int hauteur, ByteBuffer buffer) {

        //créer une nouvelle texture
        idTexture = glGenTextures();

        //lier la texture
        lier();

        //gérer une couple de paramètres par défaut (trouvé sur internet cette partie là)
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, largeur , hauteur, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void lier() {
        glBindTexture(GL_TEXTURE_2D,idTexture);
    }

    public String getCheminTexture() {
        return cheminTexture;
    }

    public void detruireProgramme() {
        glDeleteTextures(idTexture);
    }


}
