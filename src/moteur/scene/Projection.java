package moteur.scene;

import org.joml.Matrix4f;

/**
 * Classe responsable de bien gérer la perceptive de la matrice
 */
public class Projection {

    public static final float FOV = (float) Math.toRadians(60);

    private static final float Z_LOIN = 1000;

    private static final float Z_PROCHE = 0.01f;

    private Matrix4f matriceProjection;

    public Projection(int largeur, int hauteur) {
        matriceProjection = new Matrix4f();
        mettreAJour(largeur,hauteur);
    }
    public void mettreAJour(int largeur, int hauteur) {
        matriceProjection.setPerspective(FOV,(float) largeur/hauteur,Z_PROCHE,Z_LOIN);
    }

    //getters
    public Matrix4f getMatriceProjection() {return matriceProjection;}
}
