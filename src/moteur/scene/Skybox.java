package moteur.scene;

import moteur.Graphique.*;

public class Skybox {

    private Entite entiteSkybox;

    private Model modelSkybox;

    public Skybox(String cheminModel, TextureCache textureCache) {

        modelSkybox = ModelLoader.loadModel("model-skybox",cheminModel, textureCache);
        entiteSkybox = new Entite("entite-skybox",modelSkybox.getId());

    }

    public Entite getEntiteSkybox() {
        return entiteSkybox;
    }

    public Model getModelSkybox() {
        return modelSkybox;
    }

}
