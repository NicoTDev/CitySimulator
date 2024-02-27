package moteur.Graphique;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material {

    private List<Mesh> meshList;

    public static final Vector4f COULEUR_DEFAUT = new Vector4f(0,0,0,1);

    private Vector4f couleurDiffuse;

    private String cheminTexture;

    public Material() {
        couleurDiffuse = COULEUR_DEFAUT;
        meshList = new ArrayList<>();
    }

    public void detruireProgramme() {meshList.forEach(Mesh::detruireProgramme);}

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public String getCheminTexture() {
        return cheminTexture;
    }

    public void setCheminTexture(String cheminTexture) {
        this.cheminTexture = cheminTexture;
    }

    public Vector4f getCouleurDiffuse() {
        return couleurDiffuse;
    }

    public void setCouleurDiffuse(Vector4f couleurDiffuse) {
        this.couleurDiffuse = couleurDiffuse;
    }


}
