package jeu;

import java.util.*;

public class Graph {

    //créer le dictionnaire avec les intersections et leurs intersections adjoints
    public Map<Intersection, Map<Intersection,Route>> itemsAdjoints;

    SystemeRoutier systemeRoutier;

    public Graph(Intersection intersectionDepart, SystemeRoutier systemeRoutier) {

        this.systemeRoutier = systemeRoutier;
        itemsAdjoints = new HashMap<>();
        ajouterItem(intersectionDepart);


    }
    public void ajouterItem(Intersection nouvelleIntersection) {

        //créer le tableau avec les intersections adjacents de l'intersection choisie (l'intersection et la reference de la route
        HashMap<Intersection,Route> intersectionsAdjointes = new HashMap<>();

        //pour toutes les routes liées, on trouve les intersections qui seront les intersections adjacentes de l'intersection choisie
        for (Route route : nouvelleIntersection.getRoutesLiee()) {

            //s'il n'y a pas de route, on la skip ça n'a aucun intérêt
            if (route != null) {

                //si l'un ou l'autre est null, cela signifie que la seule intersection possible est celle que l'on regarde et on l'a deja ajouté, donc on ajoute rien
                if (!(route.getIntersectionFin() == null || route.getIntersectionDepart() == null)) {

                    //ajouter l'intersection qui sera une intersection adjointe de l'intersection choisie
                    Intersection intersectionLocale = (nouvelleIntersection == route.getIntersectionFin()) ? route.getIntersectionDepart() : route.getIntersectionFin();

                    //l'ajouter au tableau
                    intersectionsAdjointes.put(intersectionLocale,systemeRoutier.getRouteEntreIntersections(intersectionLocale, nouvelleIntersection));

                }
            }
        }
        //ajouter l'intersection au graph.
        itemsAdjoints.putIfAbsent(nouvelleIntersection,intersectionsAdjointes);

        //pour toutes les adjointes
        for (Intersection intersection : intersectionsAdjointes.keySet())
            if (!itemsAdjoints.containsKey(intersection))
                ajouterItem(intersection);

    }
}
