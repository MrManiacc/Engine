package nexus.util.opengl;

import lombok.Getter;

import java.io.File;
import java.util.Arrays;

/**
 * Stores all of the data needed for a model
 */
public class RawMesh {
    @Getter
    private float[] vertices;
    @Getter
    private float[] normals;
    @Getter
    private float[] tangents;
    @Getter
    private float[] textureCoords;
    @Getter
    private int[] indices;
    @Getter
    private int[] boneIds;
    @Getter
    private float[] boneWeights;
    @Getter
    private boolean skeleton;
    @Getter
    private String name;
    @Getter
    private File file;

    /**
     * Create a raw mesh that has a skeleton
     */
    public RawMesh(String name, float[] vertices, float[] normals, float[] tangents, float[] textureCoords, int[] indices, int[] boneIds, float[] boneWeights) {
        this.name = name;
        this.vertices = vertices;
        this.normals = normals;
        this.tangents = tangents;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.boneIds = boneIds;
        this.boneWeights = boneWeights;
        this.skeleton = true;
    }

    /**
     * Create a mesh that is static and doesn't contain a skeleton
     */
    public RawMesh(String name, float[] vertices, float[] normals, float[] tangents, float[] textureCoords, int[] indices) {
        this.name = name;
        this.vertices = vertices;
        this.normals = normals;
        this.tangents = tangents;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.skeleton = false;
    }

    /**
     * Deserialize from a file
     *
     * @param file bin file
     */
    public RawMesh(File file) {
        this.file = file;
    }

    /**
     * Convert the raw mesh into a vao which is readable by opengl
     * IMPORTANT - THIS MUCH BE RAN AFTER OPENGL HAS BEEN INITIALIZED
     *
     * @return a vao containing the proper data
     */
    public Vao toVao() {
        Vao vao;
        if (skeleton) {
            vao = Vao.create(6);
            vao.bind();
            vao.createAttribute(vertices, 4);
            vao.createAttribute(normals, 3);
            vao.createAttribute(tangents, 3);
            vao.createAttribute(textureCoords, 2);
            vao.createAttribute(boneWeights, 4);
            vao.createIntAttribute(boneIds, 4);
        } else {
            vao = Vao.create(4);
            vao.bind();
            vao.createAttribute(vertices, 4);
            vao.createAttribute(normals, 3);
            vao.createAttribute(tangents, 3);
            vao.createAttribute(textureCoords, 2);
        }
        vao.createIndexBuffer(indices);
        vao.setIndexCount(indices.length);
        vao.unbind();
        return vao;
    }

    public String toString() {
        return name
                + "\n" + Arrays.toString(vertices)
                + "\n" + Arrays.toString(textureCoords)
                + "\n" + Arrays.toString(normals)
                + "\n" + Arrays.toString(indices)
                + "\n" + Arrays.toString(boneIds)
                + "\n" + Arrays.toString(boneWeights);
    }

}
