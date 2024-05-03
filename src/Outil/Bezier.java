package Outil;

import org.joml.Vector2f;

public abstract class Bezier {


    /**
     * donner l'interpolation linéaire entre 2 points
     * @param v1 point 1
     * @param v2 point 2
     * @param t temps
     * @return le point au temps t
     */
    private static Vector2f lineaire(Vector2f v1, Vector2f v2, float t) {

        return new Vector2f(v1).add(new Vector2f(v2).sub(new Vector2f(v1)).mul(t));
    }

    /**
     * donne l'interpolation quadratique entre 3 points
     * @param v1 point 1
     * @param v2 point 2
     * @param v3 point 3
     * @param t temps
     * @return le point au temps t
     */
    private static Vector2f quadratique(Vector2f v1, Vector2f v2, Vector2f v3, float t) {

        Vector2f vecteurInter1 = lineaire(v1,v2,t);
        Vector2f vecteurInter2 = lineaire(v2,v3,t);

        return lineaire(vecteurInter1,vecteurInter2,t);
    }

    /**
     * donne l'interpolation cubique entre 4 points
     * @param v1 point 1
     * @param v2 point 2
     * @param v3 point 3
     * @param v4 point 4
     * @param t temps
     * @return le point au temps t
     */
    public static Vector2f cubique(Vector2f v1, Vector2f v2, Vector2f v3, Vector2f v4, float t) {

        Vector2f vecteurInter1 = quadratique(v1,v2,v3,t);
        Vector2f vecteurInter2 = quadratique(v2,v3,v4,t);

        return lineaire(vecteurInter1,vecteurInter2,t);


    }

}
