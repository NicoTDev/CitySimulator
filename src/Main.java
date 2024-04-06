import Outil.CouleurConsole;
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



    private static final float VITESSE = 0.010f;

    private static final float SENSIBILITE = 0.05f;

    private List<Voiture> voitures;

    public int rotation;

    public Mode modeUtilisateur;

    Route routeEnConstruction;

    boolean isRouteEnCours;



    Model arbreModel;

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
        terrain.genererTerrain(70,70);


        //creer un terrain
        Model terrainModel = ModelLoader.loadModel("model-terrain","ressources/models/terrain/Terrain.obj",scene.getTextureCache());
        scene.ajouterModel(terrainModel);
        Entite terrainEntite = new Entite("entite-terrain",terrainModel.getId());
        scene.ajouterEntite(terrainEntite,terrainEntite.getClass());

        //creer la liste des voitures du jeu
        voitures = new ArrayList<>();

        //pour le moment, la carte est créé à partir de cube, cela va changer plus tard
        final int TAILLECARTE = 30;


        //tester le skin de voiture
        Model modelVoiture = ModelLoader.loadModel("voiture-model", "ressources/models/camion/camion.obj", scene.getTextureCache());
        scene.ajouterModel(modelVoiture);
        //créer la voiture
        Voiture voiture = new Voiture("voiture-entite",modelVoiture.getId());
        scene.ajouterEntite(voiture,voiture.getClass());

        //Skybox
        //Skybox skybox = new Skybox("ressources/models/skybox/skybox.obj", scene.getTextureCache());
        //skybox.getEntiteSkybox().setTaille(50);
        //scene.setSkybox(skybox);

        scene.getCamera().monter(5);
        scene.getCamera().reculer(20);
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
        //regarder pour bouger
        if (entreSouris.isBoutonDroitPresse()) {
            Vector2f vectDisp = entreSouris.getVectDisp();
            camera.rotationner((float) Math.toRadians(-vectDisp.x*SENSIBILITE),
                    (float) Math.toRadians(-vectDisp.y*SENSIBILITE));
        }

        //ici, avant de lui permettre d'intéragir, on regarde s'il est spectateur;
        if (!modeUtilisateur.equals(Mode.SPECTATEUR)) {
        //regarder si on selectionne quelque chose
            if (entreSouris.isBoutonGauchePresse() && System.currentTimeMillis() > tempsActuel+100) {
                tempsActuel = System.currentTimeMillis();

                switch (modeUtilisateur) {

                    case CONSTRUCTEURDEROUTE -> {
                        placerPointRoute(fenetre,scene,entreSouris.getPositionActuelle());
                    }

                    case PLACEURINTERSECTION -> {}
                    case CUSTOMIZERINTERSECTION -> {}
                }
            }


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
            voiture.mettreAJourVoiture();
            //if (voiture.getRotation().y > 2*PI)
            //    voiture.setRotation(0,1,0,0);
            //if (rotation == 360)
            //    rotation = 0;
            //voiture.setRotation(0,1,0,(float) Math.toRadians(rotation++));
            //voiture.mettreAJourVoiture();



            //System.out.println("Angle y : " + voiture.getRotation().y + " Angle : " + voiture.getRotation().angle());

        }
    }

    public void selectionnerEntite(Fenetre fenetre, Scene scene, Vector2f pos) {

        int largeurFenetre = fenetre.getLargeur();
        int hauteurFenetre = fenetre.getHauteur();

        //on normalise les coordonnées
        float x = (2 * pos.x) / largeurFenetre - 1.0f;
        float y = 1.0f - (2 * pos.y) / hauteurFenetre;
        float z = -1.0f;

        //faire l'inverse de la technique que l'on a fait pour generer les objets en multipliant cette fois les matrices inverse
        //de la projection et de la matriceVue pour lancer un "ray"
        Matrix4f invProjMatrix = scene.getProjection().getMatriceProjectionInverse();
        Vector4f mouseDir = new Vector4f(x, y, z, 1.0f);
        mouseDir.mul(invProjMatrix);
        mouseDir.z = -1.0f;
        mouseDir.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getMatriceVueInverse();
        mouseDir.mul(invViewMatrix);

        Vector4f min = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector4f max = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        Vector2f nearFar = new Vector2f();

        Entite entiteSelectionnee = null;
        float plusPetiteDistance = Float.POSITIVE_INFINITY;
        Vector3f centre = scene.getCamera().getPosition();

        Collection<Model> models = scene.getDicoModel().values();
        Matrix4f modelMatrix = new Matrix4f();
        for (Model model : models) {
            List<Entite> entities = model.getEntites();

            //pour chaque Entite sur la map, on regarde si le ray traverse l'objet
            for (Entite entite : entities) {

                modelMatrix.translate(entite.getPosition()).scale(entite.getTaille());
                for (Material material : model.getMateriaux()) {
                    //pour chaque mesh de l'entite
                    for (Mesh mesh : material.getMeshList()) {

                        //on get les aabb réel de l'objet
                        Vector3f aabbMin = new Vector3f(-100,0,-100);
                        min.set(aabbMin.x, aabbMin.y, aabbMin.z, 1.0f);
                        min.mul(modelMatrix);
                        Vector3f aabbMax = new Vector3f(100,1,100);
                        max.set(aabbMax.x, aabbMax.y, aabbMax.z, 1.0f);
                        max.mul(modelMatrix);

                        //si le rayon traverse l'objet, on selectionne l'entite
                        if (Intersectionf.intersectRayAab(centre.x, centre.y, centre.z, mouseDir.x, mouseDir.y, mouseDir.z,
                                min.x, min.y, min.z, max.x, max.y, max.z, nearFar) && nearFar.x < plusPetiteDistance) {
                            plusPetiteDistance = nearFar.x;
                            entiteSelectionnee = entite;
                        }
                    }
                }
                modelMatrix.identity();
            }
        }
        //System.out.println( entiteSelectionnee == null ? entiteSelectionnee : CouleurConsole.BLEU.couleur + entiteSelectionnee);
    }


    public Vector4f getDirectionSouris(Fenetre fenetre, Scene scene, Vector2f positionSouris) {
        int largeurFenetre = fenetre.getLargeur();
        int hauteurFenetre = fenetre.getHauteur();

        //on normalise les coordonnées
        float x = (2 * positionSouris.x) / largeurFenetre - 1.0f;
        float y = 1.0f - (2 * positionSouris.y) / hauteurFenetre;
        float z = -1.0f;

        //faire l'inverse de la technique que l'on a fait pour generer les objets en multipliant cette fois les matrices inverse
        //de la projection et de la matriceVue pour lancer un "ray"
        Matrix4f invProjMatrix = scene.getProjection().getMatriceProjectionInverse();

        //créer la direction du rayon
        Vector4f directionSouris = new Vector4f(x, y, z, 1.0f);
        directionSouris.mul(invProjMatrix);
        directionSouris.z = -1.0f;
        directionSouris.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getMatriceVueInverse();
        directionSouris.mul(invViewMatrix);

        return directionSouris;
    }
    public void placerPointRoute(Fenetre fenetre, Scene scene, Vector2f pos) {

        Vector4f directionSouris = getDirectionSouris(fenetre,scene,pos);

        //ici on initialise les valeurs de minimum et de maximum du terrain
        Vector4f min = new Vector4f(-Math.round((terrain.getLargeur()-1) / 2.0f ), 0.0f, -Math.round( (terrain.getHauteur()-1) / 2.0f) ,1.0f);
        Vector4f max = new Vector4f(Math.round((terrain.getLargeur()-1) / 2.0f ), 0.1f, Math.round( (terrain.getHauteur()-1) / 2.0f) ,1.0f);
        Vector2f t = new Vector2f();

        float plusPetiteDistance = Float.POSITIVE_INFINITY;
        Vector3f centre = scene.getCamera().getPosition();

        //si le rayon traverse l'objet, on selectionne l'entite
        if (Intersectionf.intersectRayAab(centre.x, centre.y, centre.z, directionSouris.x, directionSouris.y, directionSouris.z,
                min.x, min.y, min.z, max.x, max.y, max.z, t) && t.x < plusPetiteDistance) {
            plusPetiteDistance = t.x;

            Vector3f point = new Vector3f(directionSouris.x, directionSouris.y, directionSouris.z).mul(plusPetiteDistance).add(centre);

            //soit créer une route, soit ajouter un segment si cette route n'existe pas
            if (isRouteEnCours) {
                routeEnConstruction.ajouterSegment(new Vector2f(point.x,point.z),scene);
            }
            else {
                routeEnConstruction = new Route(new Vector2f(point.x, point.z), scene);
                isRouteEnCours = true;
            }


            //System.out.println( plusPetiteDistance == Float.POSITIVE_INFINITY ? plusPetiteDistance : CouleurConsole.BLEU.couleur + plusPetiteDistance);
        }

    }

}


