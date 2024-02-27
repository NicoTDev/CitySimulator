package moteur;

import moteur.Graphique.Rendu;
import moteur.scene.Scene;

/**
 * Interface servant à implementer la logique du jeu
 */
public interface ILogiqueJeu {

    void detruireProgramme();
    void initialisation(Fenetre fenetre, Scene scene, Rendu rendu);
    void entree(Fenetre fenetre, Scene scene, long diffTempsMillis);
    void miseAJour(Fenetre fenetre, Scene scene, long diffTempsMillis);


}
