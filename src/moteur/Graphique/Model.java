package moteur.Graphique;

import moteur.scene.Entite;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Model {

    private final String id;
    private List<Entite> entites;

    private List<Material> materiaux;

    public Model(String id, List<Material> materiaux) {
        this.id = id;
        this.materiaux = materiaux;
        entites = new ArrayList<>();
    }

    public void detruireProgramme() {}

    public List<Entite> getEntites() {
        return entites;
    }

    public List<Material> getMateriaux() {
        return materiaux;
    }

    public String getId() {return id;}





}
