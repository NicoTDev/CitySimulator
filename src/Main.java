import moteur.*;
import moteur.Graphique.Mesh;
import moteur.Graphique.Model;
import moteur.Graphique.Rendu;
import moteur.scene.Entite;
import moteur.scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Main à lancer au démarrage
 */
public class Main implements ILogiqueJeu {


    private Entite cubeEntity;

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
    }

    /**
     * Méthode lancée chaque boucle du jeu et qui sert à recevoir les entrées de l'utilisateur
     * @param fenetre
     * @param scene
     * @param diffTempsMillis
     * @param inputConsomme
     */
    @Override
    public void entree(Fenetre fenetre, Scene scene, long diffTempsMillis, boolean inputConsomme) {

    }

    /**
     * Méthode lancée chaque
     * @param fenetre
     * @param scene
     * @param diffTempsMillis
     */
    @Override
    public void miseAJour(Fenetre fenetre, Scene scene, long diffTempsMillis) {

    }
}

