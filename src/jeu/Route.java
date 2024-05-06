package jeu;

import Outil.Bezier;
import moteur.Graphique.*;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;


import java.util.ArrayList;
import java.util.Collections;

/**
 *  Cet algorithme utilise les courbes de Bézier pour tracer les routes
 *  informations sur le sujet : https://fr.wikipedia.org/wiki/Courbe_de_B%C3%A9zier
 */
public class Route {

    //coefficient de frottement de la route
    final float COEFFICIENT_FROTTEMENT = 10;

    private int nombreUtilisation;

    float longueurCurveEstimee;

    //taille de la route (1 par défaut)
    final float TAILLEROUTE = 1;


    //points d'ancrage et de controle
    private ArrayList<Vector2f> pointsBezier;

    //point pour créer les vertexs
    private ArrayList<Vector2f> pointsRoute;

    //nom de la rue
    private String nomRoute;

    //model de la route
    private Model routeModel;

    //mesh de la route
    private Mesh routeMesh;

    //material de la route
    private Material material;
    private ArrayList<Material> materials;

    String nomAbrege;

    //Entite de la route
    private Entite routeEntite;

    //variables pour stocker l'intersection de depart et de fin de la route
    private Intersection intersectionDepart;

    private Intersection intersectionFin;

    /**
     * @param pointDepart point initiale de la route
     * @param scene ref de la scene
     */
    public Route(Vector2f pointDepart, Scene scene) {
        //créer les points initiales de la route
        nomRoute = genererNomRoute();
        pointsRoute = new ArrayList<>();
        pointsBezier = new ArrayList<>();
        nombreUtilisation = 0;

        intersectionDepart = null;
        intersectionFin = null;

        //ajouter les points au start (l'ancrage et le point de base avec)
        pointsBezier.add(new Vector2f(pointDepart));
        pointsBezier.add(new Vector2f(pointDepart).add(-1,0).add(new Vector2f(0,1)).mul(0.5f));

        //créer les textures
        scene.getTextureCache().creerTexture("ressources/models/route/route.png");

        //créer le material de la texture
        material = new Material();
        material.setCheminTexture("ressources/models/route/route.png");
        material.setCouleurDiffuse(new Vector4f(0,0,0,1));

        materials = new ArrayList<>();
        materials.add(material);

        routeModel = new Model("model-"+ nomRoute,materials);
        scene.ajouterModel(routeModel);

        routeEntite = new Entite("entite-"+ nomRoute,routeModel.getId());
        scene.ajouterRoute(this);


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

        ArrayList<Vector2f> liste = evaluerPointCourbe(0.5f,1);

        for (Vector2f v : liste) {
            pointsRoute.add(v);
        }

        routeMesh = genererRouteMesh(pointsRoute, scene);

        if (material.getMeshList().isEmpty())
            material.getMeshList().add(routeMesh);
        else
            material.getMeshList().set(0,routeMesh);

        routeModel = new Model("model-id",materials);

        routeEntite = new Entite("route-entite",routeModel.getId());
        scene.getDicoModel().put(routeEntite.getIdModel(),routeModel);

        scene.ajouterRoute(this);

        //for (Vector2f p : pointsRoute) {
        //    Entite entite = new Entite("","arret-model");
        //    entite.setPosition(p.x,0.2f,p.y);
        //    scene.ajouterEntite(entite);
        //}


    }

    //vu que le premier segment a 4 points et les autres 3, on le retire et le remet à la fin
    public int getNombreSegments() {
        return (getNombrePoints() - 4) / 3 + 1;
    }

    public int getNombrePoints(){
        return pointsBezier.size();
    }


    public ArrayList<Vector2f> getPointsBezier() {
        return pointsBezier;
    }

