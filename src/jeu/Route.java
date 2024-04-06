package jeu;

import Outil.Bezier;
import Outil.CouleurConsole;
import moteur.Fenetre;
import moteur.Graphique.*;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 *  Cet algorithme utilise les courbes de Bézier pour tracer les routes
 *  informations sur le sujet : https://fr.wikipedia.org/wiki/Courbe_de_B%C3%A9zier
 */
public class Route{

    final float COEFFICIENT_FROTTEMENT = 10;

    final float TAILLEROUTE = 2;


    //points d'ancrage et de controle
    ArrayList<Vector2f> pointsBezier;

    //points sur la droite (à enlever dans l'avenir)
    ArrayList<Vector2f> pointsRoute;

    String nomRue;

    Model routeModel;

    Mesh routeMesh;


    public Route(Vector2f pointDepart, Scene scene) {
        //créer les points initiales de la route
        nomRue = String.valueOf( (int) (Math.random()*1000000));
        pointsRoute = new ArrayList<>();
        pointsBezier = new ArrayList<>();

        //ajouter les points au start (l'ancrage et le point de base avec)
        pointsBezier.add(new Vector2f(pointDepart));
        pointsBezier.add(new Vector2f(pointDepart).add(-1,0).add(new Vector2f(0,1)).mul(0.5f));

        //créer les textures
        scene.getTextureCache().creerTexture("ressources/models/route/route.png");

        //créer le material de la texture
        Material material = new Material();
        material.setCheminTexture("ressources/models/route/route.png");
        material.setCouleurDiffuse(new Vector4f(0,0,0,1));
        material.getMeshList().add(routeMesh);

        ArrayList<Material> materials = new ArrayList<>();
        materials.add(material);

    }

    public Vector2f point(int i) {
        return pointsBezier.get(i);
    }
    public void ajouterSegment(Vector2f ancrage,Scene scene) {

        //ajouter le point automatique en face du dernier ancrage
        Vector2f newPoint1 = new Vector2f(point(getNombrePoints()-1)).mul(2).sub(new Vector2f(point(getNombrePoints()-2)));
        pointsBezier.add(newPoint1);
        Vector2f newPoint2 = new Vector2f(ancrage).add(new Vector2f(point(getNombrePoints()-1))).div(2);
        pointsBezier.add(new Vector2f(ancrage));
        pointsBezier.add(newPoint2);
        //changer les points autour.

        mettreAJourRoute(scene);

        //System.out.println(pointsBezier);

    }

    //getUnSegment
    public Vector2f[] getSegment(int i) {

        return new Vector2f[]{getPointsBezier().get(i*3), getPointsBezier().get(i*3+1), getPointsBezier().get(i*3+2), getPointsBezier().get(i*3+3)};

    }

    public void setPointControlAuto(int ancrageIndex) {

        Vector2f ancrage = point(ancrageIndex);
        Vector2f dir = new Vector2f(0,0);
        float[] distancesAutour = new float[2];

        /*
            la manière de fonctionner est que l'on trouver
         */
        if (ancrageIndex - 3 >= 0) {
            Vector2f mouv = new Vector2f(point(ancrageIndex-3)).sub(ancrage);
            dir.add(new Vector2f(mouv).normalize());
            distancesAutour[0] = (float) Math.sqrt(mouv.x*mouv.x+mouv.y*mouv.y);
        }
        if (ancrageIndex + 3 < pointsBezier.size()) {
            Vector2f mouv = new Vector2f(point(ancrageIndex+3)).sub(ancrage);
            dir.sub(new Vector2f(mouv).normalize());
            distancesAutour[1] = (float) -Math.sqrt(mouv.x*mouv.x+mouv.y*mouv.y);
        }

        dir.normalize();

        for (int i = 0 ; i < 2 ; i ++) {
            int indexControl = ancrageIndex + i * 2 - 1 ;
            if (indexControl >= 0 && indexControl < pointsBezier.size())
                pointsBezier.set(indexControl,new Vector2f(ancrage).add(new Vector2f(dir).mul(distancesAutour[i]).mul(0.5f)));
        }





    }

    public void mettreAJourRoute(Scene scene) {


        pointsRoute = new ArrayList<>();
        for (int i = 0 ; i < pointsBezier.size(); i+=3) {
            setPointControlAuto(i);
        }

        ArrayList<Vector2f> liste = evaluerPointCourbe(0.5f,0.5f);

        for (Vector2f v : liste) {
            pointsRoute.add(v);
        }

        routeMesh = genererRouteMesh(pointsRoute, scene);

        //créer le material de la texture
        Material material = new Material();
        material.setCheminTexture("ressources/models/route/route.png");
        material.setCouleurDiffuse(new Vector4f(0,0,0,1));
        material.getMeshList().add(routeMesh);

        ArrayList<Material> materials = new ArrayList<>();
        materials.add(material);

        Model routeModel = new Model("model-id",materials);
        scene.ajouterModel(routeModel);
        Entite routeEntite = new Entite("route-entite",routeModel.getId());
        scene.ajouterEntite(routeEntite,Route.class);

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

    public ArrayList<Vector2f> getPointsSegments(int index) {
        ArrayList<Vector2f> points = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            points.add(pointsBezier.get(3*index+i));
            //System.out.println(pointsBezier.get(i));
        }
        return points;
    }


