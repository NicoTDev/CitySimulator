package moteur.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 *
 */
public class Entite {

    private final String id;

    private final String idModel;

    private Matrix4f matriceModel;

    private Vector3f position;

    private Quaternionf rotation;

    private float taille;

    public Entite(String id, String idModel) {
        this.id = id;
        this.idModel = idModel;
        matriceModel = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        taille = 1;
    }

    public String getIdEntite() {
        return id;
    }
    public String getIdModel() {
        return idModel;
    }

    public Matrix4f getMatriceModel() {
        return matriceModel;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public float getTaille() {
        return taille;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
        mettreAJour();
    }

    public void setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleRad(x,y,z,angle);
        mettreAJour();
    }

    public void setTaille(float taille) {
        this.taille = taille;
        mettreAJour();
    }

    public void mettreAJour() {
        matriceModel.translationRotateScale(position,rotation,taille);
    }
}
