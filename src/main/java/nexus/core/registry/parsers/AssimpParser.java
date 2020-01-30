package nexus.core.registry.parsers;

import lombok.SneakyThrows;
import nexus.core.animation.AnimatedFrame;
import nexus.core.animation.Bone;
import nexus.core.animation.Node;
import nexus.core.animation.VertexWeight;
import nexus.core.render.Animation;
import nexus.util.CommonUtils;
import nexus.util.opengl.RawMesh;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

/**
 * This class will does various tasks to model
 * files to generate uniform outputs
 */
public class AssimpParser {
    private static final Map<String, List<Animation>> cache = new HashMap<>();
    public static final int DEFAULT_PARAMETERS =
            Assimp.aiProcess_Triangulate |
                    Assimp.aiProcess_GenSmoothNormals |
                    Assimp.aiProcess_FlipUVs |
                    Assimp.aiProcess_CalcTangentSpace |
                    Assimp.aiProcess_LimitBoneWeights |
                    Assimp.aiProcess_JoinIdenticalVertices;


    /**
     * This method will grab all the meshes from a given file
     *
     * @param file the file to parse
     * @return an array of parses meshes
     */
    @SneakyThrows
    public static Optional<RawMesh[]> parseMeshes(File file, int... parameters) {
        Optional<AIScene> scene = createScene(file, parameters);
        if (scene.isEmpty()) return Optional.empty();
        RawMesh[] meshes = new RawMesh[scene.get().mNumMeshes()];
        List<Bone> bones = new ArrayList<>();
        String name = CommonUtils.removeExtension(file);
        for (int i = 0; i < meshes.length; i++)
            meshes[i] = parseMesh(AIMesh.create(Objects.requireNonNull(scene.get().mMeshes()).get(i)), bones, name + i);
        Optional<List<Animation>> animations = Optional.of(buildAnimationMap(scene.get(), bones));
        animations.ifPresent(animationList -> cache.put(file.getPath(), animationList));
        return Optional.of(meshes);
    }

    /**
     * This method will parse all of the animations for a given file.
     * It will work best if the bones array is already built/if the mesh
     * has already been processed
     *
     * @param file       the file to parse for
     * @param parameters the parameters to parse for
     * @return returns a map of the animations
     */
    @SneakyThrows
    public static Optional<List<Animation>> parseAnimations(File file, int... parameters) {
        if (cache.containsKey(file.getPath())) {
            return Optional.of(cache.get(file.getPath()));
        }
        Optional<AIScene> scene = createScene(file, parameters);
        if (scene.isEmpty()) return Optional.empty();
        String name = CommonUtils.removeExtension(file);
        List<Bone> bones = new ArrayList<>();
        for (int i = 0; i < scene.get().mNumMeshes(); i++)
            parseMesh(AIMesh.create(Objects.requireNonNull(scene.get().mMeshes()).get(i)), bones, name + i);
        return Optional.of(buildAnimationMap(scene.get(), bones));
    }

    /**
     * This method will parse an individual mesh file into a raw mesh format
     *
     * @param mesh the mesh to parse
     * @return a parsed mesh file
     */
    private static RawMesh parseMesh(AIMesh mesh, List<Bone> bones, String name) {
        ByteBuffer vertexArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 4 * Float.BYTES);
        AIVector3D.Buffer vertices = mesh.mVertices();

        ByteBuffer normalArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
        AIVector3D.Buffer normals = mesh.mNormals();

