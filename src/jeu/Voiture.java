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

    Vector2f positionLocale;

    float angle;

    Route routeActuelle;

    int indexDeRoute;


    public Voiture(String id, String idModel) {
        super(id, idModel);
        vitesse = 0;
        acceleration = 0;
        masse = 3500;
        pointAAller = new Vector2f(-10,12);
        positionLocale = new Vector2f(getPosition().x, getPosition().z);
        angle = getRotation().angle();
        indexDeRoute = 0;
    }

    public float getAngle() {
        return getRotation().angle();
    }


    public void setAngle(float angle) {
        setRotation(0,1,0,angle);
        this.angle = angle;
    }

    public float actualiserAngle() {
        float x = pointAAller.x - positionLocale.x;
        float y = pointAAller.y - positionLocale.y;
        float angle = (float) abs(atan(y/x));
        if (x < 0 && y < 0)
            angle += PI;
        else if (x < 0 && y > 0)
            angle = (float)(PI-angle);
        else if (x > 0 && y < 0)
            angle = (float)(2*PI-angle);
        System.out.println(CouleurConsole.BLEU.couleur + "a : " + angle + " a : " + max(min(angle-getAngle(),(float) Math.toRadians(10)),(float) Math.toRadians(-10)));
        return max(min(angle-getAngle(),(float) Math.toRadians(2)),(float) Math.toRadians(-2));
    }

    public Vector2f getProchainPoint() {
        return new Vector2f((float) (vitesse*cos(angle)), (float) (vitesse*sin(angle)));
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

    public float getForceFrottementMax() {
        return routeActuelle.COEFFICIENT_FROTTEMENT*getForceNormale();
    }

    public void setPositionLocale(float x, float y) {
        setPosition(x,getPosition().y,y);
        positionLocale.x = x;
        positionLocale.y = y;
    }

    public void mettreAJourVoiture() {
        //trouvez l'angle à avoir
        float angleAAjouter = actualiserAngle();
        setAngle(getAngle()+angleAAjouter);
        //mettre à jour la position
        if (getDistanceDestinationPoint() > 1) {
            Vector2f positionsAdd = getProchainPoint();
            Vector2f getVecteurDerapage;
            setPositionLocale(getPositionLocale().x + positionsAdd.x, getPositionLocale().y + positionsAdd.y);
        }
    }

    public Vector2f getPositionLocale() {
        return positionLocale;
    }




    //DEV------------------------------

    public float getDistanceDestinationPoint() {
        return (float) sqrt(Math.pow(pointAAller.x-positionLocale.x,2)+Math.pow(pointAAller.y-positionLocale.y,2));
    }
    public void setPointAAller(float x, float y) {
        pointAAller.x = x;
        pointAAller.y = y;
    }

    public void getVecteurDerapage() {}



}
