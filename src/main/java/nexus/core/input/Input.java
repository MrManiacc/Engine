package nexus.core.input;

import com.artemis.Component;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a generic input class,
 * this should be a singleton
 */
public class Input extends Component {

    private int[] keyStates;
    private int[] mouseStates;
    private Vector2f mousePos = new Vector2f(-1, -1), mouseDeltaPos = new Vector2f();
    @Getter
    @Setter
    private boolean grabbed = false;

    /**
     * Setup the input with a reference to said states
     *
     * @param keyStates   the key states
     * @param mouseStates the mouse states
     */
    public void addStates(int[] keyStates, int[] mouseStates) {
        this.keyStates = keyStates;
        this.mouseStates = mouseStates;
    }

    /**
     * Updates the mouse position as well as the mouse delta
     *
     * @param x mouse x pos
     * @param y mouse y pos
     */
    public void updateMouse(float x, float y, float dx, float dy) {
        mouseDeltaPos.x = dx;
        mouseDeltaPos.y = dy;
        mousePos.x = x;
        mousePos.y = y;
    }

    /**
     * Gets the position of the mouse
     *
     * @return mouse position
     */
    public Vector2f getMousePosition() {
        return mousePos;
    }

    /**
     * Gets the mouse delta position
     *
     * @return the mouse delta
     */
    public Vector2f getMouseDelta() {
        return mouseDeltaPos;
    }

    /**
     * Checks to see if a key is down or not
     *
     * @param key the key to check
     * @return key state
     */
    public boolean isKeyPressed(int key) {
        return keyStates[key] == GLFW_PRESS;
    }

    /**
     * Checks to see if a key is repeated or not
     *
     * @param key the key to check
     * @return key state
     */
    public boolean isKeyRepeated(int key) {
        return keyStates[key] == GLFW_REPEAT;
    }

    /**
     * Checks to see if a key is released or not
     *
     * @param key the key to check
     * @return key state
     */
    public boolean isKeyReleased(int key) {
        return keyStates[key] == GLFW_RELEASE;
    }

    /**
     * Checks to see if a key is not released
     *
     * @param key the key to check
     * @return the key state
     */
    public boolean isKeyDown(int key) {
        return isKeyPressed(key) || isKeyRepeated(key);
    }

    /**
     * Checks to see if a mouse is down or not
     *
     * @param mouse the mouse to check
     * @return mouse state
     */
    public boolean isMousePressed(int mouse) {
        return mouseStates[mouse] == GLFW_PRESS;
    }

    /**
     * Checks to see if a mouse is repeated or not
     *
     * @param mouse the key to check
     * @return mouse state
     */
    public boolean isMouseRepeated(int mouse) {
        return mouseStates[mouse] == GLFW_REPEAT;
    }

    /**
     * Checks to see if a key is released or not
     *
     * @param key the key to check
     * @return key state
     */
    public boolean isMouseReleased(int key) {
        return mouseStates[key] == GLFW_RELEASE;
    }

    /**
     * Checks to see if a mouse is not released
     *
     * @param mouse the mouse to check
     * @return the mouse state
     */
    public boolean isMouseDown(int mouse) {
        return isMousePressed(mouse) || isMouseRepeated(mouse);
    }


}
