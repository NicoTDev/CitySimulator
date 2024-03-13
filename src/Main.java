import jeu.Terrain;
import moteur.*;
import moteur.Graphique.*;
import moteur.scene.Camera;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sin;

/**
 * Classe Main à lancer au démarrage
 */
public class Main implements ILogiqueJeu {


    private Entite entite;

    private Terrain terrain;

    private static final float VITESSE = 0.050f;

    private static final float SENSIBILITE = 0.05f;

    private float rotation = 0;

    public static void main(String args[]) {
        Main main = new Main();
        Moteur moteur = new Moteur("Jeu", new Fenetre.optionFenetre(), main);
        moteur.start();
    }

    @Override
    public void detruireProgramme() {


    }

    /**
     * Méthode lancée une fois au démarrage du jeu pour créer la scène
     * @param fenetre
     * @param scene
     * @param rendu
     */
    @Override
    public void initialisation(Fenetre fenetre, Scene scene, Rendu rendu) {

        Model cubeModel = ModelLoader.loadModel("cubeModel", "ressources/models/cube/cube.obj", scene.getTextureCache());
        scene.ajouterModel(cubeModel);


        //créer le model des arbres
        Model arbreModel = ModelLoader.loadModel("arbre-model","ressources/models/arbre/Arbre.obj", scene.getTextureCache());
        scene.ajouterModel(arbreModel);

        //pour le moment, la carte est créé à partir de cube, cela va changer plus tard
        final int TAILLECARTE = 30;


        //tester le skin de voiture
        Model modelCamion = ModelLoader.loadModel("camion-model-id", "ressources/models/camion/camion.obj", scene.getTextureCache());
        scene.ajouterModel(modelCamion);

        for (int i = 0 ; i < TAILLECARTE ; i++) {
            for (int j = 0 ; j < TAILLECARTE ; j++) {

                //int hauteur = (int) (sin(i)+sin(j));
                int hauteur = 0;
                Entite cube = new Entite("cube"+j+i, cubeModel.getId());
                cube.setPosition(i - (TAILLECARTE/2), hauteur, j - (TAILLECARTE/2));

                if ( (int) (Math.random()*100) >= 70) {
                    Entite camion = new Entite("camion-id", modelCamion.getId());
                    camion.setPosition(i - (TAILLECARTE / 2), hauteur + 0.5f, j - (TAILLECARTE / 2));
                    scene.ajouterEntite(camion);
                    camion.setRotation(0,1,0,(float)(Math.random()*2*Math.PI));
                }
                scene.ajouterEntite(cube);
            }
        }


    }

    /**
     * Méthode lancée chaque boucle du jeu et qui sert à recevoir les entrées de l'utilisateur
     * @param fenetre
     * @param scene
     * @param diffTempsMillis
     */
    @Override
    public void entree(Fenetre fenetre, Scene scene, long diffTempsMillis) {
        float mouvement = diffTempsMillis * VITESSE;
        Camera camera = scene.getCamera();

        //gérer les entrés de la caméra
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_W)) {
            camera.avancer(mouvement);
        }
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_S))
            camera.reculer(mouvement);
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_A))
            camera.allerGauche(mouvement);
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_D))
            camera.allerDroite(mouvement);
        if(fenetre.isToucheAppuye(GLFW.GLFW_KEY_SPACE))
            camera.monter(mouvement);
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_LEFT_SHIFT))
            camera.descendre(mouvement);

        EntreSouris entreSouris = fenetre.getEntreSouris();
        if (entreSouris.isBoutonDroitPresse()) {
            Vector2f vectDisp = entreSouris.getVectDisp();
            camera.rotationner((float) Math.toRadians(-vectDisp.x*SENSIBILITE),
                    (float) Math.toRadians(-vectDisp.y*SENSIBILITE));
        }




    }

    /**
     * Méthode lancée chaque rafraichissement de l'écran
     * @param fenetre ref de la fenetre
     * @param scene ref de la scène
     * @param diffTempsMillis
     */
    @Override
    public void miseAJour(Fenetre fenetre, Scene scene, long diffTempsMillis) {


    }
}

