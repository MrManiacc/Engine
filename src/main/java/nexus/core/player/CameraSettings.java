package nexus.core.player;

import com.artemis.Component;
import lombok.Getter;
import lombok.Setter;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Stores various information needed for the local player
 */
public class CameraSettings extends Component {
    @Getter
    @Setter
    private int
            forwardKey = GLFW_KEY_W,
            backwardKey = GLFW_KEY_S,
            leftKey = GLFW_KEY_A,
            rightKey = GLFW_KEY_D,
            upKey = GLFW_KEY_SPACE,
            downKey = GLFW_KEY_LEFT_CONTROL;
    @Getter
    @Setter
    private float cameraSensitivity = 5f, cameraSpeed = 1.0f;
    @Getter
    @Setter
    public boolean activated = true;

}
