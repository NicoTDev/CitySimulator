package moteur;


import moteur.Graphique.Rendu;
import moteur.scene.Scene;

/**
 * Classe responsable de relier toutes les composantes du jeu entre elles (logique, entree, rendu et les scènes)
 */
public class Moteur {


    public static final int TARGET_UPS = 30;

    private final ILogiqueJeu logiqueJeu;

    private final Fenetre fenetre;

    private Rendu rendu;

    private boolean isActif;

    private Scene scene;

    private int targetFps;

    private int targetUps;

    public Moteur(String titreFenetre, Fenetre.optionFenetre opts, ILogiqueJeu logiqueJeu) {
        fenetre = new Fenetre(titreFenetre, opts, () -> {
            redimensionner();
            return null;
        });
        targetFps = opts.fps;
        targetUps = opts.ups;
        this.logiqueJeu = logiqueJeu;
        rendu = new Rendu(fenetre);
        scene = new Scene(fenetre.getLargeur(),fenetre.getHauteur());
        logiqueJeu.initialisation(fenetre,scene,rendu);
        isActif = true;
    }


    private void redimensionner() {
        int largeur = fenetre.getLargeur();
        int hauteur = fenetre.getHauteur();
        scene.redimensionner(largeur, hauteur);
        rendu.redimensionner(largeur, hauteur);
    }

    private void run() {


        long tempsInitial = System.currentTimeMillis();
        float timeU = 1000.0f / targetUps;
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;
        ILogiqueGui logiqueGui = scene.getLogiqueGui();

        long updateTime = tempsInitial;
        while (isActif && !fenetre.isWindowShouldClose()) {

            fenetre.pollEvents();

            long present = System.currentTimeMillis();

            deltaUpdate += (present - tempsInitial) / timeU;
            deltaFps += (present - tempsInitial) / timeR;

            //gérer l'input de l'utilisateur
            if (targetFps <= 0 || deltaFps >= 1) {
                fenetre.getEntreSouris().entree();
                boolean entreeUtilise = logiqueGui != null && logiqueGui.getCommandeInput(scene, fenetre);
                logiqueJeu.entree(fenetre, scene, present - tempsInitial, false);
            }

            //gérer la mise à jour de la logique du jeu
            if (deltaUpdate >= 1) {
                long diffTempsMilli = present - updateTime;
                logiqueJeu.miseAJour(fenetre, scene, diffTempsMilli);
                updateTime = present;
                deltaUpdate--;
            }

            //gérer le rendu graphique du jeu
            if (targetFps <= 0 || deltaFps >= 1) {

                rendu.rendre(fenetre, scene);
                deltaFps--;
                fenetre.mettreAJour();
            }
            tempsInitial = present;
        }
    }

    public void start() {
        isActif = true;
        run();
    }

    public void stop() {isActif = false;}

    public void detruireProgramme() {
        scene.detruireProgramme();
        fenetre.detruireProgramme();
        rendu.detruireProgramme();
    }


}
