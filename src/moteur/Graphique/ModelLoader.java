package moteur.Graphique;

import Outil.CouleurConsole;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.assimp.Assimp.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Pointer;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {


    private ModelLoader() {

    }

    public static Model loadModel(String id, String chemin, TextureCache textureCache) {

        return loadModel(id,chemin,textureCache, aiProcess_GenSmoothNormals |
                aiProcess_JoinIdenticalVertices | //joindre les verticles similaires pour réduire le nombre
                aiProcess_Triangulate | //dire que l'on veut tout split en triangles
                aiProcess_FixInfacingNormals | //fix quelque chose dans la normal
                aiProcess_PreTransformVertices);

    }

    public static Model loadModel(String id, String chemin, TextureCache textureCache, int flags) {

        //trouvez le dossier
        File file = new File(chemin);

        if (!file.exists())
            throw new IllegalArgumentException("Ce fichier n'existe pas [" + chemin + "]");

        String dossierModel = file.getParent();

        AIScene aiScene = aiImportFile(chemin,flags);
        if (aiScene == null)
            System.err.println("[ERREUR] Incapable de loader le model : " + chemin);

        //créer la liste de material
        int numMaterials = aiScene.mNumMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0 ; i < numMaterials ; i ++) {

            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materials.add(loadMaterial(aiMaterial,dossierModel,textureCache));

        }

        //créer la liste des meshes
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material materialDefaut = new Material();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = loadMesh(aiMesh);
            int idxMaterial = aiMesh.mMaterialIndex();
            Material material;
            if (idxMaterial >= 0 && idxMaterial < materials.size()) {
                material = materials.get(idxMaterial);
            }
            else
                material = materialDefaut;

            material.getMeshList().add(mesh);
            //si un material defaut doit être loader, l'ajouter
            if (!materialDefaut.getMeshList().isEmpty())
                materials.add(materialDefaut);
        }

        return new Model(id,materials);
    }

    private static Mesh loadMesh(AIMesh mesh) {
        float[] verticles = loadVerticles(mesh);
        float[] textCoords = loadTextCoord(mesh);
        int[] indices = loadIndices(mesh);

        //collisionBox
        AIAABB aabb = mesh.mAABB();
        Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
        Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());
        //System.out.println(CouleurConsole.JAUNE.couleur + aabbMin.x);
        //System.out.println(CouleurConsole.JAUNE.couleur + aabbMax.x);

        if (textCoords.length == 0) {
            int numElements = (verticles.length / 3) * 2;
            textCoords = new float[numElements];
        }

        return new Mesh(verticles, textCoords, indices, aabbMin, aabbMax);
    }

    private static int[] loadIndices(AIMesh mesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = mesh.mNumFaces();
        AIFace.Buffer faces = mesh.mFaces();
        for (int i = 0 ; i < numFaces; i ++) {
            AIFace face = faces.get(i);
            IntBuffer buffer = face.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private static float[] loadVerticles(AIMesh mesh) {
        AIVector3D.Buffer buffer = mesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos] = textCoord.x();
            data[pos+1] = textCoord.y();
            data[pos+2] = textCoord.z();
            pos+=3;
        }
        return data;
    }

    private static float[] loadTextCoord(AIMesh mesh) {
        AIVector3D.Buffer buffer = mesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos] = textCoord.x();
            data[pos+1] = 1 - textCoord.y();
            pos+=2;
        }
        return data;
    }
    private static Material loadMaterial(AIMaterial aiMaterial, String dossierModel, TextureCache textureCache) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D couleur = AIColor4D.create();

            int resultat = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE,0,
                    couleur);

            if (resultat == aiReturn_SUCCESS) {
                material.setCouleurDiffuse(new Vector4f(couleur.r(),couleur.g(),couleur.b(),couleur.a()));
            }

            AIString aiCheminTexture = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiCheminTexture, (IntBuffer) null,
                    null, null, null, null, null);
            String cheminTexture = aiCheminTexture.dataString();
            if (!cheminTexture.isEmpty()) {
                material.setCheminTexture(dossierModel + File.separator + new File(cheminTexture).getName());
                textureCache.creerTexture(material.getCheminTexture());
                material.setCouleurDiffuse(Material.COULEUR_DEFAUT);
            }

            return material;
        }
    }



}