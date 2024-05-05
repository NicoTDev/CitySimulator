package jeu;

import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiWindow;
import moteur.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Gui {

    SystemeRoutier systemeRoutier;

    boolean isEnCours;

    Scene scene;
    long tempsDépart;
    int selectedRadioIndex = 0;

    public Gui(Scene scene, SystemeRoutier systemeRoutier) {
        isEnCours = false;
        this.systemeRoutier = systemeRoutier;
        this.scene = scene;
        tempsDépart = 0;
    }


    public void rendre() {
        ImFontAtlas fonts = ImGui.getIO().getFonts();
/*
        // Chargez la police à partir du fichier
        String fontPath = "Fonts.ttf"; // Remplacez ceci par le chemin de votre fichier de police
        fonts.addFontFromFileTTF(fontPath, 16);


        //créer la fenêtre de base

 */
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
            isEnCours = true;
            tempsDépart = System.currentTimeMillis();
            systemeRoutier.setModeUtilisateur(Mode.SPECTATEUR);
        }
        ImGui.popStyleColor();

        //Créations bouton utilisateur
        //Constructeur route
        ImGui.setCursorPos(0,10);
        if(systemeRoutier.modeUtilisateur == Mode.CONSTRUCTEURDEROUTE)
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        else
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));

        if (ImGui.button("Constructeur de routes",430,70)) {
            systemeRoutier.setModeUtilisateur(Mode.CONSTRUCTEURDEROUTE);
        }
        ImGui.popStyleColor();

        //Placer intersection
        ImGui.setCursorPos(0,90);
        if(systemeRoutier.modeUtilisateur == Mode.PLACEURINTERSECTION)
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        else
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));

        if (ImGui.button("Placeur d'intersections",430,70)) {
            systemeRoutier.setModeUtilisateur(Mode.PLACEURINTERSECTION);
        }
        ImGui.popStyleColor();

        //Personaliser un intersection
        ImGui.setCursorPos(0,170);
        if(systemeRoutier.modeUtilisateur == Mode.CUSTOMIZERINTERSECTION)
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        else
            ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));

        if (ImGui.button("Personnaliseur d'intersections",430,70)) {
            systemeRoutier.setModeUtilisateur(Mode.CUSTOMIZERINTERSECTION);
        }
        ImGui.popStyleColor();





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

        for(int i =0; i < scene.getRoutes().size();i++){
            ImGui.setCursorPos(40,80 + (i * 20));
            ImGui.text(scene.getRoutes().values().stream().toList().get(i).getNomAbrege());
            ImGui.setCursorPos(400,80 + (i * 20));
            ImGui.text(String.valueOf(scene.getRoutes().values().stream().toList().get(i).getNombreUtilisation())); // Nbr de fois sur la route
        }
        ImGui.end();
    }
    public void boutonsConstructeurRoutes(){
        ImGui.getIO().setFontGlobalScale(1.5f);

        //Dire le temps que la simulation a commencé
        ImGui.setCursorPos(580, 100);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(0, 0, 0, 1));
        if(systemeRoutier.routeEnConstruction == null) {
            ImGui.textWrapped("Appuyez sur le terrain ou un objet pour démarrer une route");

        }
        else
            ImGui.textWrapped("Route " + systemeRoutier.routeEnConstruction + " en construction");

        ImGui.popStyleColor();
    }

    public void boutonsPlacerIntersections() {
        //Dire le temps que la simulation a commencé
        ImGui.setCursorPos(700, 100);
        ImGui.pushStyleColor(ImGuiCol.Text, ImGui.getColorU32(0, 0, 0, 1));
        ImGui.textWrapped("Appuyez sur une fin d'une route");
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

        ImGui.text("Choisir une option d'intersection:");
        ImGui.setCursorPos(50, 800);
        if (ImGui.radioButton("Lumières", selectedRadioIndex == 0)) {
            selectedRadioIndex = 0;
        }
        if (ImGui.radioButton("Panneaux arrêts", selectedRadioIndex == 1)) {
            selectedRadioIndex = 1;
        }
        // }
        ImGui.popStyleColor();
    }


    public boolean isEnCours() {
        return isEnCours;
    }
}