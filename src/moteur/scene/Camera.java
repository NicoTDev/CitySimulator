package moteur.scene;

import org.joml.*;

/**
 * Classe responsable de gérer la caméra
 */
public class Camera {

    private Vector3f direction;

    private Vector3f position;

    private Vector3f droite;

    private Vector2f rotation;

    private Vector3f haut;

    private Matrix4f matriceVue;

    private Matrix4f matriceVueInverse;

    public Camera() {
        direction = new Vector3f();
        position = new Vector3f();
        droite = new Vector3f();
        rotation = new Vector2f();
        haut = new Vector3f();
        matriceVue = new Matrix4f();
        matriceVueInverse = new Matrix4f();
    }

    public void rotationner(float x, float y) {
        rotation.add(x,y);
        mettreAJour();
    }

    public void avancer(float nombre) {
        matriceVue.positiveZ(direction).negate().mul(nombre);
        position.add(direction);
        mettreAJour();
    }

    public void reculer(float nombre) {
        matriceVue.positiveZ(direction).negate().mul(nombre);
        position.sub(direction);
        mettreAJour();
    }

    public void monter(float nombre) {
        matriceVue.positiveY(haut).mul(nombre);
        position.add(haut);
        mettreAJour();
    }

    public void descendre(float nombre) {
        matriceVue.positiveY(haut).mul(nombre);
        position.sub(haut);
        mettreAJour();
    }

    public void allerGauche(float nombre) {
        matriceVue.positiveX(droite).mul(nombre);
        position.sub(droite);
        mettreAJour();
    }

    public void allerDroite(float nombre) {
        matriceVue.positiveX(droite).mul(nombre);
        position.add(droite);
        mettreAJour();
    }



    private void mettreAJour() {
        matriceVue.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x,-position.y, -position.z);
        matriceVueInverse.set(matriceVue).invert();
    }

    public Matrix4f getMatriceVue() {
        return matriceVue;
    }

    public Matrix4f getMatriceVueInverse() {
        return matriceVueInverse;
    }

    public Vector3f getPosition() {
        return position;
    }
}
