package jeu;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import moteur.scene.Scene;

public class Gui {

    SystemeRoutier systemeRoutier;

    Scene scene;

    public Gui(Scene scene, SystemeRoutier systemeRoutier) {
        this.systemeRoutier = systemeRoutier;
        this.scene = scene;
    }


    public void rendre() {

        //créer la fenêtre de base
        ImGui.newFrame();
        ImGui.begin("testerr",
                ImGuiWindowFlags.NoTitleBar+ImGuiWindowFlags.NoResize+ImGuiWindowFlags.NoBackground+ImGuiWindowFlags.NoMove);
        //créer tous les élements de la fenêtre ici
        //-------------------------

        ImGui.setCursorPos(0,10);
        ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(0,255,0,1));
        //Gui universel, toujours rester là
        if (ImGui.button("Débuter la simulation",200,50)) {
        }
        ImGui.popStyleColor();
        ImGui.pushStyleColor(ImGuiCol.Button,ImGui.getColorU32(255,0,0,1));
        ImGui.setCursorPos(0,100);
        if (ImGui.button("arrêter la simulation",200,50))  {
        }
        ImGui.popStyleColor();






        //-------------------------
        ImGui.end();
        ImGui.render();
        ImGui.endFrame();

    }

}
