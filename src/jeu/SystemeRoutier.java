package jeu;

import Outil.CouleurConsole;
import moteur.EntreSouris;
import moteur.Fenetre;
import moteur.Graphique.Terrain;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeMap;

public class SystemeRoutier {


    Terrain terrain;

    Route routeEnConstruction;

    Mode modeUtilisateur;

    Scene scene;

    Fenetre fenetre;

    /**
     * @param terrain reference du terrain
     * @param scene reference de la scene
     * @param fenetre reference de la fenetre
     */
    public SystemeRoutier(Terrain terrain, Scene scene, Fenetre fenetre) {

        this.terrain = terrain;
        this.scene = scene;
        this.fenetre = fenetre;
        routeEnConstruction = null;
        modeUtilisateur = Mode.CONSTRUCTEURDEROUTE;



    }

    /**
     * sert à savoir quelle interaction sera lancé par l'utilisateur
     * @param entreSouris touche appuyée
     */
    public void interagir(EntreSouris entreSouris) {


        switch (modeUtilisateur) {
            case CONSTRUCTEURDEROUTE -> {
                placerPointRoute(entreSouris.getPositionActuelle());
            }
            case PLACEURINTERSECTION -> {
                placerIntersection(entreSouris.getPositionActuelle());
            }
            case CUSTOMIZERINTERSECTION -> {
            }
        }
    }


    /**
     * sert à passer d'un point sur l'écran à un point sur la carte à un vecteur dirigé vers l'intérieur de l'écran
     * @param positionSouris
     * @return un vecteur qui est dirigé vers le centre de l'univers
     */
    public Vector4f getDirectionSouris(Vector2f positionSouris) {
        int largeurFenetre = fenetre.getLargeur();
        int hauteurFenetre = fenetre.getHauteur();

        //on normalise les coordonnées
        float x = (2 * positionSouris.x) / largeurFenetre - 1.0f;
        float y = 1.0f - (2 * positionSouris.y) / hauteurFenetre;
        float z = -1.0f;

        //faire l'inverse de la technique que l'on a fait pour generer les objets en multipliant cette fois les matrices inverse
        //de la projection et de la matriceVue pour lancer un "ray"
        Matrix4f invProjMatrix = scene.getProjection().getMatriceProjectionInverse();

        //créer la direction du rayon
        Vector4f directionSouris = new Vector4f(x, y, z, 1.0f);
        directionSouris.mul(invProjMatrix);
        directionSouris.z = -1.0f;
        directionSouris.w = 0.0f;

        Matrix4f invViewMatrix = scene.getCamera().getMatriceVueInverse();
        directionSouris.mul(invViewMatrix);

        return directionSouris;
    }

