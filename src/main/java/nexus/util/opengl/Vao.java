package nexus.util.opengl;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a stored portion of vertices in opengl memory
 */
public class Vao {
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_INT = 4;
    public final int id;
    private List<Vbo> dataVbos = new ArrayList<>();
    private Vbo indexVbo;
    private int indexCount;
    private int attributes;
    private int attribPtr = 0;

    private Vao(int id, int attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    /**
     * this creates a new opengl vao instance
     *
     * @param attributes the number of attributes to bind
     * @return new vao instance
     */
    public static Vao create(int attributes) {
        int id = GL30.glGenVertexArrays();
        return new Vao(id, attributes);
    }

    public void setIndexCount(int count) {
        this.indexCount = count;
    }


    public int getIndexCount() {
        return indexCount;
    }

    /**
     * Binds a vao and the specified vbo attributes
     */
    public void bind() {
        GL30.glBindVertexArray(id);
        for (int i = 0; i < attributes; i++) {
            GL20.glEnableVertexAttribArray(i);
        }
    }

    /**
     * Unbinds a vao and it's corresponding vbo id's
     */
    public void unbind() {
        for (int i = 0; i < attributes; i++) {
            GL20.glDisableVertexAttribArray(i);
        }
        GL30.glBindVertexArray(0);
    }

    /**
     * Stores the indices for the vao into a opengl buffer
     *
     * @param indices the indices for the model
     */
    public void createIndexBuffer(int[] indices) {
        this.indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVbo.bind();
        indexVbo.storeData(indices);
        this.indexCount = indices.length;
    }

    /**
     * Stores the indices for the vao into a opengl buffer
     */
    public void createIndexBuffer(IntBuffer data) {
        this.indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVbo.bind();
        indexVbo.storeData(data);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createAttribute(float[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribPtr++, attrSize, GL11.GL_FLOAT, false, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createAttribute(FloatBuffer data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribPtr++, attrSize, GL11.GL_FLOAT, false, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createIntAttribute(int[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL30.glVertexAttribIPointer(attribPtr++, attrSize, GL11.GL_INT, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Creates a vbo to store some specific data into. This is used for rendering
     *
     * @param data     the data to be put into the vbo
     * @param attrSize the size 3 for 3d and 2 for 2d
     */
    public void createIntAttribute(IntBuffer data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL30.glVertexAttribIPointer(attribPtr++, attrSize, GL11.GL_INT, 0, 0);
        dataVbo.unbind();
        dataVbos.add(dataVbo);
    }

    /**
     * Unloads the vao from memory
     */
    public void delete() {
        GL30.glDeleteVertexArrays(id);
        for (Vbo vbo : dataVbos) {
            vbo.delete();
        }
        indexVbo.delete();
    }

}

