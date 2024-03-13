import jeu.Terrain;
import jeu.Voiture;
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

import static java.lang.Math.*;

/**
 * Classe Main à lancer au démarrage
 */
public class Main implements ILogiqueJeu {



    private static final float VITESSE = 0.050f;

    private static final float SENSIBILITE = 0.05f;

    private List<Voiture> voitures;

    public int rotation;



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

        //creer la liste des voitures du jeu
        voitures = new ArrayList<>();

        rotation = 0;
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
                scene.ajouterEntite(cube);
            }
        }

        //créer le véhicule
        Voiture camion = new Voiture("voiture1",modelCamion.getId());
        camion.setPosition(0,0.5f,0);
        scene.ajouterEntite(camion);
        camion.setVitesse(0.1f);
        camion.setRotation(0,1,0,(float)Math.toRadians(45));
        camion.setPosition(5,camion.getPosition().y,5);
        //camion.setRotation(0,1,0,(float) toRadians(90));
        voitures.add(camion);

        scene.getCamera().rotationner(0,(float)Math.toRadians(180));

        scene.getCamera().monter(5);


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
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_W))
            camera.avancer(mouvement);
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
        for (Voiture voiture : voitures) {
            //voiture.mettreAJourVoiture();
            //if (voiture.getRotation().y > 2*PI)
            //    voiture.setRotation(0,1,0,0);
            //if (rotation == 360)
            //    rotation = 0;
            //voiture.setRotation(0,1,0,(float) Math.toRadians(rotation++));

            //System.out.println("Angle y : " + voiture.getRotation().y + " Angle : " + voiture.getRotation().angle());

        }
    }

}

