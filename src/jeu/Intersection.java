package jeu;

import moteur.Graphique.Model;
import moteur.Graphique.ModelLoader;
import moteur.Graphique.Rendu;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.invoke.VolatileCallSite;

public class Intersection {

    private Route[] routesLiee;

    private Vector2f position;

    private Vector2f[] pointsIntersection;

    private Vector2f direction;

    private Model intersectionModel;

    private Entite intersectionEntite;

    private Scene scene;

    private String id;

    private float angle;

    /**
     *
     * @param scene refDeLaScene
     * @param routeIni routeRelieDeBase
     * @param sens sens -1 pour la lier au debut de la route 1 pour le lier au dernier
     */
    public Intersection(Scene scene, Route routeIni, int sens) {
        routesLiee = new Route[4];
        this.scene = scene;
        this.id = genererNom();

        //créer les objets de base
        intersectionModel = ModelLoader.loadModel("model-" + id,"ressources/models/intersection/intersection.obj",scene.getTextureCache());
        scene.ajouterModel(intersectionModel);

        intersectionEntite = new Entite("entite-"+id,intersectionModel.getId());
        intersectionEntite.setTaille(1);
        ajouterRoute(routeIni,0);
        Vector2f direction;
        if (sens == -1) {
            direction = new Vector2f(routeIni.getPointsRoute().get(1)).sub(routeIni.getPointsRoute().get(0));
        }
        else {
            direction = new Vector2f(routeIni.getPointsRoute().get(routeIni.getPointsRoute().size()-1)).sub(routeIni.getPointsRoute().get(routeIni.getPointsRoute().size()-2));
        }
        direction.normalize().mul(sens * 0.5f);
        this.setDirection(direction);
        float angle = -direction.angle(new Vector2f(1,0));
        this.getIntersectionEntite().setRotation(0,1,0,angle);
        scene.ajouterIntersection(this);
        Vector3f position;
        if (sens == -1) {
            position = new Vector3f(routeIni.getPremierPoint().x, 0.01f, routeIni.getPremierPoint().y);
            routeIni.setIntersectionDepart(this);
        }
        else {
            position = new Vector3f(routeIni.getDernierPoint().x, 0.01f, routeIni.getDernierPoint().y);
            routeIni.setIntersectionFin(this);
        }
        this.setPosition(position.x+direction.x,position.y,position.z+direction.y);
        this.setPointsIntersection();


    }

    /**
     *
     * @param route route à ajouter
     * @param sens 0 bas 1 droite 2 haut 3 gauche
     */
    public void ajouterRoute(Route route, int sens) {
        routesLiee[sens] = route;
    }

    public Route[] getRoutesLiee() {
        return routesLiee;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector2f(x,z);
        intersectionEntite.setPosition(x,0.01f,z);
    }

    public Entite getIntersectionEntite() {
        return intersectionEntite;
    }

    public void setPointsIntersection() {
        //creer les points de racolage
        pointsIntersection = new Vector2f[]{
                new Vector2f(position.x-direction.x,position.y-direction.y),
                new Vector2f(position.x-direction.y,position.y+direction.x),
                new Vector2f(position.x+direction.x,position.y+direction.y),
                new Vector2f(position.x+direction.y,position.y-direction.x)
        };
        //testerrrrrrr
        for (Vector2f vector2f : pointsIntersection) {
            Entite tester = new Entite("stop"+vector2f, "cube-model");
            tester.setPosition(vector2f.x, 0.01f, vector2f.y);
            scene.ajouterEntite(tester);
            tester.setRotation(0, 1, 0, vector2f.angle(new Vector2f(1,0)));
        }
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
        angle = -direction.angle(new Vector2f(0,1));
    }

    public String genererNom() {

        return "intersection # " + scene.getIntersections().size() + " ( ID : #" + System.currentTimeMillis() + " )";
    }

    public Vector2f[] getPointsIntersection() {
        return pointsIntersection;
    }

    public Vector2f getDirection() {
        return direction;
    }
}
