package Outil;

import moteur.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class Bezier {


    private static Vector2f lineaire(Vector2f v1, Vector2f v2, float t) {

        return new Vector2f(v1).add(new Vector2f(v2).sub(new Vector2f(v1)).mul(t));
    }

    private static Vector2f quadratique(Vector2f v1, Vector2f v2, Vector2f v3, float t) {

        Vector2f vecteurInter1 = lineaire(v1,v2,t);
        Vector2f vecteurInter2 = lineaire(v2,v3,t);

        return lineaire(vecteurInter1,vecteurInter2,t);
    }

    public static Vector2f cubique(Vector2f v1, Vector2f v2, Vector2f v3, Vector2f v4, float t) {

        Vector2f vecteurInter1 = quadratique(v1,v2,v3,t);
        Vector2f vecteurInter2 = quadratique(v2,v3,v4,t);

        return lineaire(vecteurInter1,vecteurInter2,t);


    }


    public static ArrayList<Vector2f> genererCurve(Vector2f v1, Vector2f v2, Vector2f v3, Vector2f v4) {
        ArrayList<Vector2f> liste = new ArrayList<>();

        for (float i = 0 ; i < 1; i+=0.01) {
            liste.add(cubique(v1,v2,v3,v4,i));
        }

        return liste;
    }

}
