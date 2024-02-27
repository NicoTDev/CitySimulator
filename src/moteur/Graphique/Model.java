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

    private List<Mesh> meshes;

    public Model(String id, List<Mesh> meshes) {
        this.id = id;
        this.meshes = meshes;
        entites = new ArrayList<>();
    }

    public void detruireProgramme() {}

    public List<Entite> getEntites() {
        return entites;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public String getId() {return id;}





}
