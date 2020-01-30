package nexus.core.input;

import com.artemis.ComponentMapper;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import imgui.ImGui;
import imgui.ImGuiIO;
import nexus.context.Context;
import nexus.core.defaults.IteratingInitSystem;
import nexus.window.Display;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Updates the input using the input system
 */
@One(Input.class)
public class InputSystem extends IteratingInitSystem {
    private Display display;
    private Context coreContext;
    public ComponentMapper<Input> mInput;  // used to access component.
    private final int DOUBLE_CLICKED = 3;
    private final int NO_STATE = -1;
    private final int KEYBOARD_SIZE = 512;
    private final int MOUSE_SIZE = 16;
    private int[] keyStates = new int[KEYBOARD_SIZE];
    private int[] mouseStates = new int[MOUSE_SIZE];
    private long lastMouseClick = 0;
    private long doubleClickPeriod = 1000000000 / 5; //5th of a second for double click.
    private float mx = -1, my = -1, dx = -1, dy = -1;
    private Input inputComponent;

    public InputSystem(Context coreContext) {
        this.coreContext = coreContext;
    }


    /**
     * Create the input and register the callbacks
     */
    public void postInitialization() {
        display = coreContext.get(Display.class);
        createInput();
        createCallbacks();
    }

    /**
     * Create the input entity
     */
    private void createInput() {
        inputComponent = new Input();
        inputComponent.addStates(keyStates, mouseStates);
        int inputEntity = world.create();
        world.edit(inputEntity).add(inputComponent);
        coreContext.put(Input.class, inputComponent);
    }

    /**
     * Create the callbacks for input
     */
    private void createCallbacks() {
        ImGuiIO io = coreContext.get(ImGuiIO.class);
        glfwSetInputMode(display.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        inputComponent.setGrabbed(true);
        // Here goes GLFW callbacks to update user input stuff in ImGui
        glfwSetKeyCallback(display.getWindow(), (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            if (key == GLFW_KEY_INSERT && action == GLFW_PRESS) {
                glfwSetInputMode(display.getWindow(), GLFW_CURSOR, !inputComponent.isGrabbed() ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
                inputComponent.setGrabbed(!inputComponent.isGrabbed());
            }
            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
            keyStates[key] = action;
        });

        glfwSetCharCallback(display.getWindow(), (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(display.getWindow(), (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

            mouseStates[button] = action;
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                long last = lastMouseClick;
                boolean released = mouseStates[button] == GLFW_RELEASE;
                if (released)
                    lastMouseClick = System.nanoTime();
                long now = System.nanoTime();
                if (released && now - last < doubleClickPeriod) {
                    lastMouseClick = 0;
                    //We double clicked
                    mouseStates[button] = DOUBLE_CLICKED;
                }
            }
        });

        glfwSetScrollCallback(display.getWindow(), (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
        });

        glfwSetCursorPosCallback(display.getWindow(), (window, x, y) -> {
            if (mx != -1 && my != -1) {
                dx = (float) x - mx;
                dy = (float) y - my;
            }
            this.mx = (float) x;
            this.my = (float) y;
        });
    }


    /**
     * Update the input components, theoretically there should only
     * be one input component, but it's possible to use multiple for some reason
     *
     * @param entityId the input entity id
     */
    protected void process(int entityId) {
        Input input = mInput.get(entityId);
        input.updateMouse(mx, my, dx, dy);
    }

    /**
     * Reset all of the states and poll the input
     */
    protected void end() {
        resetState();
    }

    /**
     * Resets all the mouse states to no state
     * as well as checks the time on the double click
     */
    private void resetState() {
        long now = System.nanoTime();
        if (now - lastMouseClick > doubleClickPeriod) {
            dx = 0;
            dy = 0;
            lastMouseClick = 0;
        }
    }
}
