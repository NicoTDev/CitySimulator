package Outil;

public enum CouleurConsole {
    BLEU ("\u001B[36m"),
    JAUNE ("\u001B[33m"),
    ROUGE ("\u001B[31m"),
    RESET ("\u001B[0m");

    public String couleur;
    CouleurConsole(String couleur) {
        this.couleur = couleur;
    }
}
