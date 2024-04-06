package moteur;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Classe responsable de surveiller les entrées de souris de l'utilisateur
 */
public class EntreSouris {


    private boolean isBoutonGauchePresse;
    private boolean isBoutonDroitPresse;
    private boolean isDansLaFenetre;
    private Vector2f positionActuelle;
    private Vector2f positionPrecedente;
    private Vector2f vectDisp;


    public EntreSouris(long refFenetre) {

        positionPrecedente = new Vector2f(-1,-1);
        positionActuelle = new Vector2f();
        vectDisp = new Vector2f();
        isDansLaFenetre = false;
        isBoutonDroitPresse = false;
        isBoutonGauchePresse = false;

        //observer la position du curseur
        glfwSetCursorPosCallback(refFenetre,(ref,x,y) -> {
            positionActuelle.x = (float) x;
            positionActuelle.y = (float) y;
        });

        glfwSetCursorEnterCallback(refFenetre, (ref,isCorrect) -> {
            isDansLaFenetre = isCorrect;
        });

        glfwSetMouseButtonCallback(refFenetre, (ref, bouton, action, mode) -> {
            isBoutonGauchePresse = (bouton == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS);
            isBoutonDroitPresse = (bouton == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS);
        });
    }

    public void entree() {
        vectDisp.x = 0;
        vectDisp.y = 0;

        if (positionPrecedente.x > 0 && positionPrecedente.y > 0 && isDansLaFenetre) {
            double delx = positionActuelle.x - positionPrecedente.x;
            double dely = positionActuelle.y - positionPrecedente.y;

            boolean rotationx = delx != 0;
            boolean rotationy = dely != 0;

            if (rotationx)
                vectDisp.y = (float) delx;
            if (rotationy)
                vectDisp.x = (float) dely;


        }

        positionPrecedente.x = positionActuelle.x;
        positionPrecedente.y = positionActuelle.y;
    }

    public boolean isBoutonDroitPresse() {
        return isBoutonDroitPresse;
    }

    public boolean isBoutonGauchePresse() {
        return isBoutonGauchePresse;
    }

    public Vector2f getVectDisp() {
        return vectDisp;
    }

    public Vector2f getPositionActuelle() {
        return positionActuelle;
    }
}
