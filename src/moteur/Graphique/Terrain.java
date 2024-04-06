package moteur.Graphique;

import moteur.Graphique.Mesh;
import moteur.Graphique.Model;
import moteur.Graphique.ModelLoader;
import moteur.Graphique.TextureCache;
import moteur.scene.Entite;
import moteur.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMesh;

public class Terrain {


    //initialiser les variable
    int largeur;
    int hauteur;
    float pointEnHautGaucheX;
    float pointEnHautGaucheY;

    Vector3f[] verticles;
    int[] triangles;
    Vector2f[] uvs;

    int trianglePos;

    float[] positions;

    float[] textCoords;

    Model terrainModel;

    //generer le terrain au complet
    public Mesh genererTerrain(int largeur, int hauteur) {

        int pos = 0;
        this.trianglePos = 0;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.pointEnHautGaucheX = -(largeur-1)/2f;
        this.pointEnHautGaucheY = (hauteur-1)/2f;
        //this.triangles = new int[(largeur-1)*(hauteur-1)*6];
        this.triangles = new int[1];
        this.verticles = new Vector3f[largeur*hauteur];
        this.verticles = new Vector3f[1];
        this.positions = new float[verticles.length*3];
        this.uvs = new Vector2f[largeur*hauteur];
        this.textCoords = new float[uvs.length*2];

        //ajouter tous les verticles
        /*
        for (int y = 0 ;  y < hauteur; y ++) {
            for (int x = 0 ; x < largeur; x ++) {

                verticles[pos] = new Vector3f(pointEnHautGaucheX+x,0,pointEnHautGaucheY-y);
                positions[pos] = verticles[pos].x;
                positions[pos+1] = verticles[pos].y;
                positions[pos+2] = verticles[pos].z;

                uvs[pos] = new Vector2f(x/(float) largeur,y/(float) hauteur);
                textCoords[pos] = uvs[pos].x;
                textCoords[pos+1] = uvs[pos].y;


                if (y < hauteur-1 && x < largeur-1) {

                    //ajoutons le triangle
                    ajouterTriangle(pos,pos+largeur+1,pos+largeur);
                    ajouterTriangle(pos+1+largeur,pos,pos+1);

                }

                pos++;

            }
        }

         */
        return new Mesh(positions,textCoords,triangles);

    }


    public void ajouterTriangle(int a, int b, int c) {
        triangles[trianglePos] = a;
        triangles[trianglePos+1] = b;
        triangles[trianglePos+2] = c;
        trianglePos+=3;
    }


    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }
}
