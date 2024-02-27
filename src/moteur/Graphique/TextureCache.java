package moteur.Graphique;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe servant à stocker les textures que l'on aura créé dans le but de les réutiliser
 */
public class TextureCache {

    public static final String TEXTURE_DEFAUT = "ressources/models/defaut/texture_manquante.jpg";

    private Map<String, Texture> dicoTextures;

    public TextureCache() {

        dicoTextures = new HashMap<>();
        dicoTextures.put(TEXTURE_DEFAUT,new Texture(TEXTURE_DEFAUT));

    }
    public void detruireProgramme() {
        dicoTextures.values().forEach(Texture::detruireProgramme);
    }

    public Texture creerTexture(String chemin) {
        return dicoTextures.computeIfAbsent(chemin,Texture::new);
    }

    public Texture getTexture(String chemin) {
        Texture texture = null;
        if (chemin != null)
            texture = dicoTextures.get(chemin);

        if (texture == null)
            texture = dicoTextures.get(TEXTURE_DEFAUT);
        return texture;
    }
}
