package jeu;

import Outil.CouleurConsole;
import Outil.MathLocal;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;

import static java.lang.Math.*;


public class Voiture extends Entite {

    //tous les déplacement des voitures se feront en 2D
    private float vitesse;
    private float acceleration;

    private int masse;

    private Vector2f pointAAller;

    private Vector2f pointBase;

    private Vector2f positionLocale;

    private boolean doitEtreArret;

    private int indexRoute;

    private float angle;

    private Route routeActuelle;

    private Maison maisonAAller;

    private Maison maisonDepart;

    private float distanceEntrePoint;

    private int sens;

    ArrayList<Route> chemin;

    private float vitesseMaximale;

    boolean doitEtreDetruite;

    SystemeRoutier gps;

    Scene scene;

    Signalisation signalisation;

    private ArrayList<Vector2f> pointsRoute;


    /**
     *
     * @param id id de l'entite
     * @param idModel id du model
     * @param maisonDepart maison de depart de la voiture
     * @param maisonAAller maison d'arrivé de la voiture
     * @param gps reference au systeme routier
     */
    public Voiture(String id, String idModel, Maison maisonDepart, Maison maisonAAller, SystemeRoutier gps, Scene scene) {
        super(id, idModel);
        this.gps = gps;
        //définir la vitesse de base et l'accélération
        vitesse = 0;
        vitesseMaximale = gps.getVitesseRoulee();
        acceleration = 0.1f;
        distanceEntrePoint = 0;
        this.scene = scene;

        //plus tard
        indexRoute = 0;

        //masse de la voiture
        masse = 3500;

        //définir la maison de depart et celle où aller
        this.maisonDepart = maisonDepart;
        this.maisonAAller = maisonAAller;

        this.gps = gps;
        //définir le chemin à parcourir
        chemin = this.gps.getChemin(maisonDepart,maisonAAller);


        //mettre la route actuelle
        //définir le sens initiale que la voiture va

        routeActuelle = chemin.get(0);
        routeActuelle.augmenterNombreUtilisation();
        chemin.remove(0);

        this.sens = this.maisonDepart.getSensRouteLiee();
        //mettre la position initiale de la voiture
        setPosition(getPointRoute().get(0).x,0.01f,getPointRoute().get(0).y);

        positionLocale = new Vector2f(getPosition().x, getPosition().z);

        //mettre le point à aller
        distanceEntrePoint = 0;
        pointAAller = getPointRoute().get(2);
        pointBase = getPointRoute().get(0);

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
        float x = pointAAller.x - pointBase.x;
        float y = pointAAller.y - pointBase.y;
        float angle = (float) abs(atan(y/x));
        if (x < 0 && y < 0)
            angle += PI;
        else if (x < 0 && y > 0)
            angle = (float)(PI-angle);
        else if (x > 0 && y < 0)
            angle = (float)(2*PI-angle);

        //System.out.println("Angle à gauche : " + angle + " | Angle à droite : " + ((PI*2-angle)));
        return (angle);
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

    public void setPointsRoute() {
        pointsRoute = (sens == 1) ? routeActuelle.getPointsRoute() : routeActuelle.getPointsRouteInv();
    }

    public boolean mettreAJourVoiture(double temps) {


        if (doitEtreArret) {

            //if (!isVoituresTropProche())
            //    doitEtreArret = false;
            //regarder si on est arrêté à cause d'un stop
            if (signalisation != null) {
                if (signalisation.getClass() == Arret.class) {
                    Arret arret = ((Arret) (signalisation));
                    System.out.println(arret.dicoVoitureDuree);
                    if ((System.currentTimeMillis() - arret.dicoVoitureDuree.get(this) > 3000)) {
                        System.out.println("Trois Secondes!");
                        doitEtreArret = false;
                        arret.retirerVoiture(this);
                        signalisation = null;
                    }
                }
            }
            else{}


            return false;
        }

        while (vitesse < vitesseMaximale)
            vitesse = (float) Math.min(vitesseMaximale,vitesse + (acceleration*temps));
        while (vitesse > vitesseMaximale)
            vitesse = (float) Math.max(vitesseMaximale,vitesse - (acceleration*temps));
        //setPositionLocale(getPositionLocale().x + getProchainPoint(temps).x,getPositionLocale().y + getProchainPoint(temps).y);
        Vector2f dir;
        Vector2f vecteurSensRoute;

        try {

            //mettre à jour la voiture
            while (new Vector2f(pointAAller).distance(pointBase) - distanceEntrePoint < 0.1f) {
                distanceEntrePoint = 0;
                pointBase = pointAAller;
                pointAAller = getPointRoute().get(getPointRoute().indexOf(pointAAller) + 1);
            }
            //mettre à jour
            dir = new Vector2f((new Vector2f(pointAAller).sub(pointBase))).normalize();
            vecteurSensRoute = new Vector2f(-dir.y,dir.x).mul(0.25f);
            //setPositionLocale(pointAAller.x+vecteurSensRoute.x, vpointAAller.y+vecteurSensRoute.y);
            distanceEntrePoint += (float) (temps * vitesse);
            Vector2f nouveauPt = new Vector2f(pointBase).add(new Vector2f((float) (distanceEntrePoint*cos(angle)), (float) (distanceEntrePoint*sin(angle)))).add(new Vector2f(vecteurSensRoute));

            setPositionLocale(nouveauPt.x,nouveauPt.y);
            setAngle(getAjustementAngle());
            //réajuster le point à aller

        } catch (Exception e) {

            pointBase = getPointRoute().get(getPointRoute().size()-1);
            if (chemin.isEmpty()) {
                //sens *= maisonAAller.getSensRouteLiee();
                doitEtreDetruite = true;
            }
            else {
                signalisation = Graph.getIntersectionEntreRoutes(routeActuelle, chemin.get(0)).getSignalisation();
                if (signalisation != null) {
                    if (signalisation.getClass() == Arret.class) {
                        doitEtreArret = true;
                        ((Arret) signalisation).ajouterVoiture(this);
                    }
                }
                //réagir lorsqu'on doit passer par une intersection (lorsqu'on finit la route)
                updateRouteActuelle();
                pointAAller = getPointRoute().get(0);
            }

            //gérer le stop


            //if (isVoituresTropProche()) {
            //    System.out.println("Voiture trop proche");
            //    return false;
            //}


        }

        return doitEtreDetruite;
    }

    public Vector2f getPositionLocale() {
        return positionLocale;
    }


    //DEV------------------------------
    public void setPointAAller(float x, float y) {
        pointAAller.x = x;
        pointAAller.y = y;
    }

    public void getVecteurDerapage() {}


    public void updateRouteActuelle() {

        //mettre le sens correctement
            //trouvez le sens que la voiture aura
        sens *= (chemin.get(0).getIntersectionDepart() == routeActuelle.getIntersectionFin() ||
                chemin.get(0).getIntersectionFin() == routeActuelle.getIntersectionDepart()) ? 1 : -1;
        routeActuelle = chemin.get(0);
        chemin.remove(0);
        routeActuelle.augmenterNombreUtilisation();
        //mettre la route actuelle
    }

    public void setRouteActuelle(Route routeActuelle) {
        this.routeActuelle = routeActuelle;
    }

    public ArrayList<Vector2f> getPointRoute() {
        return (sens == 1) ? routeActuelle.getPointsRoute() : getPointRouteInverse();
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

    public boolean isDoitEtreDetruite() {
        return doitEtreDetruite;
    }

    public boolean isVoituresTropProche() {
        //regarder les collisions
        for (Voiture voiture : scene.getVoitures()) {
            if (voiture.getPositionLocale().distance(this.getPositionLocale()) < 1) {
                System.out.println(voiture.getPositionLocale().distance(this.getPositionLocale()));
                return true;
            }
        }
        return false;

    }

    public void detruireVoiture() {
        doitEtreArret = true;
    }

    public void setVitesseMaximale(float vitesse) {
        vitesseMaximale = vitesse;
    }
}
