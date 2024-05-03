package moteur.Graphique;

import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import moteur.Fenetre;
import moteur.ILogiqueGui;
import moteur.scene.Scene;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class RenduGui {

    private MeshGui meshGui;

    private GLFWKeyCallback prevKeyCallback;

    private Vector2f taille;

    private ShaderChargeur shaderChargeur;

    private Texture texture;

    private UniformsMap uniformsMap;

    public RenduGui(Fenetre fenetre) {

        //créer les shadeurs
        List<ShaderChargeur.donneeShader> shaders = new ArrayList<>();
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/gui/gui.vert", GL_VERTEX_SHADER));
        shaders.add(new ShaderChargeur.donneeShader("ressources/shaders/gui/gui.frag", GL_FRAGMENT_SHADER));
        shaderChargeur = new ShaderChargeur(shaders);


        //créer les uniforms
        uniformsMap = new UniformsMap(shaderChargeur.getIdProgramme());
        uniformsMap.creerUniform("taille");
        taille = new Vector2f();

        //loader les ressources
        creerUIRessources(fenetre);

        //lier les key
        setupKeyCallBack(fenetre);

    }

    public void detruireProgramme() {
        shaderChargeur.detruireProgramme();
        texture.detruireProgramme();
        if (prevKeyCallback != null)
            prevKeyCallback.free();
    }

    private void creerUIRessources(Fenetre fenetre) {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(fenetre.getLargeur(), fenetre.getHauteur());

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt largeur = new ImInt();
        ImInt hauteur = new ImInt();
        ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(largeur, hauteur);
        texture = new Texture(largeur.get(), hauteur.get(), buf);
        meshGui = new MeshGui();
    }

    public void redimensionner(int largeur, int hauteur) {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(largeur, hauteur);
    }

    public void rendre(Scene scene) {
        ILogiqueGui logiqueGui = scene.getLogiqueGui();
        //si on rien à rendre, on rend rien
        if (logiqueGui == null) {
            return;
        }

        //update le rendu du Gui
        logiqueGui.dessinerGui();

        shaderChargeur.utiliser();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glBindVertexArray(meshGui.getIdVao());

        glBindBuffer(GL_ARRAY_BUFFER, meshGui.getVerticlesVbo());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, meshGui.getIndicesVbo());

        ImGuiIO io = ImGui.getIO();
        taille.x = 2.0f / io.getDisplaySizeX();
        taille.y = -2.0f / io.getDisplaySizeY();
        uniformsMap.setUniform("taille", taille);

        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for (int i = 0; i < numLists; i++) {
            glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);

            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for (int j = 0; j < numCmds; j++) {
                final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                texture.lier();
                glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
            }
        }

        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
    }


    private void setupKeyCallBack(Fenetre fenetre) {
        ImGuiIO io = ImGui.getIO();
        //lier les entrés avec ImGui
        io.setKeyMap(ImGuiKey.Tab, GLFW_KEY_TAB);
        io.setKeyMap(ImGuiKey.LeftArrow, GLFW_KEY_LEFT);
        io.setKeyMap(ImGuiKey.RightArrow, GLFW_KEY_RIGHT);
        io.setKeyMap(ImGuiKey.UpArrow, GLFW_KEY_UP);
        io.setKeyMap(ImGuiKey.DownArrow, GLFW_KEY_DOWN);
        io.setKeyMap(ImGuiKey.PageUp, GLFW_KEY_PAGE_UP);
        io.setKeyMap(ImGuiKey.PageDown, GLFW_KEY_PAGE_DOWN);
        io.setKeyMap(ImGuiKey.Home, GLFW_KEY_HOME);
        io.setKeyMap(ImGuiKey.End, GLFW_KEY_END);
        io.setKeyMap(ImGuiKey.Insert, GLFW_KEY_INSERT);
        io.setKeyMap(ImGuiKey.Delete, GLFW_KEY_DELETE);
        io.setKeyMap(ImGuiKey.Backspace, GLFW_KEY_BACKSPACE);
        io.setKeyMap(ImGuiKey.Space, GLFW_KEY_SPACE);
        io.setKeyMap(ImGuiKey.Enter, GLFW_KEY_ENTER);
        io.setKeyMap(ImGuiKey.Escape, GLFW_KEY_ESCAPE);
        io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW_KEY_KP_ENTER);

        //

        prevKeyCallback = glfwSetKeyCallback(fenetre.getRefFenetre(), (ref, touche, scancode, action, mods) -> {
                    fenetre.keyCallBack(touche, action);
                    if (!io.getWantCaptureKeyboard()) {
                        if (prevKeyCallback != null) {
                            prevKeyCallback.invoke(ref, touche, scancode, action, mods);
                        }
                        return;
                    }
                    if (action == GLFW_PRESS) {
                        io.setKeysDown(touche, true);
                    } else if (action == GLFW_RELEASE) {
                        io.setKeysDown(touche, false);
                    }
                    io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
                    io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
                    io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
                    io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
                }
        );

        glfwSetCharCallback(fenetre.getRefFenetre(), (handle, c) -> {
            if (!io.getWantCaptureKeyboard()) {
                return;
            }
            io.addInputCharacter(c);
        });
    }


}
