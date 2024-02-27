package jeu;

import moteur.Graphique.Model;
import moteur.Graphique.ModelLoader;
import moteur.Graphique.TextureCache;
import moteur.scene.Entite;
import moteur.scene.Scene;

public class Terrain extends Entite {

    private Model terrainModel;

    private static final String TERRAIN_MODEL_ID = "terrain-model";

    public Terrain(String id, TextureCache textureCache) {
        super(id, TERRAIN_MODEL_ID);
        terrainModel = ModelLoader.loadModel(TERRAIN_MODEL_ID,"ressources/models/terrain/terrain.obj",textureCache);

    }



    public Model getTerrainModel() {
        return terrainModel;
    }

    public float getHauteur() {
        System.out.println(getPosition().y);
        return getPosition().y;
    }
}
