package jeu;

import Outil.CouleurConsole;
import Outil.MathLocal;
import moteur.scene.Entite;
import org.joml.Vector2f;

import java.util.*;

import static java.lang.Math.*;


public class Voiture extends Entite {

    //tous les déplacement des voitures se feront en 2D
    private float vitesse;
    private float acceleration;

    private int masse;

    private Vector2f pointAAller;

    private Vector2f positionLocale;

    private int indexRoute;

    private float angle;

    private Route routeActuelle;

    private Route prochaineRoute;

    private Maison maisonAAller;

    private Maison maisonDepart;

    private float distanceEntrePoint;

    private int sens;

    Stack<Route> chemin;

    SystemeRoutier gps;


    /**
     *
     * @param id id de l'entite
     * @param idModel id du model
     * @param maisonDepart maison de depart de la voiture
     * @param maisonAAller maison d'arrivé de la voiture
     * @param gps reference au systeme routier
     */
    public Voiture(String id, String idModel,Maison maisonDepart, Maison maisonAAller, SystemeRoutier gps) {
        super(id, idModel);

        //définir la vitesse de base et l'accélération
        vitesse = 2;
        acceleration = 0;
        distanceEntrePoint = 0;

        //plus tard
        indexRoute = 0;

        //masse de la voiture
        masse = 3500;

        //définir la maison de depart et celle où aller
        this.maisonDepart = maisonDepart;
        this.maisonAAller = maisonAAller;

        //définir le chemin à parcourir
        chemin = gps.getChemin(maisonDepart,maisonAAller);

        //mettre la route actuelle
        try {
            routeActuelle = chemin.pop();
            prochaineRoute = chemin.pop();
        } catch (EmptyStackException e) {
            routeActuelle = maisonDepart.getRouteReliee();
        }
        //définir le sens initiale que la voiture va
        this.sens = this.maisonDepart.getSensRouteLiee();




        //mettre le point à aller initiale
        if (sens == -1) {
            pointAAller = routeActuelle.getPointsRouteInv().get(2);
            setPosition(routeActuelle.getPointsRouteInv().get(0).x,0.012f,routeActuelle.getPointsRouteInv().get(0).y);
        }

        else {
            pointAAller = routeActuelle.getPointsRoute().get(2);
            setPosition(routeActuelle.getPointsRoute().get(0).x,0.012f,routeActuelle.getPointsRoute().get(0).y);
        }


        //mettre la position initiale de la voiture
        positionLocale = new Vector2f(getPosition().x, getPosition().z);
        //mettre l'angle
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
        float x = pointAAller.x - positionLocale.x;
        float y = pointAAller.y - positionLocale.y;
        float angle = (float) abs(atan(y/x));
        if (x < 0 && y < 0)
            angle += PI;
        else if (x < 0 && y > 0)
            angle = (float)(PI-angle);
        else if (x > 0 && y < 0)
            angle = (float)(2*PI-angle);

        return angle - this.angle;
    }

    public Vector2f getProchainPoint(double multiplicateur) {
        return new Vector2f((float) ((vitesse*multiplicateur)*cos(angle)), (float) ((vitesse*multiplicateur)*sin(angle)));
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
        vitesse += acceleration*temps;
        //setPositionLocale(getPositionLocale().x + getProchainPoint(temps).x,getPositionLocale().y + getProchainPoint(temps).y);
        try {
            //faire avancer la voiture



        } catch (Exception e) {

            //réagir lorsqu'on doit passer par une intersection (lorsqu'on finit la route)
            if (sens == -1) {
                if (routeActuelle.getIntersectionDepart() == prochaineRoute.getIntersectionDepart()) {
                    sens*=-1;
                }
            }
            else {
                if (routeActuelle.getIntersectionFin() == prochaineRoute.getIntersectionFin()) {
                    sens*=-1;
                }
            }
            routeActuelle = prochaineRoute;
            prochaineRoute = chemin.pop();


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
        pointAAller = routeActuelle.getPremierPoint();
    }

    public ArrayList<Vector2f> getPointRoute() {
        return routeActuelle.getPointsRoute();
    }
    public ArrayList<Vector2f> getPointRouteInverse() {
        ArrayList<Vector2f> arrayInverse = new ArrayList<>(routeActuelle.getPointsRoute());
        Collections.reverse(arrayInverse);
        return arrayInverse;
    }

    public Route getRouteActuelle() {
        return routeActuelle;
    }

    public static String genererNom() {
        return "Voiture #" + System.currentTimeMillis();
    }

    public Maison getMaisonDepart() {
        return maisonDepart;
    }

}
