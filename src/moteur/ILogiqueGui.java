package moteur;

import moteur.scene.Scene;

public interface ILogiqueGui {
    void dessinerGui();

    boolean getCommandeInput(Scene scene, Fenetre fenetre);
}
