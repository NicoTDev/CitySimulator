package jeu;

import Outil.CouleurConsole;

import java.util.*;

public class Graph {

    //créer le dictionnaire avec les intersections et leurs intersections adjoints
    public Map<Intersection, Map<Intersection,Route>> itemsAdjoints;

    SystemeRoutier systemeRoutier;

    Intersection intersectionDepart;

    public Graph(Intersection intersectionDepart, SystemeRoutier systemeRoutier) {

        this.intersectionDepart = intersectionDepart;
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


    public Stack<Intersection> getCheminIntersection(Intersection intersectionArrivee) {
        Stack<Intersection> cheminIntersection = new Stack<>();
        HashMap<Intersection, Float> intersectionNonObserve = new HashMap<>();
        HashMap<Intersection, Float> intersectionObserve = new HashMap<>();



        //créer le tableau avec les valeurs
        for (Intersection intersection : itemsAdjoints.keySet()) {
            intersectionNonObserve.put(intersection, Float.POSITIVE_INFINITY);
        }

        if (!intersectionNonObserve.containsKey(intersectionArrivee))
            throw new IllegalArgumentException("Les intersections ne sont pas liables!");


        //mettre la valeur de 0 à l'intersection de base
        intersectionNonObserve.put(intersectionDepart,0F);

        //tant que tous les sommets n'ont pas été observés
        while (!intersectionNonObserve.isEmpty()) {

            //on choisit le sommet le plus proche
            Intersection intersectionARegarder = getIntersectionLaPlusProche(intersectionNonObserve);

            //on l'ajoute à la liste des sommets observés et on l'enleve de celle des sommets non observés
            intersectionObserve.put(intersectionARegarder,intersectionNonObserve.get(intersectionARegarder));
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



        //faire le chemin
        cheminIntersection.push(intersectionArrivee);

        //tant qu'on est pas revenu au début
        while (cheminIntersection.peek() != intersectionDepart) {
            //on trouve l'intersection la plus proche du debut lié à celle que l'on regarde
            Intersection prochaineIntersectionAAregarder = (Intersection) itemsAdjoints.get(cheminIntersection.peek()).keySet().toArray()[0];
            for (Intersection intersection : itemsAdjoints.get(cheminIntersection.peek()).keySet()) {
                if (intersectionObserve.get(intersection) < intersectionObserve.get(prochaineIntersectionAAregarder))
                    prochaineIntersectionAAregarder = intersection;
            }
            cheminIntersection.push(prochaineIntersectionAAregarder);

        }



        return cheminIntersection;
    }

    public Intersection getIntersectionLaPlusProche(HashMap<Intersection, Float> intersections) {
        //mettre la premiere intersections
        Intersection intersectionLaPlusProche = (Intersection) intersections.keySet().toArray()[0];
        for (Intersection intersection : intersections.keySet()) {
            if (intersections.get(intersection) < intersections.get(intersectionLaPlusProche)) {
                intersectionLaPlusProche = intersection;
            }
        }

        return intersectionLaPlusProche;

    }
}
