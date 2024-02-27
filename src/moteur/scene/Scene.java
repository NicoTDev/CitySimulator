package moteur.scene;

import moteur.Graphique.Mesh;
import moteur.Graphique.Model;
import moteur.Moteur;
import org.lwjgl.system.Pointer;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsable d'entreposer les éléments 3D du jeu
 */
public class Scene {



    private Projection projection;

    private HashMap<String, Model> dicoModel;

    public Scene(int largeur, int hauteur) {
        projection = new Projection(largeur,hauteur);
        dicoModel = new HashMap<>();
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
    }
    public Projection getProjection() {return projection;}

    public HashMap<String, Model> getDicoModel() {return dicoModel;}
}