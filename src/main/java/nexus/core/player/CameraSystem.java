package nexus.core.player;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import nexus.context.Context;
import nexus.core.defaults.IteratingInitSystem;
import nexus.core.input.Input;
import nexus.core.math.Transform;
import nexus.window.Display;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.text.DecimalFormat;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Moves the camera accordingly
 */
@All({Transform.class, Camera.class, CameraSettings.class})
public class CameraSystem extends IteratingInitSystem {
    private Context coreContext;
    private Input input;
    private Display display;
    public ComponentMapper<Transform> mTransform;
    public ComponentMapper<Camera> mCamera;
    public ComponentMapper<CameraSettings> mCameraSettings;
    private final Vector3f rotX = new Vector3f(1, 0, 0), rotY = new Vector3f(0, 1, 0), rotZ = new Vector3f(0, 0, 1);
    private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;

    public CameraSystem(Context coreContext) {
        this.coreContext = coreContext;
    }

    /**
     * Create the camera entity
     */
    public void postInitialization() {
        this.coreContext.put(LocalPlayer.class, new LocalPlayer(coreContext));
        input = coreContext.get(Input.class);
        display = coreContext.get(Display.class);
        input.setGrabbed(true);
    }

    /**
     * Update the camera entity
     *
     * @param entityID the entity to process
     */
    protected void process(int entityID) {
        Transform transform = mTransform.get(entityID);
        Camera camera = mCamera.get(entityID);
        CameraSettings settings = mCameraSettings.get(entityID);
        if (input.isGrabbed()) {
            updatePosition(transform, camera, settings);
            updateRotation(transform, settings);
            updateView(transform, camera);
        }
    }

    /**
     * Update the position of the camera
     *
     * @param transform      camera transform
     * @param cameraSettings camera settings
     */
    private void updatePosition(Transform transform, Camera camera, CameraSettings cameraSettings) {
        Vector3f forward = camera.getForward().mul(cameraSettings.getCameraSpeed() * world.delta);
        Vector3f right = camera.getRight().mul(cameraSettings.getCameraSpeed() * world.delta);
        if (input.isKeyDown(cameraSettings.getForwardKey()))
            transform.incrementPosition(forward.x, 0, forward.z);
        if (input.isKeyDown(cameraSettings.getBackwardKey()))
            transform.incrementPosition(-forward.x, 0, -forward.z);
        if (input.isKeyDown(cameraSettings.getLeftKey()))
            transform.incrementPosition(-right.x, 0, -right.z);
        if (input.isKeyDown(cameraSettings.getRightKey()))
            transform.incrementPosition(right.x, 0, right.z);
        if (input.isKeyDown(cameraSettings.getUpKey()))
            transform.incrementPosition(0, cameraSettings.getCameraSpeed() * world.delta, 0);
        if (input.isKeyDown(cameraSettings.getDownKey()))
            transform.incrementPosition(0, -cameraSettings.getCameraSpeed() * world.delta, 0);
    }

    /**
     * Updates the camera rotation
     */
    private void updateRotation(Transform transform, CameraSettings cameraSettings) {
        transform.getRotation().add((input.getMouseDelta().y * cameraSettings.getCameraSensitivity()) * world.delta, (input.getMouseDelta().x * cameraSettings.getCameraSensitivity()) * world.delta, 0);
    }

    /**
     * Updates the camera's view matrix
     */
    private void updateView(Transform transform, Camera camera) {
        camera.getViewMatrix()
                .identity()
                .rotateX(transform.getRotation().x)
                .rotateY(transform.getRotation().y)
                .rotate(0, rotZ)
                .translate(-transform.getPosition().x, -transform.getPosition().y, -transform.getPosition().z);
    }
}
