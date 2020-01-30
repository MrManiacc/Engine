package nexus.core.registry.assets;

import com.google.flatbuffers.FlatBufferBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import nexus.core.registry.assets.raw.Models;
import nexus.core.registry.assets.raw.RawModel;
import nexus.core.registry.Pack;
import nexus.core.registry.parsers.AssimpParser;
import nexus.core.render.Model;
import nexus.util.CommonUtils;
import nexus.util.opengl.RawMesh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an mesh in opengl
 */
public class MeshAsset implements IAsset {
    @Getter
    private String name;
    @Getter
    @Setter
    private Pack pack;
    @Getter
    private File file;
    @Getter
    private boolean loaded = false;
    @Getter
    private RawMesh[] meshes;
    private FlatBufferBuilder buffer;
    private boolean binary;

    /**
     * Creates an mesh and gets the file name
     *
     * @param file
     */
    public MeshAsset(File file, boolean binary) {
        this.file = file;
        this.binary = binary;
        try {
            this.name = CommonUtils.removeExtension(file);
            this.buffer = new FlatBufferBuilder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a model component using all of the
     * rawModels
     *
     * @return model component
     */
    public Model toComponent() {
        Model model = new Model();
        for (RawMesh mesh : meshes)
            model.addVao(mesh.toVao());
        return model;
    }

    /**
     * Deserialize the bin file
     */
    public void deserialize() {
        Models models = Models.getRootAsModels(CommonUtils.decodeFile(file));
        this.meshes = new RawMesh[models.meshesLength()];
        for (int i = 0; i < meshes.length; i++) {
            RawModel rawModel = models.meshes(i);
            List<Float> vertices = new ArrayList<>();
            for (int j = 0; j < rawModel.verticesLength(); j++)
                vertices.add(rawModel.vertices(j));
            List<Float> normals = new ArrayList<>();
            for (int j = 0; j < rawModel.normalsLength(); j++)
                normals.add(rawModel.normals(j));
            List<Float> tangents = new ArrayList<>();
            for (int j = 0; j < rawModel.tangentsLength(); j++)
                tangents.add(rawModel.tangents(j));
            List<Float> textureCoords = new ArrayList<>();
            for (int j = 0; j < rawModel.textureCoordinatesLength(); j++)
                textureCoords.add(rawModel.textureCoordinates(j));
            List<Integer> indices = new ArrayList<>();
            for (int j = 0; j < rawModel.indicesLength(); j++)
                indices.add(rawModel.indices(j));
            Collections.reverse(vertices);
            Collections.reverse(normals);
            Collections.reverse(tangents);
            Collections.reverse(textureCoords);
            Collections.reverse(indices);
            if (!rawModel.bones())
                meshes[i] = new RawMesh(rawModel.name(), CommonUtils.toFloatArray(vertices), CommonUtils.toFloatArray(normals), CommonUtils.toFloatArray(tangents), CommonUtils.toFloatArray(textureCoords), CommonUtils.toIntArray(indices));
            else {
                List<Integer> boneIds = new ArrayList<>();
                for (int j = 0; j < rawModel.boneIdsLength(); j++)
                    boneIds.add(rawModel.boneIds(j));
                List<Float> weights = new ArrayList<>();
                for (int j = 0; j < rawModel.boneWeightsLength(); j++)
                    weights.add(rawModel.boneWeights(j));
                Collections.reverse(weights);
                Collections.reverse(boneIds);
                meshes[i] = new RawMesh(rawModel.name(), CommonUtils.toFloatArray(vertices), CommonUtils.toFloatArray(normals), CommonUtils.toFloatArray(tangents), CommonUtils.toFloatArray(textureCoords), CommonUtils.toIntArray(indices), CommonUtils.toIntArray(boneIds), CommonUtils.toFloatArray(weights));
            }

        }
    }

    /**
     * Serialize to file
     */
    @SneakyThrows
    public void serialize() {
        if (!loaded) load();
        int[] meshOffsets = new int[meshes.length];
        int[] namesOffset = new int[meshes.length];
        for (int j = 0; j < meshes.length; j++)
            namesOffset[j] = buffer.createString(meshes[j].getName());
        for (int i = 0; i < meshes.length; i++) {
            RawMesh mesh = meshes[i];
            //---add vertices
            RawModel.startVerticesVector(buffer, mesh.getVertices().length);
            int vertices = CommonUtils.arrayToBuffer(buffer, mesh.getVertices());
            //---add texture coords
            RawModel.startTextureCoordinatesVector(buffer, mesh.getTextureCoords().length);
            int textureCoords = CommonUtils.arrayToBuffer(buffer, mesh.getTextureCoords());
            //---add normals
            RawModel.startNormalsVector(buffer, mesh.getNormals().length);
            int normals = CommonUtils.arrayToBuffer(buffer, mesh.getNormals());
            //---add tangents
            RawModel.startTangentsVector(buffer, mesh.getTangents().length);
            int tangents = CommonUtils.arrayToBuffer(buffer, mesh.getTangents());
            //---add indices
            RawModel.startIndicesVector(buffer, mesh.getIndices().length);
            int indices = CommonUtils.arrayToBuffer(buffer, mesh.getIndices());
            int boneIds = 0;
            int boneWeights = 0;
            if (mesh.isSkeleton()) {
                //---add bone ids
                RawModel.startBoneIdsVector(buffer, mesh.getBoneIds().length);
                boneIds = CommonUtils.arrayToBuffer(buffer, mesh.getBoneIds());
                //---add bone weights
                RawModel.startBoneWeightsVector(buffer, mesh.getBoneWeights().length);
                boneWeights = CommonUtils.arrayToBuffer(buffer, mesh.getBoneWeights());
            }
            //---start model
            RawModel.startRawModel(buffer);
            //---add bone boolean
            RawModel.addBones(buffer, mesh.isSkeleton());
            //---add name
            RawModel.addName(buffer, namesOffset[i]);
            //---add vertices
            RawModel.addVertices(buffer, vertices);
            //---add texture coords
            RawModel.addTextureCoordinates(buffer, textureCoords);
            //---add normals
            RawModel.addNormals(buffer, normals);
            //---add tangents
            RawModel.addTangents(buffer, tangents);
            //---add indices
            RawModel.addIndices(buffer, indices);
            if (mesh.isSkeleton()) {
                //---add bone ids
                RawModel.addBoneIds(buffer, boneIds);
                //---add bone weights
                RawModel.addBoneWeights(buffer, boneWeights);
            }
            int model = RawModel.endRawModel(buffer);
            meshOffsets[i] = model;
        }

        int meshes = Models.createMeshesVector(buffer, meshOffsets);
        Models.startModels(buffer);
        Models.addMeshes(buffer, meshes);
        int model = Models.endModels(buffer);
        Models.finishModelsBuffer(buffer, model);
        FileOutputStream fos = new FileOutputStream(new File(file.getParentFile().getPath() + File.separator + name + ".bin"));
        fos.write(buffer.sizedByteArray());
        fos.close();
    }

    /**
     * Loads an mesh to opengl
     */
    public void load() {
        if (!loaded) {
            //TODO: load mesh
            if (binary) deserialize();
            else{
                AssimpParser.parseMeshes(file).ifPresent(rawMeshes -> {
                    this.loaded = true;
                    this.meshes = rawMeshes;
                });
                serialize();
            }

        }
    }

    /**
     * Unloads an mesh from opengl
     */
    public void unload() {
        if (loaded) {
            //TODO unload mesh
            loaded = false;
        }
    }
}
