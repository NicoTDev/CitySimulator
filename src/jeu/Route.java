package jeu;

import Outil.Bezier;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;

import java.util.ArrayList;

/**
 *  Cet algorithme utilise les courbes de Bézier pour tracer les routes
 *  informations sur le sujet : https://fr.wikipedia.org/wiki/Courbe_de_B%C3%A9zier
 */
public class Route {

    final float COEFFICIENT_FROTTEMENT = 10;

    ArrayList<Vector2f> pointsBezier;

    ArrayList<Vector2f> pointsRoute;

    public Route(Vector2f pointDepart) {
        //créer les points initiales de la route
        pointsRoute = new ArrayList<>();
        pointsBezier = new ArrayList<>();
        pointsBezier.add(new Vector2f(pointDepart).add(-1,0));
        pointsBezier.add(new Vector2f(pointDepart).add(-1,0).add(new Vector2f(0,1)).mul(0.5f));
        pointsBezier.add(new Vector2f(pointDepart).add(1,0));
        pointsBezier.add(new Vector2f(pointDepart).add(1,0).add(new Vector2f(0,-1)).mul(0.5f));


        for (int i = 0 ; i < getNombreSegments()-1; i++) {
            ArrayList<Vector2f> liste = Bezier.genererCurve(point(i*3),point(i*3+1),point(i*3+2),point(i*3+3));
            for (Vector2f v : liste) {
                pointsRoute.add(v);
            }
        }

    }

    public Vector2f point(int i) {
        return pointsBezier.get(i);
    }
    public void ajouterSegment(Vector2f ancrage) {

        //ajouter le point automatique en face du dernier ancrage
        Vector2f newPoint1 = new Vector2f(point(getNombrePoints()-1)).mul(2).sub(new Vector2f(point(getNombrePoints()-2)));
        pointsBezier.add(newPoint1);
        Vector2f newPoint2 = new Vector2f(ancrage).add(new Vector2f(point(getNombrePoints()-1))).div(2);
        pointsBezier.add(newPoint2);
        pointsBezier.add(new Vector2f(ancrage));

    }

    //getUnSegment
    public Vector2f[] getSegment(int i) {

        return new Vector2f[]{getPointsBezier().get(i*3), getPointsBezier().get(i*3+1), getPointsBezier().get(i*3+2), getPointsBezier().get(i*3+3)};

    }


    public void genererRoute(Scene scene) {


        for (int i = 0 ; i < getPointsBezier().size(); i++) {
            Entite entite = new Entite("point"+i,"arbre-model");
            entite.setPosition(getPointsBezier().get(i).x, (int) (i/3.0f),getPointsBezier().get(i).y);
            entite.setTaille(0.1f);
            scene.ajouterEntite(entite);

        }
        //pour chaque point, on va devoir trouver les normales
        for (int i = 0; i < getNombrePoints(); i++) {
            for (Vector2f vector2f : pointsRoute) {
                Entite point = new Entite("TESTER"+Math.random(),"camion-model-id");
                point.setTaille(0.1f);
                point.setPosition(vector2f.x, 1,vector2f.y);
                scene.ajouterEntite(point);
            }
        }

    }

    //vu que le premier segment a 4 points et les autres 3, on le retire et le remet à la fin
    public int getNombreSegments() {
        return (getNombrePoints() - 4) / 3 + 1;
    }

    public int getNombrePoints(){
        return pointsBezier.size();
    }

    public Route(ArrayList<Vector2f> points) {
        this.pointsBezier = points;
    }

    public ArrayList<Vector2f> getPointsBezier() {
        return pointsBezier;
    }

    //public void genererPointDeRoute(Scene scene) {
    //}

}

