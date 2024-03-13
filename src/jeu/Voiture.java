package jeu;

import Outil.CouleurConsole;
import moteur.scene.Entite;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static java.lang.Math.*;


public class Voiture extends Entite {

    //tous les déplacement des voitures se feront en 2D
    float vitesse;
    float acceleration;

    int masse;

    Vector2f pointAAller;

    Route routeActuelle;

    int indexDeRoute;


    public Voiture(String id, String idModel) {
        super(id, idModel);
        vitesse = 0;
        acceleration = 0;
        masse = 3500;
        pointAAller = new Vector2f(0,10);
        indexDeRoute = 0;

    }

    public float getAngle() {
        return getRotation().angle();
    }

    public float getAnglePointRecherche() {
        float y = pointAAller.y - getPosition().z;
        float x = pointAAller.x - getPosition().x;
        float angle = (float) abs(atan(y/x));
        if (x < 0 && y < 0)
            angle += PI;
        else if (x < 0 && y > 0)
            angle = (float)(PI-angle);
        else if (x > 0 && y < 0)
            angle = (float)(2*PI-angle);
        //System.out.println(CouleurConsole.BLEU.couleur + "x : " + x + "\ny : " + y + "\nangle : " + angle);
        return angle;
    }
    //A surement supprime
    public void faireFace() {
        //setRotation(0,1,0,getAnglePointRecherche());
        System.out.println(getRotation().angle());
    }

    public Vector2f getProchainPoint() {
        return new Vector2f(new float[]{(float)(vitesse*sin(getAngle())),-(float)(vitesse*cos(getAngle()))});
    }

    public void mettreAJourVoiture() {
        //mettre à jour la localisation
        Vector2f p = getProchainPoint();
        double distancePtVoiture = sqrt(Math.pow(pointAAller.x-getPosition().x,2)+Math.pow(pointAAller.y-getPosition().z,2));
        //System.out.println(CouleurConsole.BLEU.couleur + getPosition().x + " = x   " + getPosition().z + " = y");
        if (distancePtVoiture > 1) {
            faireFace();
            setPosition(getPosition().x + p.x, getPosition().y, getPosition().z + p.y);
            vitesse += acceleration;
        }
    }

    public void avancer() {
        Vector2f p = getProchainPoint();
        faireFace();
        setPosition(getPosition().x + p.x, getPosition().y, getPosition().z + p.y);
    }

    public void setVitesse(float vitesse) {
        this.vitesse = vitesse;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    //getter de force ou d'énergie
    public float getEnergieCinetique() {
        return (float) (1/2*masse*Math.pow(vitesse,2));
    }

    public float getForceGravitationelle() {
        return masse*9.81f;
    }
    public float getForceNormale() {
        return -getForceGravitationelle();
    }
    //pour faire ceci, il faut avoir accès au coefficient de frottement de la route
    public float getForceFrottement() {
        return 0.0f;
    }


}
