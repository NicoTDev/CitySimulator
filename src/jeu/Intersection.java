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

    private String nom;

    private boolean isMaison;

    private Vector2f position;

    private Vector2f[] pointsIntersection;

    private Vector2f direction;

    private Model intersectionModel;

    private Entite intersectionEntite;

    private Scene scene;

    private String id;

    private float angle;

    private Signalisation signalisation;

    /**
     *
     * @param scene refDeLaScene
     * @param routeIni routeRelieDeBase
     * @param sens sens -1 pour la lier au debut de la route 1 pour le lier au dernier
     */
    public Intersection(Scene scene, Route routeIni, int sens) {

        //initialiser les variables
        routesLiee = new Route[4];
        this.scene = scene;
        this.isMaison = false;
        this.id = genererNom();
        Vector2f direction;
        Vector3f position;
        //créer le model de l'entite
        intersectionModel = ModelLoader.loadModel("model-" + id,"ressources/models/intersection/intersection.obj",scene.getTextureCache());
        scene.ajouterModel(intersectionModel);

        //créer l'entite de l'intersection
        intersectionEntite = new Entite("entite-"+id,intersectionModel.getId());
        intersectionEntite.setTaille(1);

        //ajouter à l'intersection la premiere route
        ajouterRoute(routeIni,0);

        //regardez si c'est la derniere ou la premiere intersection, créer ensuite la variable direction pour savoir dans quelle direction l'intersection pointe
        if (sens == -1) {
            direction = new Vector2f(routeIni.getPointsRoute().get(1)).sub(routeIni.getPointsRoute().get(0));
        }
        else {
            direction = new Vector2f(routeIni.getPointsRoute().get(routeIni.getPointsRoute().size()-1)).sub(routeIni.getPointsRoute().get(routeIni.getPointsRoute().size()-2));
        }
        direction.normalize().mul(sens * 0.5f);
        this.setDirection(direction);

        //trouver l'angle de l'intersection avec le vection (1,0)
        float angle = -direction.angle(new Vector2f(1,0));
        //positionnner l'interesection
        this.getIntersectionEntite().setRotation(0,1,0,angle);

        //ajouter l'intersection à la liste des intersections
        scene.ajouterIntersection(this);

        //regarder encore c'est le premier ou le dernier pour ajouter l'intersection à la rue et ajouter l'intersection dans la variable de la route
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

        //dev------------------------

        setLumiere();
    }

    public Intersection(Scene scene,Route routeLiee) {
        this.routesLiee = new Route[1];
        this.scene = scene;
        isMaison = true;
        routesLiee[0] = routeLiee;
        this.id = genererNom().replaceAll("Intersection", "Maison");
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
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
        angle = -direction.angle(new Vector2f(0,1));
    }

    public String genererNom() {
        return "Intersection # " + scene.getIntersections().size() + " ( ID : #" + System.currentTimeMillis() + " )";
    }

    public Vector2f[] getPointsIntersection() {
        return pointsIntersection;
    }

    public Vector2f getDirection() {
        return direction;
    }

    public void setSignalisation(Signalisation signalisation) {
        this.signalisation = signalisation;
        if (signalisation.getClass() == Arret.class) {
            setArret();
        }
        else if (signalisation.getClass() == Lumiere.class) {
            setLumiere();
        }
    }

    public void setLumiere() {
        int rotationIncrement = -90;
        for (Vector2f position : getPositionsSignalisation()) {
            Entite entite = new Entite("entite ("+position.x+","+position.y+")","lumiere-model");
            entite.setPosition(position.x,0.01f,position.y);
            entite.setRotation(0,1,0,angle - (float) Math.toRadians(rotationIncrement));
            scene.ajouterEntite(entite);
            rotationIncrement += 90;
        }
    }

    public void setArret() {
        int rotationIncrement = -90;
        for (Vector2f position : getPositionsSignalisation()) {
            Entite entite = new Entite("entite ("+position.x+","+position.y+")","arret-model");
            entite.setPosition(position.x,0.01f,position.y);
            entite.setRotation(0,1,0,angle - (float) Math.toRadians(rotationIncrement));
            scene.ajouterEntite(entite);
            rotationIncrement += 90;
        }

    }

    public Vector2f[] getPositionsSignalisation() {
        Vector2f directionPerp = new Vector2f(-direction.y,direction.x);
        return new Vector2f[]{
                //Point central + (direction) * multiplicateur
                //Point central + ((point Central +- pt 1 +- pt 2) * multiplicateur)
                new Vector2f(position).sub(direction).add(directionPerp),
                new Vector2f(position).add(direction).add(directionPerp),
                new Vector2f(position).add(direction).sub(directionPerp),
                new Vector2f(position).sub(direction).sub(directionPerp)
        };
    }

    public void getSignalisation() {}

    public String toString() {
        return this.id;
    }

    public boolean isMaison() {
        return isMaison;
    }
}
