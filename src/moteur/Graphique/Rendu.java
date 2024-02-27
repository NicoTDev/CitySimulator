package moteur.Graphique;

import moteur.Fenetre;
import moteur.scene.Scene;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 */
public class Rendu {


    RenduScene renduScene;
    public Rendu(Fenetre fenetre) {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_CULL_FACE);
        //glCullFace(GL_BACK);
        renduScene = new RenduScene();

    }
    public void detruireProgramme() {
        renduScene.detruireProgramme();
        //
    }
    public void rendre(Fenetre fenetre, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0,0,fenetre.getLargeur(),fenetre.getHauteur());

        renduScene.rendre(scene);

    }

    public void resize(int largeur, int hauteur) {}
}
