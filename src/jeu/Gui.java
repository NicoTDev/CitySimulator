package jeu;

import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiWindow;
import imgui.type.ImFloat;
import moteur.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Gui {

    SystemeRoutier systemeRoutier;

    String messageErreurActuel;

    boolean isErrone;

    long delai;

    boolean isEnCours;

    Scene scene;
    long tempsDépart;
    int selectedRadioIndex = 0;

    public Gui(Scene scene, SystemeRoutier systemeRoutier) {
        isEnCours = false;
        this.systemeRoutier = systemeRoutier;
        this.scene = scene;
        tempsDépart = 0;
        messageErreurActuel = "NaN";
    }


    public void rendre() {
        ImGui.getIO().setFontGlobalScale(2f);
        ImGui.newFrame();
        ImGui.setNextWindowSize(1920, 1080);
        ImGui.begin("testerr",
                ImGuiWindowFlags.NoTitleBar+ImGuiWindowFlags.NoResize+ImGuiWindowFlags.NoBackground+ImGuiWindowFlags.NoMove);
        //créer tous les élements de la fenêtre ici

        //-------------------------
        switch(systemeRoutier.modeUtilisateur){
            case SPECTATEUR :
                boutonsSpectateur(tempsDépart);
                break;
            case CONSTRUCTEURDEROUTE :
                boutonsUniversels();
                boutonsConstructeurRoutes();
                break;
            case PLACEURINTERSECTION:
                boutonsUniversels();
                boutonsPlacerIntersections();
                break;
            case CUSTOMIZERINTERSECTION:
                boutonsUniversels();
                boutonsPersonnaliserIntersections();
                break;
        }
        if (isErrone) {
            afficherMessageErreur();
        }
        //-------------------------
        ImGui.end();
        ImGui.render();
        ImGui.endFrame();
    }
    public void boutonsUniversels(){
        //Boutons universels
        ImGui.setCursorPos(770,10);
        ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        if (ImGui.button("Débuter la simulation",320,50)) {
            scene.getRoutes().values().forEach(Route::reinitialiserNombreUtilisation);
            isEnCours = true;
            tempsDépart = System.currentTimeMillis();
            systemeRoutier.setModeUtilisateur(Mode.SPECTATEUR);
        }
        ImGui.popStyleColor();

        //Constructeur route
        ImGui.setCursorPos(0,10);
        chaqueBoutons(Mode.CONSTRUCTEURDEROUTE, "Constructeur de routes");

        //Placer intersection
        ImGui.setCursorPos(0,90);
        chaqueBoutons(Mode.PLACEURINTERSECTION,"Placeur d'intersections");

        //Personaliser un intersection
        ImGui.setCursorPos(0,170);
        chaqueBoutons(Mode.CUSTOMIZERINTERSECTION,"Personnaliseur d'intersections");

        //créer le bouton pour réinitialiser
        ImGui.setCursorPos(800,800);
        if (ImGui.button("Réinitialiser le niveau")) {
            systemeRoutier.finirConstructionRoute();
            scene.nettoyerScene();
        }
    }
    public void boutonsSpectateur(long tempsDépart){
        //Arrêter la simulation
        ImGui.setCursorPos(770,10);
        ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));
        if (ImGui.button("Arrêter la simulation",320,50)) {
            isEnCours = false;
            systemeRoutier.setModeUtilisateur(Mode.CONSTRUCTEURDEROUTE);
        }
        ImGui.popStyleColor();

        //Dire le temps que la simulation a commencé
        long tempsActuel = System.currentTimeMillis();
        ImGui.setCursorPos(800,100);
        ImGui.pushStyleColor(ImGuiCol.Text,ImGui.getColorU32(0,0,0,1));

        ImGui.textWrapped("Temps écoulé : " + (int)((tempsActuel-tempsDépart)/1000) + " s");
        ImGui.popStyleColor();

        ImGui.setCursorPos(10,800);
        float[] valeurTemps = new float[]{systemeRoutier.vitesseRoulee};
        ImGui.sliderFloat("Vitesse du temps",valeurTemps,0F,20F);
        systemeRoutier.vitesseRoulee = valeurTemps[0];
        scene.getVoitures().forEach(n->n.setVitesseMaximale(valeurTemps[0]));

        ImGui.setCursorPos(10,850);
        int[] valeurVoiture = new int[]{systemeRoutier.nombreVoiture};
        ImGui.sliderInt("Nombre de voitures",valeurVoiture,0,60);
        systemeRoutier.nombreVoiture = valeurVoiture[0];

        //Autre fenetre
        ImGui.setNextWindowSize(800, 800);
        ImGui.begin("Rapport",ImGuiWindowFlags.NoResize+ImGuiWindowFlags.AlwaysVerticalScrollbar);

        //Dire le temps que la simulation a commencé
        ImGui.setCursorPos(0, 900);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(0, 0, 0, 1));
        ImGui.textWrapped("Simulation en cours...");
        ImGui.popStyleColor();

        ImGui.setCursorPos(40,40);
        ImGui.text("Nom des routes :");
        ImGui.setCursorPos(400,40);
        ImGui.text("Nombre de fois utilisée :");

        int nombreUtilisationTotal = scene.getRoutes().values().stream().mapToInt(Route :: getNombreUtilisation).sum()+1;
        for(int i =0; i < scene.getRoutes().size();i++){
            int nombreUtilisation = scene.getRoutes().values().stream().sorted(Comparator.comparingInt(Route::getNombreUtilisation)).toList().reversed().get(i).getNombreUtilisation();
            float rougeAbsolu = (510 * nombreUtilisation)
                    / (nombreUtilisationTotal*0.5f);
            float rouge = Math.min(255,rougeAbsolu);


            ImGui.setCursorPos(40,80 + (i * 22));
            ImGui.text(scene.getRoutes().values().stream().sorted(Comparator.comparingInt(Route::getNombreUtilisation)).toList().reversed().get(i).getNomAbrege());
            ImGui.setCursorPos(600,80 + (i * 22));
            ImGui.pushStyleColor(ImGuiCol.Text, (nombreUtilisation > 0)?ImGui.getColorU32(rouge/(float)255, ((255-rouge))/(float)255 ,0, 1):ImGui.getColorU32(0, 255,0, 1));
            ImGui.text(String.valueOf(scene.getRoutes().values().stream().sorted(Comparator.comparingInt(Route::getNombreUtilisation)).toList().reversed().get(i).getNombreUtilisation())); // Nbr de fois sur la route
            ImGui.popStyleColor();
        }
        ImGui.end();
    }
    public void boutonsConstructeurRoutes(){
        ImGui.getIO().setFontGlobalScale(1.5f);
        //Dire le temps que la simulation a commencé
        ImGui.setCursorPos(580, 100);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(0, 0, 0, 1));
        if(systemeRoutier.routeEnConstruction == null)
            ImGui.textWrapped("Appuyez sur le terrain ou un objet pour démarrer une route");
        else
            ImGui.textWrapped(systemeRoutier.routeEnConstruction.getNomAbrege() + " en construction");

        ImGui.popStyleColor();
    }

    public void boutonsPlacerIntersections() {
        //Dire le temps que la simulation a commencé
        ImGui.setCursorPos(700, 100);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(0, 0, 0, 1));
        ImGui.textWrapped("Appuyez sur une fin ou un début de route");
        ImGui.popStyleColor();
    }
    public void boutonsPersonnaliserIntersections() {
        //Dire le temps que la simulation a commencé
        ImGui.setCursorPos(690, 100);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(0, 0, 0, 1));
        //Mauvais if en attendant que Nico crée variable pour dire si une intersection est selectionnée ou pas
        /*
        if(systemeRoutier.routeEnConstruction == null)
          ImGui.textWrapped("Appuyez sur une intersection");
        else{

        */
        // Afficher les boutons radio

        if (systemeRoutier.intersectionSelectionne != null) {
            ImGui.text("Choisissez une intersection à modifier");
            ImGui.setCursorPos(50, 800);
            if (systemeRoutier.getIntersectionSelectionne().getSignalisation() == null) {
                selectedRadioIndex = 2;
            }
            else {
                if (systemeRoutier.getIntersectionSelectionne().getSignalisation().getClass() == Arret.class)
                    selectedRadioIndex = 1;
                else if (systemeRoutier.getIntersectionSelectionne().getSignalisation().getClass() == Lumiere.class)
                    selectedRadioIndex = 0;
            }

            if (ImGui.radioButton("Lumières", selectedRadioIndex == 0)) {
                systemeRoutier.getIntersectionSelectionne().setSignalisation(new Lumiere());
                selectedRadioIndex = 0;
            }

            if (ImGui.radioButton("Panneaux arrêts", selectedRadioIndex == 1)) {
                systemeRoutier.getIntersectionSelectionne().setSignalisation(new Arret());
                selectedRadioIndex = 1;
            }

            if (ImGui.radioButton("Rien", selectedRadioIndex == 2)) {
                systemeRoutier.getIntersectionSelectionne().setSignalisation(null);
                selectedRadioIndex = 2;
            }
            // }
        }
        ImGui.popStyleColor();
    }
    //Optimisation de la fonction boutonsUniversels
    public void chaqueBoutons(Mode mode,String nom){
        if(systemeRoutier.modeUtilisateur == mode)
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        else
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));

        if (ImGui.button(nom,430,70)) {
            systemeRoutier.setModeUtilisateur(mode);
        }
        ImGui.popStyleColor();
    }
    //Optimisation de la fonction boutonsUniversels
    public void chaqueBoutons(Mode mode,String nom){
        if(systemeRoutier.modeUtilisateur == mode)
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        else
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));

        if (ImGui.button(nom,430,70)) {
            systemeRoutier.setModeUtilisateur(mode);
        }
        ImGui.popStyleColor();
    }



    public boolean isEnCours() {
        return isEnCours;
    }

    public void setEnCours(boolean valeur) {
        isEnCours = valeur;
    }

    public void afficherMessageErreur() {
        if (delai == 0)
            delai = System.currentTimeMillis();

        if (System.currentTimeMillis()-delai > 2000) {
            delai = 0;
            isErrone = false;
        }
        else {
            ImGui.setCursorPos(900-(messageErreurActuel.length()*5),150);
            ImGui.pushStyleColor(ImGuiCol.Text,ImGui.getColorU32(1,0,0,1));
            ImGui.text(messageErreurActuel);
            ImGui.popStyleColor();

        }


    }

    public void setMessageErreur(String messageErreurActuel) {
        this.messageErreurActuel = messageErreurActuel;
        isErrone = true;
    }
}