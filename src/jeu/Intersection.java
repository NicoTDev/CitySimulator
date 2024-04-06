package jeu;

import org.joml.Vector2f;

public class Intersection {

    Route[] routesLiee;

    Vector2f position;

    public Intersection(Vector2f position) {
        routesLiee = new Route[4];
        this.position = position;
    }

    /**
     *
     * @param route1 route reliee
     * @param sens (0 = gauche, 1 = haut, 2 = droite, 3 = bas)
     */
    public Intersection(Vector2f position,Route route1, int sens) {
        routesLiee = new Route[4];
        routesLiee[sens] = route1;
        this.position = position;
    }

    public Intersection(Vector2f position,Route route1, int sens1, Route route2, int sens2) {
        routesLiee = new Route[4];
        routesLiee[sens1] = route1;
        routesLiee[sens2] = route2;
        this.position = position;
    }

    public Intersection(Vector2f position,Route route1, int sens1, Route route2, int sens2, Route route3, int sens3) {
        routesLiee = new Route[4];
        routesLiee[sens1] = route1;
        routesLiee[sens2] = route2;
        routesLiee[sens3] = route3;
        this.position = position;
    }

    public Intersection(Vector2f position, Route route1, int sens1, Route route2, int sens2, Route route3, int sens3, Route route4, int sens4) {
        routesLiee = new Route[4];
        routesLiee[sens1] = route1;
        routesLiee[sens2] = route2;
        routesLiee[sens3] = route3;
        routesLiee[sens4] = route4;
        this.position = position;
    }

    public Route[] getRoutesLiee() {
        return routesLiee;
    }

    public Vector2f getPosition() {
        return position;
    }

}
