import Outil.MathLocal;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
import jeu.*;
import moteur.Graphique.Terrain;
import moteur.*;
import moteur.Graphique.*;
import moteur.scene.Camera;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import javax.swing.text.MaskFormatter;
import java.lang.Math;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Classe Main à lancer au démarrage
 */
public class Main implements ILogiqueJeu, ILogiqueGui {

    private static final float VITESSE = 0.025f;

    private static final float SENSIBILITE = 0.05f;

    boolean isEnCours;

    SystemeRoutier systemeRoutier;

    Terrain terrain;

    Gui gui;

    Entite skyBox;

    Model modelVoiture;
    Scene scene;

    long tempsActuel = System.currentTimeMillis();

    double deltaPrecedent;


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

        this.scene = scene;
        //créer la skybox
        Model skyboxModel = ModelLoader.loadModel("skybox-model","ressources/models/skybox/skybox.obj",scene.getTextureCache());
        skyBox = new Entite("skybox-entite",skyboxModel.getId());
        skyBox.setTaille(200);
        skyBox.setPosition(0,-50,0);
        scene.ajouterModel(skyboxModel);
        scene.ajouterEntite(skyBox);

        //mettre les modes par défaut

        //importer les models dans le jeu
        Model arretModel = ModelLoader.loadModel("arret-model",Arret.CHEMINOBJET,scene.getTextureCache());
        scene.ajouterModel(arretModel);
        Model lumiereModel = ModelLoader.loadModel("lumiere-model",Lumiere.CHEMINOBJET,scene.getTextureCache());
        scene.ajouterModel(lumiereModel);



        //creer un terrain
        terrain = new Terrain();
        terrain.genererTerrain(70, 70);

        systemeRoutier = new SystemeRoutier(terrain,scene, fenetre);


        //creer un terrain
        Model terrainModel = ModelLoader.loadModel("model-terrain", "ressources/models/terrain/Terrain.obj", scene.getTextureCache());
        scene.ajouterModel(terrainModel);
        Entite terrainEntite = new Entite("entite-terrain", terrainModel.getId());
        scene.ajouterEntite(terrainEntite);

        Model cubeModel = ModelLoader.loadModel("arret-model","ressources/models/arret/Arret.obj",scene.getTextureCache());
        scene.ajouterModel(cubeModel);

        //pour le moment, la carte est créé à partir de cube, cela va changer plus tard
        final int TAILLECARTE = 30;


        //tester le skin de voiture
        modelVoiture = ModelLoader.loadModel("voiture-model", "ressources/models/camion/camion.obj", scene.getTextureCache());
        scene.ajouterModel(modelVoiture);
        //créer la voiture

        //importer le cube
        Model modelCube = ModelLoader.loadModel("cube-model","ressources/models/cube/cube.obj", scene.getTextureCache());
        scene.ajouterModel(modelCube);



        Model maisonModel = ModelLoader.loadModel("maison-model","ressources/models/maison/maison.obj",scene.getTextureCache());
        scene.ajouterModel(maisonModel);


        //générer des maison au depart
        //(int)(Math.random()*5+5)
        for (int i = 0 ; i < 3 ; i++) {
            Maison maisonLocal = new Maison("maison-"+i,maisonModel.getId(),(float)Math.toRadians((float)(Math.random()*360)),scene,i+1);
            boolean positionCorrect;
            int precision = 0;
            do {
                positionCorrect = true;
                maisonLocal.setPosition((float) (Math.random() * 60 - 30), 0.012f, (float) (Math.random() * 60 - 30));
                for (Maison maison : scene.getMaisons()) {

                    if (Math.abs(maison.getPosition().distance(maisonLocal.getPosition())) < 15  - (precision/100)) {
                        positionCorrect = false;
                        precision++;
                    }
                }
            } while (!positionCorrect);
            scene.ajouterMaison(maisonLocal);
        }

        scene.getCamera().monter(5);
        scene.getCamera().reculer(20);

        gui = new Gui(scene,systemeRoutier);

        scene.setGuiLogique(this);


    }

    /**
     * Méthode lancée chaque boucle du jeu et qui sert à recevoir les entrées de l'utilisateur
     *
     * @param fenetre
     * @param scene
     * @param diffTempsMillis
     * @param entreUtilise
     */
    @Override
    public void entree(Fenetre fenetre, Scene scene, long diffTempsMillis, boolean entreUtilise) {

        //si ImGui a déjà utilisé cette entrée, ne rien faire.
        if (entreUtilise) {
            return;
        }

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

            systemeRoutier.interagir(entreSouris);
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
        //si la simulation est lancé, on exécute la boucle de jeu.
        if (gui.isEnCours()) {

            try {
                while (scene.getVoitures().size() < 5) {
                    Maison maisonDepart = scene.getMaisons().get((int)(Math.random()*scene.getMaisons().size()));
                    Maison maisonArrive;
                    do {
                        maisonArrive = scene.getMaisons().get((int)(Math.random()*scene.getMaisons().size()));
                    } while (maisonDepart == maisonArrive);
                    Voiture voiture = new Voiture(Voiture.genererNom(), "voiture-model", maisonDepart, maisonArrive, systemeRoutier);
                    voiture.setTaille(0.5f);
                    scene.ajouterVoiture(voiture);
                }
            }
            catch (NullPointerException e) {
                systemeRoutier.setModeUtilisateur(Mode.CONSTRUCTEURDEROUTE);
                gui.setMessageErreur("Toutes les maisons ne sont pas liées!");
                gui.setEnCours(false);
            }
            catch (IllegalArgumentException e) {
                systemeRoutier.setModeUtilisateur(Mode.CONSTRUCTEURDEROUTE);
                gui.setMessageErreur("Certaines maisons ne sont pas rejoignables");
                gui.setEnCours(false);
            }

            //game loop
            double multiplicateur;
            //prendre le temps actuel
            double deltaActuel = System.currentTimeMillis();
            //trouver le temps écoulé
            double diffDelta = deltaActuel - deltaPrecedent;

            //mettre la valeur du delta actuel au précedent
            deltaPrecedent = deltaActuel;
            for (Voiture voiture : scene.getVoitures()) {
                if (diffDelta / 1000 < 1) {
                    if(voiture.mettreAJourVoiture(diffDelta / 1000))
                    {
                        //scene.getVoitures().remove(voiture);
                        modelVoiture.getEntites().remove(voiture);
                    }
                }
            }

            //enlever les
            scene.setVoitures((ArrayList<Voiture>) scene.getVoitures().stream().filter(n->!n.isDoitEtreDetruite()).collect(Collectors.toList()));
        }

        //Sinon, on fait la boucle d'engine
        else {
            scene.setVoitures(new ArrayList<>());
            modelVoiture.setEntites(new ArrayList<>());
        }
    }

    @Override
    public void dessinerGui() {
        gui.rendre();
    }

    @Override
    public boolean getCommandeInput(Scene scene, Fenetre fenetre) {
        ImGuiIO imGuiIO = ImGui.getIO();
        EntreSouris entreSouris = fenetre.getEntreSouris();
        Vector2f mousePos = entreSouris.getPositionActuelle();
        imGuiIO.setMousePos(mousePos.x, mousePos.y);
        imGuiIO.setMouseDown(0, entreSouris.isBoutonGauchePresse());
        imGuiIO.setMouseDown(1, entreSouris.isBoutonDroitPresse());
        return (imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard());
    }


}


