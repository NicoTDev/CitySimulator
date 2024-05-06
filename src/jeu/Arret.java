package jeu;

import java.util.*;

public class Arret extends Signalisation {

    Queue<Voiture> voituresEnAttente;

    HashMap<Voiture, Long> dicoVoitureDuree;

    public static final String CHEMINOBJET = "ressources/models/arret/Arret.obj";

    public Arret() {

        voituresEnAttente = new LinkedList<>();
        dicoVoitureDuree = new HashMap<>();
    }

    public void ajouterVoiture(Voiture voiture) {
        voituresEnAttente.add(voiture);
        dicoVoitureDuree.put(voiture, System.currentTimeMillis());
    }

    public void retirerVoiture(Voiture voiture) {
        voituresEnAttente.remove(voiture);
        dicoVoitureDuree.remove(voiture);
    }
}
