import Outil.CouleurConsole;
import Outil.MathLocal;
import jeu.*;
import moteur.Graphique.Terrain;
import moteur.*;
import moteur.Graphique.*;
import moteur.scene.Camera;
import moteur.scene.Entite;
import moteur.scene.Scene;
import moteur.scene.Skybox;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBIWriteCallbackI;

import java.lang.System.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Classe Main à lancer au démarrage
 */
public class Main implements ILogiqueJeu {


    private static final float VITESSE = 0.025f;

    private static final float SENSIBILITE = 0.05f;

    private List<Voiture> voitures;

    public Mode modeUtilisateur;

    boolean isRouteEnCours;

    SystemeRoutier systemeRoutier;

    Terrain terrain;

    long tempsActuel = System.currentTimeMillis();


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
     *
     * @param fenetre
     * @param scene
     * @param rendu
     */
    @Override
    public void initialisation(Fenetre fenetre, Scene scene, Rendu rendu) {

        //mettre les modes par défaut
        modeUtilisateur = Mode.CONSTRUCTEURDEROUTE;
        isRouteEnCours = false;



        //creer un terrain
        terrain = new Terrain();
        terrain.genererTerrain(70, 70);

        systemeRoutier = new SystemeRoutier(terrain,scene);


        //creer un terrain
        Model terrainModel = ModelLoader.loadModel("model-terrain", "ressources/models/terrain/Terrain.obj", scene.getTextureCache());
        scene.ajouterModel(terrainModel);
        Entite terrainEntite = new Entite("entite-terrain", terrainModel.getId());
        scene.ajouterEntite(terrainEntite);

        Model cubeModel = ModelLoader.loadModel("cube-model","ressources/models/arret/Arret.obj",scene.getTextureCache());
        scene.ajouterModel(cubeModel);

        //creer la liste des voitures du jeu
        voitures = new ArrayList<>();

        //pour le moment, la carte est créé à partir de cube, cela va changer plus tard
        final int TAILLECARTE = 30;


        //tester le skin de voiture
        Model modelVoiture = ModelLoader.loadModel("voiture-model", "ressources/models/camion/camion.obj", scene.getTextureCache());
        scene.ajouterModel(modelVoiture);
        //créer la voiture
        Voiture voiture = new Voiture("voiture-entite", modelVoiture.getId());
        scene.ajouterVoiture(voiture);
        voiture.setTaille(0.5f);

        //fausse rue
        Route route = new Route(new Vector2f(0,0),scene);
        route.ajouterSegment(new Vector2f(10,0),scene);
        route.ajouterSegment(new Vector2f(10,10),scene);

        voiture.setRouteActuelle(route);


        scene.getCamera().monter(5);
        scene.getCamera().reculer(20);
    }

    /**
     * Méthode lancée chaque boucle du jeu et qui sert à recevoir les entrées de l'utilisateur
     *
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
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_SPACE))
            camera.monter(mouvement);
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_LEFT_SHIFT))
            camera.descendre(mouvement);

        //créer des raccourcis clavier
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_7))
            systemeRoutier.setModeUtilisateur(Mode.CONSTRUCTEURDEROUTE);
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_8)) {
            systemeRoutier.setModeUtilisateur(Mode.PLACEURINTERSECTION);
        }
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_9))
            systemeRoutier.setModeUtilisateur(Mode.CUSTOMIZERINTERSECTION);
        if (fenetre.isToucheAppuye(GLFW.GLFW_KEY_0))
            systemeRoutier.setModeUtilisateur(Mode.SPECTATEUR);

        camera.getPosition().set(new Vector3f(MathLocal.clamp(camera.getPosition().x,-terrain.getLargeur()/2.0f,terrain.getLargeur()/2.0f),
                MathLocal.clamp(camera.getPosition().y,0.5f,30),MathLocal.clamp(camera.getPosition().z,-terrain.getHauteur()/2.0f,terrain.getHauteur()/2.0f)));
        EntreSouris entreSouris = fenetre.getEntreSouris();
        //regarder pour bouger
        if (entreSouris.isBoutonDroitPresse()) {
            Vector2f vectDisp = entreSouris.getVectDisp();
            camera.rotationner((float) Math.toRadians(-vectDisp.x * SENSIBILITE),
                    (float) Math.toRadians(-vectDisp.y * SENSIBILITE));
        }

        //ici, avant de lui permettre d'intéragir, on regarde s'il est spectateur;
        //regarder si on selectionne quelque chose
        if (entreSouris.isBoutonGauchePresse() && System.currentTimeMillis() > tempsActuel + 500) {
            tempsActuel = System.currentTimeMillis();

            systemeRoutier.interagir(fenetre,entreSouris);
        }
    }

    /**
     * Méthode lancée chaque rafraichissement de l'écran
     *
     * @param fenetre         ref de la fenetre
     * @param scene           ref de la scène
     * @param diffTempsMillis
     */
    @Override
    public void miseAJour(Fenetre fenetre, Scene scene, long diffTempsMillis) {

        for (Voiture voiture : voitures) {
            voiture.mettreAJourVoiture();


        }
    }

}


