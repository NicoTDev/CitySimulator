package jeu;

import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Maison extends Entite {

    private float angle;
    private Route routeReliee;

    private int numero;

    private Scene scene;
    public Maison(String id, String idModel, float angle, Scene scene,int numero) {
        super(id, idModel);
        this.angle = angle;
        this.setRotation(0,1,0,this.angle);
        this.scene = scene;
        routeReliee = null;
        this.numero = numero;

    }

    public Route getRouteReliee() {
        return routeReliee;
    }

    public void setRouteReliee(Route routeReliee) {
        this.routeReliee = routeReliee;
    }

    public Vector2f getPointDevantMaison() {
        return new Vector2f(getPosition().x,getPosition().z).add(new Vector2f(-(float)(sin(angle)),(float)(cos(angle))).mul(2));
    }

    public int getSensRouteLiee() {
        if (routeReliee == null) {
            throw new NullPointerException("La maison n'est liée à aucune route");
        }


        return (routeReliee.getIntersectionDepart().isMaison()) ? 1 : -1;


    }

    public Intersection getIntersectionMaison() {
        return (getSensRouteLiee() == 1) ? routeReliee.getIntersectionDepart() : routeReliee.getIntersectionFin();
    }

    public int getNumero() {
        return numero;
    }
}
