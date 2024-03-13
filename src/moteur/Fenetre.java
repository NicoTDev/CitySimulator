package moteur;


//importez les librairies
import moteur.scene.Entite;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.pmw.tinylog.Logger;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import java.util.concurrent.Callable;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Classe responsable de gérer les paramètres de base de la fenêtre et de la créer
 */
public class Fenetre {

    //mettre les variables
    private long refFenetre;
    private int largeur;

    private int hauteur;
    private EntreSouris entreSouris;

    private Callable<Void> fonctionResize;


    public Fenetre(String titre, optionFenetre opts, Callable<Void> fonctionResize) {

        this.fonctionResize = fonctionResize;


        //initialiser GLFW
        if (!glfwInit()) {
            throw new RuntimeException("Incapable de démarrer LWJGL");
        }


        //mettre les paramètres de base
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);


        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        //profile??
        if (opts.profileCompatible) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        //mettre la bonne taille de fenetre
        //si la taille de fenetre est correcte, la mettre
        if (opts.hauteur > 0 && opts.hauteur > 0) {
            this.largeur = opts.largeur;
            this.hauteur = opts.hauteur;
        }
        //sinon, on maximise la fenetre
        else {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            largeur = vidMode.width();
            hauteur = vidMode.height();
        }

        //créer la fenetre
        refFenetre = glfwCreateWindow(largeur,hauteur, titre, NULL, NULL);
        if (refFenetre == NULL)
            throw new RuntimeException("La fenetre n'a pas pu etre creee");

        glfwSetFramebufferSizeCallback(refFenetre, ((window, l, h) -> resized(l,h)));

        glfwSetErrorCallback((int errorCode, long msgPtr) ->
                Logger.error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr))
        );

        glfwSetKeyCallback(refFenetre, (window, key, scancode, action, mods) -> {
            keyCallBack(key, action);
        });

        glfwMakeContextCurrent(refFenetre);

        if (opts.fps > 0)
            glfwSwapInterval(0);
        else
            glfwSwapInterval(1);

        glfwShowWindow(refFenetre);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(refFenetre, arrWidth, arrHeight);
        largeur = arrWidth[0];
        hauteur = arrHeight[0];

        entreSouris = new EntreSouris(refFenetre);
    }


    public void detruireProgramme() {
        glfwFreeCallbacks(refFenetre);
        glfwDestroyWindow(refFenetre);
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }
    public void keyCallBack(int key, int action) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(refFenetre, true); // We will detect this in the rendering loop
        }
    }

    public boolean isToucheAppuye(int code) {
        return glfwGetKey(refFenetre,code) == GLFW_PRESS;
    }

    public boolean isWindowShouldClose() {
        return glfwWindowShouldClose(refFenetre);
    }

    public void mettreAJour() {
        glfwSwapBuffers(refFenetre);
    }

    public EntreSouris getEntreSouris() {
        return entreSouris;
    }

    protected void resized(int width, int height) {
        this.largeur = width;
        this.hauteur = height;
        try {
            fonctionResize.call();
        } catch (Exception excp) {
            Logger.error("Error calling resize callback", excp);
        }
    }
    public void pollEvents() {
        glfwPollEvents();
    }
    public static class optionFenetre {
        public boolean profileCompatible;
        public int fps = 120;

        public int hauteur;

        public int ups = Moteur.TARGET_UPS;

        public int largeur;
    }
    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }
}