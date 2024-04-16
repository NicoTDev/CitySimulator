package jeu;

import Outil.CouleurConsole;
import moteur.EntreSouris;
import moteur.Fenetre;
import moteur.Graphique.Terrain;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;

public class SystemeRoutier {


    Terrain terrain;

    Route routeEnConstruction;

    boolean isRouteEnCours;

    Mode modeUtilisateur;

    Scene scene;

    int nombreSegment = 1;

    public SystemeRoutier(Terrain terrain, Scene scene) {

        this.terrain = terrain;
        this.scene = scene;

        this.isRouteEnCours = false;
        routeEnConstruction = null;

        modeUtilisateur = Mode.CONSTRUCTEURDEROUTE;


    }

    public void interagir(Fenetre fenetre, EntreSouris entreSouris) {
        switch (modeUtilisateur) {

            case CONSTRUCTEURDEROUTE -> {
                placerPointRoute(fenetre, entreSouris.getPositionActuelle());
            }

            case PLACEURINTERSECTION -> {
                placerIntersection(fenetre, entreSouris.getPositionActuelle());
            }
            case CUSTOMIZERINTERSECTION -> {
            }
        }
    }


    public Vector4f getDirectionSouris(Fenetre fenetre, Vector2f positionSouris) {
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

    public void placerPointRoute(Fenetre fenetre, Vector2f pos) {


        Vector4f directionSouris = getDirectionSouris(fenetre, pos);

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
                    if (Math.abs(intersectionPoint.x - point.x) < 0.25 && Math.abs(intersectionPoint.y - point.z) < 0.25) {
                        isSurIntersection = true;
                        if (intersection.getRoutesLiee()[i] == null ) {
                            //regardez si la route se termine à l'intersection
                            if (isRouteEnCours) {
                                Vector2f direction = new Vector2f(intersectionPoint).sub(intersection.getPosition()).normalize().mul(2);
                                routeEnConstruction.ajouterSegment(new Vector2f((intersectionPoint.x + direction.x), (intersectionPoint.y + direction.y)), scene);
                                direction.div(1.5f);
                                routeEnConstruction.ajouterSegment(new Vector2f((intersectionPoint.x + direction.x), (intersectionPoint.y + direction.y)), scene);
                                isRouteEnCours = false;
                                intersection.ajouterRoute(routeEnConstruction,i);
                                routeEnConstruction.setIntersectionFin(intersection);
                                routeEnConstruction.ajouterSegment(new Vector2f(intersectionPoint.x,intersectionPoint.y), scene);
                            }
                            //sinon, on demarre une route
                            else {
                                routeEnConstruction = new Route(new Vector2f(intersectionPoint.x, intersectionPoint.y), scene);
                                isRouteEnCours = true;
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
            //si ce n'était pas sur une intersection, faire la procédure habituelle
            if (!isSurIntersection) {

                //soit créer une route, soit ajouter un segment si cette route n'existe pas
                if (isRouteEnCours) {
                    routeEnConstruction.ajouterSegment(new Vector2f(point.x, point.z), scene);
                } else {
                    routeEnConstruction = new Route(new Vector2f(point.x, point.z), scene);
                    isRouteEnCours = true;
                }
            }

            //System.out.println(scene.getRoutes().keySet());

            //System.out.println( plusPetiteDistance == Float.POSITIVE_INFINITY ? plusPetiteDistance : CouleurConsole.BLEU.couleur + plusPetiteDistance);

        }
    }

    public void placerIntersection(Fenetre fenetre, Vector2f pos) {

        Vector4f directionSouris = getDirectionSouris(fenetre, pos);
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

                //System.out.println("Assez proche");
                Intersection intersection = new Intersection(scene,route,-1);
                System.out.println(route);


            }

            else if (Intersectionf.intersectRayAab(centre.x, centre.y, centre.z, directionSouris.x, directionSouris.y,
                    directionSouris.z, dernierPoint.x-1, 0, dernierPoint.y-1,dernierPoint.x+1,0.01f,dernierPoint.y+1, t) && t.x < plusPetiteDistance
                    && route.getIntersectionFin() == null) {

                plusPetiteDistance = t.x;

                Intersection intersection = new Intersection(scene,route,1);
                System.out.println(route);

            }

        }
    }

    public void selectionnerIntersection(Fenetre fenetre, Scene scene, Vector2f pos) {


    }

    public void setModeUtilisateur(Mode modeUtilisateur) {
        isRouteEnCours = false;
        this.modeUtilisateur = modeUtilisateur;
    }
}
