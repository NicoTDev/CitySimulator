package moteur.Graphique;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * classe pour lire des données dans un fichier .txt
 */
public abstract class LecteurFichier {

    public static String lireFichier(String chemin){
        String fichier;
        try {
            fichier = new String(Files.readAllBytes(Paths.get(chemin)));
        }
        catch (Exception ignored) {
            throw new IllegalArgumentException("Fichier introuvable");
        }

        return fichier;
    }
}
