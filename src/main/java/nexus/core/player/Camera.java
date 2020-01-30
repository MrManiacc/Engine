package nexus.core.player;

import com.artemis.Component;
import lombok.Getter;
import nexus.context.Context;
import nexus.window.Display;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;

/**
 * Represents a camera in the game
 */
public class Camera extends Component {
    @Getter
    private final Matrix4f viewMatrix;
    @Getter
    private final Matrix4f projectionMatrix;
    private Vector3f forward = new Vector3f(), up = new Vector3f(), right = new Vector3f();

    public Camera() {
        viewMatrix = new Matrix4f().identity();
        projectionMatrix = new Matrix4f().identity();
    }

    /**
     * Creates a new camera using the fov, near and far
     *
     * @param fov  the field of vision
     * @param near near clip plane
     * @param far  far clip plane
     * @return a new camera instance
     */
    public static Camera newCamera(Context coreContext, float fov, float near, float far) {
        Camera camera = new Camera();
        Display display = coreContext.get(Display.class);
        camera.projectionMatrix.setPerspective((float) Math.toRadians(fov), (float) (display.getFrameWidth() / display.getFrameHeight()), near, far);
        return camera;
    }

    /**
     * Gets the camera forward vector
     *
     * @return camera forward
     */
    public Vector3f getForward() {
        viewMatrix.positiveZ(forward).negate();
        return forward;
    }


    /**
     * Gets the camera forward vector
     *
     * @return camera forward
     */
    public Vector3f getRight() {
        viewMatrix.positiveX(right);
        return right;
    }

    /**
     * Gets the camera forward vector
     *
     * @return camera forward
     */
    public Vector3f getUp() {
        viewMatrix.positiveY(up);
        return up;
    }
}
