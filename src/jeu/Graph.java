package jeu;

import Outil.CouleurConsole;

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


    public Stack<Intersection> getCheminIntersection(Intersection intersectionDepart, Intersection intersectionArrivee) {
        Stack<Intersection> cheminIntersection = new Stack<>();
        HashMap<Intersection, Float> intersectionNonObserve = new HashMap<>();
        HashMap<Intersection, Float> intersectionObserve = new HashMap<>();

        System.out.println(CouleurConsole.JAUNE.couleur + intersectionDepart + CouleurConsole.RESET.couleur);
        System.out.println(CouleurConsole.JAUNE.couleur + intersectionArrivee + CouleurConsole.RESET.couleur);

        //créer le tableau avec les valeurs
        for (Intersection intersection : itemsAdjoints.keySet()) {
            intersectionNonObserve.put(intersection, Float.POSITIVE_INFINITY);
        }
        //mettre la valeur de 0 à l'intersection de base
        intersectionNonObserve.put(intersectionDepart,0F);

        //tant que tous les sommets n'ont pas été observés
        while (!intersectionNonObserve.isEmpty() && !intersectionObserve.containsKey(intersectionArrivee)) {

            //on choisit le sommet le plus proche
            Intersection intersectionARegarder = getIntersectionLaPlusProche(intersectionNonObserve);

            //on l'ajoute à la liste des sommets observés et on l'enleve de celle des sommets non observés
            intersectionObserve.put(intersectionARegarder,intersectionNonObserve.get(intersectionARegarder));
            cheminIntersection.push(intersectionARegarder);
            intersectionNonObserve.remove(intersectionARegarder);

            //pour tous les sommets collés à lui.
            for (Intersection intersection : itemsAdjoints.get(intersectionARegarder).keySet()) {
                //on regarde d'abord si on l'a analyser, car on ne revient pas
                if (intersectionNonObserve.containsKey(intersection)) {
                    //sinon, on met la bonne valeur
                    intersectionNonObserve.put(intersection,
                            Math.min(intersectionNonObserve.get(intersection),
                                    intersectionObserve.get(
                                            intersectionARegarder)+itemsAdjoints.get(intersectionARegarder).get(intersection).getLongueur()));
                }
            }



        }
        System.out.println(CouleurConsole.BLEU.couleur + cheminIntersection + CouleurConsole.RESET.couleur);








        return cheminIntersection;
    }

    public Intersection getIntersectionLaPlusProche(HashMap<Intersection, Float> intersections) {
        //mettre la premiere intersections
        Intersection intersectionLaPlusProche = (Intersection) intersections.keySet().toArray()[0];
        System.out.println(intersectionLaPlusProche);
        for (Intersection intersection : intersections.keySet()) {
            if (intersections.get(intersection) < intersections.get(intersectionLaPlusProche)) {
                intersectionLaPlusProche = intersection;
            }
        }

        return intersectionLaPlusProche;

    }
}
