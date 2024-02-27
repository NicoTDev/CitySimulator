package moteur.Graphique;


import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

/**
 * Classe pour loader les shaders dans le programme
 */
public class ShaderChargeur {

    private final int idProgramme;

    public ShaderChargeur(List<donneeShader> listeDonnee) {

        //creer le programme de shader
        this.idProgramme = glCreateProgram();

        //vérifier si le programme se crée correctement
        if (idProgramme == 0)
            throw new RuntimeException("Impossible de créer le programme de shader");

        //créer la liste des shaders
        List<Integer> shaderModules = new ArrayList<>();

        //pour chaque shader dans la liste, les ajouter
        listeDonnee.forEach( n -> shaderModules.add(creerShader(LecteurFichier.lireFichier(n.fichierShader),n.typeShader)));


        lier(shaderModules);
    }

    protected int creerShader(String code, int type) {
        int id = glCreateShader(type);
        if (id == 0)
            throw new IllegalArgumentException("Ce shader est incorrect");
        glShaderSource(id,code);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
            throw new IllegalArgumentException("Impossible de compiler ce shader");
        }
        glAttachShader(idProgramme,id);
        return id;
    }

    public void utiliser() {glUseProgram(idProgramme);}

    public void detruireProgramme() {
        if (idProgramme != 0)
            glDeleteProgram(idProgramme);
    }

    private void lier(List<Integer> modules) {
        glLinkProgram(idProgramme);

        if (glGetProgrami(idProgramme, GL_LINK_STATUS) == 0) {
           throw new IllegalArgumentException("Erreur, impossible de lier les shaders");
        }

        //on "libère" les shaders lorsqu'ils sont compilés
        modules.forEach(n -> glDetachShader(idProgramme,n));
        modules.forEach(GL30::glDeleteShader);

    }

    public void delier() {glUseProgram(0);}

    public int getIdProgramme() {return idProgramme;}

    public record donneeShader(String fichierShader, int typeShader) {}
}