    /**
     * fonction pour placer un point de route
     * @param pos position de la souris
     */
    public void placerPointRoute(Vector2f pos) {


        //get la direction de la souris de base
        Vector4f directionSouris = getDirectionSouris(pos);

        //ici on initialise les valeurs de minimum et de maximum du terrain
        Vector4f terrainMin = new Vector4f(-Math.round((terrain.getLargeur() - 1) / 2.0f), 0.0f, -Math.round((terrain.getHauteur() - 1) / 2.0f), 1.0f);
        Vector4f terrainMax = new Vector4f(Math.round((terrain.getLargeur() - 1) / 2.0f), 0.1f, Math.round((terrain.getHauteur() - 1) / 2.0f), 1.0f);
        Vector2f t = new Vector2f();

        float plusPetiteDistance = Float.POSITIVE_INFINITY;
        Vector3f centre = scene.getCamera().getPosition();


        //regarder si on est sur le terrain
        if (Intersectionf.intersectRayAab(centre.x, centre.y, centre.z, directionSouris.x, directionSouris.y, directionSouris.z,
                terrainMin.x, terrainMin.y, terrainMin.z, terrainMax.x, terrainMax.y, terrainMax.z, t) && t.x < plusPetiteDistance) {
            plusPetiteDistance = t.x;

            //créer le point d'intersection
            Vector3f point = new Vector3f(directionSouris.x, directionSouris.y, directionSouris.z).mul(plusPetiteDistance).add(centre);

            //on regarde si l'utilisateur a cliqué sur une intersection
            boolean isSurIntersection = false;
            for (Intersection intersection : scene.getIntersections()) {
                for (int i = 0 ; i < intersection.getPointsIntersection().length; i++) {

                    Vector2f intersectionPoint = intersection.getPointsIntersection()[i];

                    //si on est sur une intersection
                    if (Math.abs(intersectionPoint.x - point.x) < 0.25 && Math.abs(intersectionPoint.y - point.z) < 0.25) {
                        isSurIntersection = true;
                        if (intersection.getRoutesLiee()[i] == null ) {

                            //regardez si la route se termine à l'intersection
                            if (routeEnConstruction != null) {
                                Vector2f direction = new Vector2f(intersectionPoint).sub(intersection.getPosition()).normalize().mul(2);
                                routeEnConstruction.ajouterSegment(new Vector2f((intersectionPoint.x + direction.x), (intersectionPoint.y + direction.y)), scene);
                                direction.div(1.5f);
                                routeEnConstruction.ajouterSegment(new Vector2f((intersectionPoint.x + direction.x), (intersectionPoint.y + direction.y)), scene);
                                intersection.ajouterRoute(routeEnConstruction,i);
                                routeEnConstruction.setIntersectionFin(intersection);
                                routeEnConstruction.ajouterSegment(new Vector2f(intersectionPoint.x,intersectionPoint.y), scene);
                                finirConstructionRoute();
                            }
                            //sinon, on demarre une route
                            else {
                                routeEnConstruction = new Route(new Vector2f(intersectionPoint.x, intersectionPoint.y), scene);
                                Vector2f direction = new Vector2f(intersectionPoint).sub(intersection.getPosition()).normalize().mul(1.5f);
                                routeEnConstruction.ajouterSegment(new Vector2f((intersectionPoint.x + direction.x), (intersectionPoint.y + direction.y)), scene);
                                direction.mul(2f);
                                routeEnConstruction.setIntersectionDepart(intersection);
                                routeEnConstruction.ajouterSegment(new Vector2f((intersectionPoint.x + direction.x), (intersectionPoint.y + direction.y)), scene);
                                intersection.ajouterRoute(routeEnConstruction, i);
                            }
                            //ajouter un segment de depart
                        }
                    }

                }
            }

            //regarder si l'utilisateur appuie sur une maison
            boolean isSurMaison = false;
            for (Maison maison : scene.getMaisons()) {
                //si on est sur une maisom
                if (maison.getRouteReliee() == null) {
                    if (new Vector2f(maison.getPosition().x,maison.getPosition().z).distance(new Vector2f(point.x,point.z)) < 1) {
                        isSurMaison = true;
                        //s'il n'a pas de route en construction, on démarre une route
                        if (routeEnConstruction == null) {
                            Route nouvelleRoute = new Route(new Vector2f(maison.getPosition().x, maison.getPosition().z), scene);
                            routeEnConstruction = nouvelleRoute;
                            Intersection nouvelleIntersection = new Intersection(maison.getNumero(),scene, routeEnConstruction);
                            scene.ajouterIntersection(nouvelleIntersection);
                            routeEnConstruction.setIntersectionDepart(nouvelleIntersection);
                            maison.setRouteReliee(nouvelleRoute);
                            nouvelleRoute.ajouterSegment(new Vector2f(maison.getPointDevantMaison().x, maison.getPointDevantMaison().y), scene);
                            scene.ajouterRoute(nouvelleRoute);


                        }
                        //s'il y avait déjà une route, on conclut la rue en la reliant à la maison
                        else {
                            maison.setRouteReliee(routeEnConstruction);
                            routeEnConstruction.ajouterSegment(new Vector2f(maison.getPointDevantMaison().x, maison.getPointDevantMaison().y), scene);
                            routeEnConstruction.ajouterSegment(new Vector2f(maison.getPosition().x, maison.getPosition().z), scene);
                            Intersection nouvelleIntersection = new Intersection(maison.getNumero(),scene, routeEnConstruction);
                            scene.ajouterIntersection(nouvelleIntersection);
                            routeEnConstruction.setIntersectionFin(nouvelleIntersection);
                            finirConstructionRoute();

                    }
                }
                }


            }
            //si ce n'était pas sur une intersection, faire la procédure habituelle
            if (!(isSurIntersection||isSurMaison)) {

                //soit créer une route, soit ajouter un segment si cette route n'existe pas
                if (routeEnConstruction != null) {
                    routeEnConstruction.ajouterSegment(new Vector2f(point.x, point.z), scene);
                } else {
                    routeEnConstruction = new Route(new Vector2f(point.x, point.z), scene);
                }
            }
        }
    }

