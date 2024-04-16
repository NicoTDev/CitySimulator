package jeu;

import Outil.CouleurConsole;
import Outil.MathLocal;
import moteur.scene.Entite;
import org.joml.Vector2f;

import java.util.ArrayList;

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

    int sens;


    /**
     * @param id id de l'entite de la voiture
     * @param idModel id du model de la voiture
     * @param routeActuelle route de depart de la voiture
     * @param sens sens dans laquelle il roule
     */
    public Voiture(String id, String idModel, Route routeActuelle,int sens) {
        super(id, idModel);
        vitesse = 1;
        this.sens = sens;
        acceleration = 0;
        masse = 3500;
        indexDeRoute = 0;
        this.routeActuelle = routeActuelle;
        pointAAller = routeActuelle.getPointsRoute().get(1);
        positionLocale = new Vector2f(getPosition().x, getPosition().z);
        angle = getAjustementAngle();
        setAngle(angle);
    }

    /**
     * get l'angle de la voiture
     * @return angle
     */
    public float getAngle() {
        return getRotation().angle();
    }


    /**
     * set l'angle de la voiture
     * @param angle nouvelle angle
     */
    public void setAngle(float angle) {
        setRotation(0,1,0,angle);
        this.angle = angle;
    }

    /**
     * mettre à jour l'angle de la voiture en fonction du nouveau point à aller
     * @return
     */
    public float getAjustementAngle() {
        System.out.println(CouleurConsole.BLEU.couleur + "a : " + angle + " a : " + max(min(angle-getAngle(),(float) Math.toRadians(10)),(float) Math.toRadians(-10)));

        float x = pointAAller.x - positionLocale.x;
        float y = pointAAller.y - positionLocale.y;
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

    public void mettreAJourVoiture(double temps) {

        setPositionLocale(pointAAller.x,pointAAller.y);
        try {
            pointAAller = routeActuelle.getPointsRoute().get(routeActuelle.getPointsRoute().indexOf(pointAAller)+1);
            setAngle(getAjustementAngle());
        } catch (Exception e) {
            pointAAller = routeActuelle.getPointsRoute().get(0);
        }
    }

    public Vector2f getPositionLocale() {
        return positionLocale;
    }




    //DEV------------------------------
    public void setPointAAller(float x, float y) {
        pointAAller.x = x;
        pointAAller.y = y;
    }

    public void setAngleSpawn() {
        int indexPointAAller = routeActuelle.getPointsRoute().indexOf(pointAAller);
        ArrayList<Vector2f> pts = routeActuelle.getPointsRoute();
        //trouvez le vecteur direction
        Vector2f vecteurDirection = new Vector2f(pts.get(indexPointAAller+1)).sub(pointAAller).normalize();
        float angle = new Vector2f(1,0).angle(vecteurDirection);
        vecteurDirection.angle(new Vector2f().zero());
        this.setAngle(angle);
    }

    public void getVecteurDerapage() {}


    public void setRouteActuelle(Route routeActuelle) {
        this.routeActuelle = routeActuelle;
    }

}
