package moteur.scene;

import org.joml.*;

/**
 *
 */
public class Camera {

    private Vector3f direction;

    private Vector3f position;

    private Vector3f droite;

    private Vector2f rotation;

    private Vector3f haut;

    private Matrix4f matriceVue;

    public Camera() {
        direction = new Vector3f();
        position = new Vector3f();
        droite = new Vector3f();
        rotation = new Vector2f();
        haut = new Vector3f();
        matriceVue = new Matrix4f();
    }

    public void ajouterRotation(float x, float y) {
        rotation.add(x,y);
        recalculate();
    }

    public void avancer(float nombre) {
        matriceVue.positiveZ(direction).mul(nombre);
        position.add(direction);
        recalculate();
    }

    public void reculer(float nombre) {
        matriceVue.positiveZ(direction).negate().mul(nombre);
        position.sub(direction);
        recalculate();
    }

    public void monter(float nombre) {
        matriceVue.positiveY(direction).mul(nombre);
    }


    private void recalculate() {
        matriceVue.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x,-position.y, -position.z);



    }

}