    /**
     * méthode pour placer un point intersection sur la carte
     * @param pos position de la souris
     */
    public void placerIntersection(Vector2f pos) {

        Vector4f directionSouris = getDirectionSouris(pos);
        Vector3f centre = scene.getCamera().getPosition();

        //ici on initialise les valeurs de minimum et de maximum du terrain
        Vector2f t = new Vector2f();

        for (Route route : scene.getRoutes().values()) {
            //si le rayon traverse l'objet, on selectionne l'entite
            //dernier point
            //Vector2f dernierPoint = route.getDernierPoint();
            //premier point
            Vector2f premierPoint = route.getPremierPoint();
            Vector2f dernierPoint = route.getDernierPoint();
            float plusPetiteDistance = Float.POSITIVE_INFINITY;

            //regarder si l'utilisateur appuie sur le point de depart
            if (Intersectionf.intersectRayAab(centre.x, centre.y, centre.z, directionSouris.x, directionSouris.y,
                    directionSouris.z, premierPoint.x-1, 0, premierPoint.y-1,premierPoint.x+1,0.01f,premierPoint.y+1, t) && t.x < plusPetiteDistance
                    && route.getIntersectionDepart() == null) {
                plusPetiteDistance = t.x;
                Intersection intersection = new Intersection(scene,route,-1);


            }

            else if (Intersectionf.intersectRayAab(centre.x, centre.y, centre.z, directionSouris.x, directionSouris.y,
                    directionSouris.z, dernierPoint.x-1, 0, dernierPoint.y-1,dernierPoint.x+1,0.01f,dernierPoint.y+1, t) && t.x < plusPetiteDistance
                    && route.getIntersectionFin() == null) {

                plusPetiteDistance = t.x;

                Intersection intersection = new Intersection(scene,route,1);

            }

        }
    }

    public void selectionnerIntersection(Fenetre fenetre, Scene scene, Vector2f pos) {


    }

    /**
     *
     * @param modeUtilisateur
     */
    public void setModeUtilisateur(Mode modeUtilisateur) {
        if (routeEnConstruction != null)
            finirConstructionRoute();
        this.modeUtilisateur = modeUtilisateur;
    }


    /**
     * sert à trouver un chemin pour passer de la route de départ à la route finale
     * @return arrayList qui indique le chemin
     */
    public ArrayList<Route> getChemin(Maison maisonDepart, Maison maisonArrive) {
        Graph graph = new Graph(maisonDepart.getIntersectionMaison(),this);
        Stack<Intersection> chemin = graph.getCheminIntersection(maisonArrive.getIntersectionMaison());
        ArrayList<Route> cheminRoute = new ArrayList<>();

        while (chemin.peek() != maisonArrive.getIntersectionMaison()) {
            cheminRoute.add(getRouteEntreIntersections(chemin.pop(),chemin.peek()));
        }

        return cheminRoute;
    }

    /**
     * sert à trouver l'intersection entre les deux routes
     * @param intersectionA intersection A
     * @param intersectionB intersection B
     * @return la route qui relie les deux (null si aucune)
     */
    public Route getRouteEntreIntersections(Intersection intersectionA, Intersection intersectionB) {

        ArrayList<Route> routeReliees = new ArrayList<>();
        for (Route routeA : intersectionA.getRoutesLiee()) {

            for (Route routeB : intersectionB.getRoutesLiee()) {
                if (routeA == routeB && routeA != null)
                    routeReliees.add(routeA);
            }
        }

        //si aucune route ne les relis, on return null
        if (routeReliees.isEmpty())
            return null;


        //sinon on trouve la route la plus courte
        Route routeLaPlusCourte = routeReliees.get(0);
        for (Route route : routeReliees) {
            if (route.getLongueur() < routeLaPlusCourte.getLongueur())
                routeLaPlusCourte = route;
        }
        return routeLaPlusCourte;
    }

    public void finirConstructionRoute() {
        System.out.println(routeEnConstruction.getNombreSegments());
        if (routeEnConstruction.getNombreSegments() == 1) {
            scene.getRoutes().remove(routeEnConstruction.toString());
            routeEnConstruction.getRouteModel().setEntites(new ArrayList<>());
        }
        routeEnConstruction = null;

    }

    //pour rouler, si l'intersection de fin est l'intersection de debut, on fait rien, si l'intersection de fin de la route actuelle de la voiture est l'intersection de fin de la seconde route aussi, on fait *-1 au sens


}
