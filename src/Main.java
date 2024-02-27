import moteur.*;
import moteur.Graphique.Mesh;
import moteur.Graphique.Model;
import moteur.Graphique.Rendu;
import moteur.scene.Camera;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Main à lancer au démarrage
 */
public class Main implements ILogiqueJeu {


    private Entite entite;

    private static final float VITESSE = 0.005f;

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
     * Méthode lancée une fois au démarrage du jeu
     * @param fenetre
     * @param scene
     * @param rendu
     */
    @Override
    public void initialisation(Fenetre fenetre, Scene scene, Rendu rendu) {
        float[] positions = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };

        float[] colours = new float[]{
                1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 0.5f, 0.5f,
                1.0f, 1.0f, 1.0f,
                0.0f, 0.5f, 0.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 0.5f, 0.5f,
        };

        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };

        Mesh objet = new Mesh(positions,colours,indices);
        List<Mesh> meshes = new ArrayList<>();
        meshes.add(objet);
        Model model = new Model("cube",meshes);
        scene.ajouterModel(model);
        entite = new Entite("entite-cube","cube");
        scene.ajouterEntite(entite);
        entite.setPosition(0,0,-3);


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

        rotation += 1f;
        if (rotation > 360)
            rotation = 0;

        entite.setRotation(1,0,1, (float) Math.toRadians(rotation));
        entite.setPosition(entite.getPosition().x,(float) (Math.sin(rotation/100)*2),entite.getPosition().z);
        entite.mettreAJour();

    }
}