        ByteBuffer tangentsArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 3 * Float.BYTES);
        AIVector3D.Buffer tangents = mesh.mTangents();
        ByteBuffer texArrayBufferData = BufferUtils.createByteBuffer(mesh.mNumVertices() * 2 * Float.BYTES);

        for (int i = 0; i < mesh.mNumVertices(); ++i) {
            AIVector3D vert = vertices.get(i);
            vertexArrayBufferData.putFloat(vert.x());
            vertexArrayBufferData.putFloat(vert.y());
            vertexArrayBufferData.putFloat(vert.z());
            vertexArrayBufferData.putFloat(1f);

            AIVector3D norm = normals.get(i);
            normalArrayBufferData.putFloat(norm.x());
            normalArrayBufferData.putFloat(norm.y());
            normalArrayBufferData.putFloat(norm.z());

            AIVector3D tang = tangents.get(i);
            tangentsArrayBufferData.putFloat(tang.x());
            tangentsArrayBufferData.putFloat(tang.y());
            tangentsArrayBufferData.putFloat(tang.z());

            if (mesh.mNumUVComponents().get(0) != 0) {
                AIVector3D texture = mesh.mTextureCoords(0).get(i);
                texArrayBufferData.putFloat(texture.x()).putFloat(texture.y());
            } else {
                texArrayBufferData.putFloat(0).putFloat(0);
            }
        }
        List<Integer> boneIds = new ArrayList<>();
        List<Float> boneWeights = new ArrayList<>();

        vertexArrayBufferData.flip();
        normalArrayBufferData.flip();
        tangentsArrayBufferData.flip();
        texArrayBufferData.flip();
        IntBuffer indices = parseMeshIndices(mesh);
        indices.flip();

        float[] verts = CommonUtils.toFloatArray(vertexArrayBufferData);
        float[] norms = CommonUtils.toFloatArray(normalArrayBufferData);
        float[] tangs = CommonUtils.toFloatArray(tangentsArrayBufferData);
        float[] texCoords = CommonUtils.toFloatArray(texArrayBufferData);
        int[] inds = CommonUtils.toIntArray(indices);

        if (parseBones(mesh, bones, boneIds, boneWeights)) {
            return new RawMesh(name, verts, norms,
                    tangs, texCoords, inds,
                    CommonUtils.toIntArray(boneIds), CommonUtils.toFloatArray(boneWeights));
        }

        return new RawMesh(name, verts, norms,
                tangs, texCoords, inds);
    }

    /**
     * Parses the bones structure, will return true if there are in fact bones,
     * or false if the mesh doesn't contain any bones
     *
     * @param mesh    the mesh to parse bones for
     * @param boneIds the list of bone ids, which will be outputted
     * @param weights the list of bone weights which will be outputted
     * @return returns true if the mesh contains bones or false if not
     */
    private static boolean parseBones(AIMesh mesh, List<Bone> bones, List<Integer> boneIds, List<Float> weights) {
        int numBones = mesh.mNumBones();
        if (numBones == 0) return false;
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        PointerBuffer aiBones = mesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = bones.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), CommonUtils.toMatrix(aiBone.mOffsetMatrix()));
            bones.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.computeIfAbsent(vw.getVertexId(), k -> new ArrayList<>());
                vertexWeightList.add(vw);
            }
        }
        int numVertices = mesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            int MAX_WEIGHTS = 4;
            for (int j = 0; j < MAX_WEIGHTS; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.getWeight());
                    boneIds.add(vw.getBoneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }
        return true;
    }


    /**
     * Parses the indices for the mesh
     *
     * @param mesh the mesh to parse
     * @return a buffer containing the indices
     */
    private static IntBuffer parseMeshIndices(AIMesh mesh) {
        int faceCount = mesh.mNumFaces();
        int elementCount = faceCount * 3;
        IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer facesBuffer = mesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBufferData.put(face.mIndices());
        }
        return elementArrayBufferData;
    }


    /**
     * Builds the map of animations form the scene and bones
     *
     * @param scene the scene to process
     * @param bones the bones to parse
     * @return the map of animations with their given names
     */
    private static List<Animation> buildAnimationMap(AIScene scene, List<Bone> bones) {
        AINode aiRootNode = scene.mRootNode();
        Matrix4f rootTransformation = CommonUtils.toMatrix(aiRootNode.mTransformation());
        Node rootNode = buildNodeHierarchy(aiRootNode, null);

        List<Animation> animations = new ArrayList<>();
        int numAnimations = scene.mNumAnimations();
        PointerBuffer aiAnimations = scene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int numChanels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();
            for (int j = 0; j < numChanels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                String nodeName = aiNodeAnim.mNodeName().dataString();
                Node node = rootNode.findByName(nodeName);
                buildTransFormationMatrices(aiNodeAnim, node);
            }

            List<AnimatedFrame> frames = buildAnimationFrames(bones, rootNode, rootTransformation);
            Animation animation = new Animation();
            animation.setName(aiAnimation.mName().dataString());
            animation.setFrames(frames);
            animation.setDuration(aiAnimation.mDuration());
            animations.add(animation);
        }
        return animations;
    }

    /**
     * Builds the actual animation frames
     *
     * @param boneList           the list of bones to use to build animations
     * @param rootNode           the root node
     * @param rootTransformation the root transform
     * @return returns a list of animated frames
     */
    private static List<AnimatedFrame> buildAnimationFrames(List<Bone> boneList, Node rootNode, Matrix4f rootTransformation) {
        int numFrames = rootNode.getAnimationFrames();
        List<AnimatedFrame> frameList = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            AnimatedFrame frame = new AnimatedFrame();
            frame.setRootTransformation(new Matrix4f(rootTransformation));
            frameList.add(frame);

            int numBones = boneList.size();
            for (int j = 0; j < numBones; j++) {
                Bone bone = boneList.get(j);
                Node node = rootNode.findByName(bone.getBoneName());
                Matrix4f boneMatrix = Node.getParentTransforms(node, i);
                Matrix4f parentMatrix = new Matrix4f(boneMatrix);
                boneMatrix.mul(bone.getOffsetMatrix());
                frame.setMatrix(j, parentMatrix, bone.getOffsetMatrix(), node.getTimes().isEmpty() ? 0 : node.getTimes().get(i));
            }
        }

        return frameList;
    }


    /**
     * Builds the transformations from the given node
     *
     * @param aiNodeAnim the node animation
     * @param node       the node to build for
     */
    private static void buildTransFormationMatrices(AINodeAnim aiNodeAnim, Node node) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        for (int i = 0; i < numFrames; i++) {
            AIVectorKey aiVecKey = positionKeys.get(i);
            AIVector3D vec = aiVecKey.mValue();

            Matrix4f transfMat = new Matrix4f().translate(vec.x(), vec.y(), vec.z());

            AIQuatKey quatKey = rotationKeys.get(i);
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            transfMat.rotate(quat);
            if (i < aiNodeAnim.mNumScalingKeys()) {
                aiVecKey = scalingKeys.get(i);
                vec = aiVecKey.mValue();
                transfMat.scale(vec.x(), vec.y(), vec.z());
            }
            node.addTransformation(transfMat, (float) quatKey.mTime());
        }
    }


    /**
     * Builds the node hierarchy recursively
     *
     * @param aiNode the node to process
     * @param parent the node parent or null if this is the root node
     * @return the root node that contains all of the children nodes
     */
    private static Node buildNodeHierarchy(AINode aiNode, Node parent) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parent);

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = buildNodeHierarchy(aiChildNode, node);
            node.addChild(childNode);
        }
        return node;
    }

    /**
     * This is a helper method because multiple methods create ai scenes
     *
     * @param file       the file to parse
     * @param parameters the paramters to parse for
     * @return an aiscene
     */
    private static Optional<AIScene> createScene(File file, int... parameters) {
        int params = 0;
        if (parameters.length != 0)
            for (int parameter : parameters) params |= parameter;
        else
            params = DEFAULT_PARAMETERS;
        AIScene scene = Assimp.aiImportFile(file.toString(), params);
        if (scene == null)
            return Optional.empty();
        return Optional.of(scene);
    }
}
