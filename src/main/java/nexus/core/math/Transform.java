package nexus.core.math;

import com.artemis.Component;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents a transform,
 * position, scale and rotation.
 * Also stores a matrix for the component
 */
public class Transform extends Component {
    @Getter

    private final Vector3f position, scale, rotation;
    private final Matrix4f worldMatrix;
    private final Vector3f rotX = new Vector3f(1, 0, 0), rotY = new Vector3f(0, 1, 0), rotZ = new Vector3f(0, 0, 1);

    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.rotation = new Vector3f(0, 0, 0);
        this.worldMatrix = new Matrix4f().identity();
    }


    /**
     * Update the position
     */
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    /**
     * Update the position
     */
    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }


    /**
     * Adds to the position
     *
     * @param dx added to x component
     * @param dy added to y component
     * @param dz added to z component
     */
    public void incrementPosition(float dx, float dy, float dz) {
        setX(position.x + dx);
        setY(position.y + dy);
        setZ(position.z + dz);
    }

    /**
     * Set the x position
     *
     * @param x x pos
     */
    public void setX(float x) {
        this.position.x = x;
    }

    /**
     * Set the y position
     *
     * @param y y pos
     */
    public void setY(float y) {
        this.position.y = y;
    }

    /**
     * Set the z position
     *
     * @param z z pos
     */
    public void setZ(float z) {
        this.position.z = z;
    }

    /**
     * Update the rotation
     */
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    /**
     * Set the x rotation
     *
     * @param x x rot
     */
    public void setRotX(float x) {
        this.rotation.x = x;
    }

    /**
     * Set the y rotation
     *
     * @param y y rot
     */
    public void setRotY(float y) {
        this.rotation.y = y;
    }

    /**
     * Set the z rotation
     *
     * @param z z rot
     */
    public void setRotZ(float z) {
        this.rotation.z = z;
    }

    /**
     * Update the scale
     */
    public void setScale(float x, float y, float z) {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }

    /**
     * Set the x scale
     *
     * @param x rot
     */
    public void setScaleX(float x) {
        this.scale.x = x;
    }

    /**
     * Set the y scale
     *
     * @param y scale
     */
    public void setScaleY(float y) {
        this.scale.y = y;
    }

    /**
     * Set the z scale
     *
     * @param z scale
     */
    public void setScaleZ(float z) {
        this.scale.z = z;
    }

    /**
     * Gets the transformation matrix after applying the correct values
     *
     * @return world matrix
     */
    public Matrix4f getMatrix() {
        worldMatrix.identity();
        worldMatrix.translate(position);
        worldMatrix.rotate(rotation.x, rotX);
        worldMatrix.rotate(rotation.y, rotY);
        worldMatrix.rotate(rotation.z, rotZ);
        worldMatrix.scale(scale);
        return worldMatrix;
    }


}