    public ArrayList<Vector2f> getPointsSegments(int index) {
        ArrayList<Vector2f> points = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            points.add(pointsBezier.get(3*index+i));
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
            longueurCurveEstimee = pointsSegments.get(0).distance(pointsSegments.get(3)) + longueurControl / 2.0f;
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
        return nomRoute;
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

        Vector2f[] verticles = new Vector2f[(points.size() * 2)];
        Vector2f[] uvs = new Vector2f[ verticles.length ];
        int nombreTriangle = 2 * (pointsRoute.size()-1) + (intersectionFin != null ? 2 : 0);
        int[] tris = new int[nombreTriangle * 3];
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
            Vector2f vecteurPerp = new Vector2f(-frontal.y, frontal.x);

            verticles[vertIndex] = new Vector2f(points.get(i)).add(new Vector2f(vecteurPerp).mul(TAILLEROUTE).mul(0.5f));
            verticles[vertIndex+1] = new Vector2f(points.get(i)).sub(new Vector2f(vecteurPerp).mul(TAILLEROUTE).mul(0.5f));


            float pourcentageCompletion = i / (float) (points.size() - 1);

            uvs[vertIndex] = new Vector2f(0, pourcentageCompletion);
            uvs[vertIndex + 1] = new Vector2f(1, pourcentageCompletion);

            if (i < points.size() - 1) {

                tris[triIndex] = vertIndex;
                tris[triIndex+1] = (vertIndex + 2);
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


    public String genererNomRoute() {

        String fichier = LecteurFichier.lireFichier("src/Outil/listeNom");
        String[] noms = fichier.split("[*]", -1);
        String[] prenoms = noms[0].split(",");
        String[] nomsFamille = noms[1].split(",");

        StringBuilder nomRue = new StringBuilder();

        if (Math.random()*100 > 99 ) {
            nomAbrege = "Rue Lolodrog ";
            return "Rue Lolodrog ( ID : " + System.currentTimeMillis()+ " )";
        }
        if (Math.random()*100 > 99 ) {
            nomAbrege = "Rue Alextraterrestre ";
            return "Rue Alextraterrestre ( ID : " + System.currentTimeMillis()+ " )";
        }
        if (Math.random()*100 > 99 ) {
            nomAbrege = "Route Forestière ";
            return "Route Forestière ( ID : " + System.currentTimeMillis()+ " )";
        }
        if (Math.random()*100 > 99 ) {
            nomAbrege = "Route Forestière ";
            return "Route Forestière ( ID : " + System.currentTimeMillis()+ " )";
        }


        nomRue.append("Rue ");
        //une chance sur 8 d'avoir un saint en avant
        if (Math.random() * 100 > 80)
            nomRue.append("Saint-");

        //ajouter le prenom
        nomRue.append(prenoms[(int) (Math.random() * prenoms.length)]);

        //une chance sur 6 d'avoir un deuxieme prenom
        if (Math.random() * 100 > 60) {
            nomRue.append("-");
            nomRue.append(prenoms[(int) (Math.random() * prenoms.length)]);
        }
        nomRue.append(" ");
        nomRue.append(nomsFamille[(int) (Math.random()*nomsFamille.length)]);
        //une chance sur 9 d'avoir un deuxieme nom de famille
        if (Math.random() * 100 > 90) {
            nomRue.append("-");
            nomRue.append(nomsFamille[(int) (Math.random() * nomsFamille.length)]);
        }


        nomAbrege = nomRue.toString();

        //mettre un id unique pour être sur, minimiser les risques
        nomRue.append(" ( ID : #");
        nomRue.append(System.currentTimeMillis());
        nomRue.append(" )");
        return nomRue.toString();
    }

    public Entite getRouteEntite() {

        return routeEntite;
    }

    public Intersection getIntersectionDepart() {
        return intersectionDepart;
    }

    public Intersection getIntersectionFin() {
        return intersectionFin;
    }

    public void setIntersectionDepart(Intersection intersectionDepart) {
        this.intersectionDepart = intersectionDepart;
    }
    public void setIntersectionFin(Intersection intersectionFin) {
        this.intersectionFin = intersectionFin;
    }

    public ArrayList<Vector2f> getPointsRoute() {
        return pointsRoute;
    }

    public ArrayList<Vector2f> getPointsRouteInv() {
        ArrayList<Vector2f> pointInv = new ArrayList<>(pointsRoute);
        Collections.reverse(pointInv);
        return pointInv;
    }


    public float getLongueur() {
        return getPointsRoute().size();
    }

    public int getNombreUtilisation() {
        return nombreUtilisation;
    }

    public void augmenterNombreUtilisation(){
        ++nombreUtilisation;
    }

    public String getNomAbrege() {
        return nomAbrege;
    }

    public void reinitialiserNombreUtilisation() {
        nombreUtilisation = 0;
    }

    public Model getRouteModel() {
        return routeModel;
    }
}