    public ArrayList<Vector2f> evaluerPointCourbe(float espacement, float precision) {


        ArrayList<Vector2f> pointsCorrects = new ArrayList<>();
        pointsCorrects.add(point(0));
        Vector2f pointPrecedent = point(0);
        float distanceDepuisPoint = 0;

        //pour chaque segement
        for (int indexSegment = 0 ; indexSegment < getNombreSegments(); indexSegment++) {

            ArrayList<Vector2f> pointsSegments = getPointsSegments(indexSegment);

            float longueurControl = pointsSegments.get(0).distance(pointsSegments.get(1)) + pointsSegments.get(1).distance(pointsSegments.get(2)) + pointsSegments.get(2).distance(pointsSegments.get(3));
            float longueurCurveEstimee = pointsSegments.get(0).distance(pointsSegments.get(3)) + longueurControl / 2.0f;
            int divisions = (int) Math.ceil(longueurCurveEstimee * precision * 10);




            float t = 0.0000001f;
            while ( t <= 1 ) {

                t += 1.0f/(divisions);
                //on trouve un point sur la courbe
                Vector2f pointSurCourbe = Bezier.cubique(pointsSegments.get(0),pointsSegments.get(1),pointsSegments.get(2),pointsSegments.get(3),t);

                //on regarde la distance avec le point précédent
                distanceDepuisPoint += pointPrecedent.distance(pointSurCourbe);

                //
                while (distanceDepuisPoint >= espacement) {

                    float depassement = distanceDepuisPoint-espacement;
                    Vector2f nouveauPoint = new Vector2f(pointSurCourbe).add(new Vector2f(pointPrecedent).sub(new Vector2f(pointSurCourbe)).normalize().mul(depassement));
                    pointsCorrects.add(nouveauPoint);
                    distanceDepuisPoint = depassement;
                    pointPrecedent = nouveauPoint;

                }

                pointPrecedent = pointSurCourbe;

            }

        }


        return pointsCorrects;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    public Vector2f getDernierPoint() {
        return pointsRoute.get(pointsRoute.size()-1);
    }
    public Vector2f getPremierPoint() {
        return pointsRoute.get(0);
    }

    public Vector2f getDirFinal() {
        return new Vector2f(getDernierPoint()).sub(pointsRoute.get(pointsRoute.size()-2)).normalize();
    }
    public Vector2f getDirStart() {
        return new Vector2f(1).sub(getPremierPoint()).normalize();
    }


    public Mesh genererRouteMesh(ArrayList<Vector2f> points, Scene scene) {

        Vector2f[] verticles = new Vector2f[points.size() * 2];
        Vector2f[] uvs = new Vector2f[ verticles.length ];
        int[] tris = new int[(points.size()-1)*6];
        int vertIndex = 0;
        int triIndex = 0;

        for (int i = 0 ; i < points.size(); i++) {

            //initialiser le vector frontal
            Vector2f frontal = new Vector2f(0,0);
            //si le vecteur n'est pas le dernier
            if (i < points.size()-1) {
                frontal.add(new Vector2f(points.get(i+1)).sub(points.get(i)));
            }
            //et il n'est pas le premier
            if (i > 0)
                frontal.add(new Vector2f(points.get(i)).sub(points.get(i-1)));

            //mettre dans un vecteur direction de longueur 1
            frontal.normalize();
            Vector2f vecteurPerp = new Vector2f(-frontal.y,frontal.x);

            verticles[vertIndex] = new Vector2f(points.get(i)).add(new Vector2f(vecteurPerp).mul(TAILLEROUTE).mul(0.5f));
            verticles[vertIndex+1] = new Vector2f(points.get(i)).sub(new Vector2f(vecteurPerp).mul(TAILLEROUTE).mul(0.5f));


            float pourcentageCompletion = i / (float) (points.size() - 1);

            uvs[vertIndex] = new Vector2f(0, pourcentageCompletion);
            uvs[vertIndex + 1] = new Vector2f(1, pourcentageCompletion);

            if (i < points.size() - 1 ) {
                tris[triIndex] = vertIndex;
                tris[triIndex+1] = vertIndex + 2;
                tris[triIndex+2] = vertIndex + 1;

                tris[triIndex + 3] = vertIndex + 1;
                tris[triIndex + 4] = vertIndex + 2;
                tris[triIndex + 5] = vertIndex + 3;

            }

            vertIndex += 2;
            triIndex += 6;

        }

        //créer l'array de positions final
        float[] positions = new float[verticles.length*3];
        int positionIndex = 0;
        for (Vector2f point : verticles) {

            positions[positionIndex++] = point.x;
            positions[positionIndex++] = 0.01f;
            positions[positionIndex++] = point.y;
        }

        //créer les textures coordinates
        float[] textureCoords = new float[uvs.length * 2];
        int textureIndex = 0;
        for (Vector2f uv : uvs) {
            textureCoords[textureIndex++] = uv.x;
            textureCoords[textureIndex++] = uv.y;
        }

        return new Mesh(positions,textureCoords,tris);
    }
}

