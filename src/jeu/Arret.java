package jeu;

import java.util.Queue;

public class Arret extends Signalisation {

    Queue<Voiture> voituresEnAttente;

    public Arret() {
        CHEMINOBJET = "ressources/models/arret/Arret.obj";
    }
}
