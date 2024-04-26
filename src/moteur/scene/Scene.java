package moteur.scene;

import jeu.Intersection;
import jeu.Route;
import jeu.Voiture;
import moteur.Graphique.Mesh;
import moteur.Graphique.Model;
import moteur.Graphique.TextureCache;
import moteur.Moteur;
import org.lwjgl.system.Pointer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsable d'entreposer les éléments 3D du jeu
 */
public class Scene {


    private Entite entiteSelectionne;

    private Projection projection;

    private Camera camera;

    private HashMap<String, Model> dicoModel;

    private TextureCache textureCache;

    //ici on regarde tous les types d'entite
    private ArrayList<Voiture> voitures;

    private HashMap<String, Route> routes;

    private ArrayList<Intersection> intersections;

    public Scene(int largeur, int hauteur) {
        projection = new Projection(largeur,hauteur);
        textureCache = new TextureCache();
        camera = new Camera();
        dicoModel = new HashMap<>();
        entiteSelectionne = null;
        routes = new HashMap<>();
        voitures = new ArrayList<>();
        intersections = new ArrayList<>();

    }

    public void detruireProgramme() {
        dicoModel.values().forEach(Model::detruireProgramme);
    }
    public void resize(int largeur, int hauteur) {
        projection.mettreAJour(largeur,hauteur);
    }
    public void ajouterModel(Model model) {
        dicoModel.put(model.getId(),model);
    }

    public void ajouterEntite(Entite entite) {
        String idModel = entite.getIdModel();
        Model model = dicoModel.get(idModel);
        if (model == null)
            throw new IllegalArgumentException(idModel + " introuvable");
        model.getEntites().add(entite);

        //tout depend de la nature de l'entite, on l'ajoute dans son groupe correspondant
    }

    public void ajouterVoiture(Voiture voiture) {
        voitures.add(voiture);
        ajouterEntite(voiture);
    }
    public void ajouterRoute(Route route) {
        routes.put(route.toString(),route);
        ajouterEntite(route.getRouteEntite());
    }

    public void ajouterIntersection(Intersection intersection) {
        intersections.add(intersection);
        ajouterEntite(intersection.getIntersectionEntite());
    }

    public Projection getProjection() {return projection;}

    public HashMap<String, Model> getDicoModel() {return dicoModel;}

    public Camera getCamera() {return camera;}

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public Entite getEntiteSelectionne() {
        return entiteSelectionne;
    }

    public void setEntiteSelectionne(Entite entite) {
        this.entiteSelectionne = entite;
    }


    public HashMap<String,Route> getRoutes() {
        return routes;
    }
    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }
}
