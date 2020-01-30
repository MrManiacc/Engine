package nexus.window;

import imgui.ImBool;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImString;
import imgui.enums.*;
import imgui.gl3.ImGuiImplGl3;
import nexus.context.Context;
import nexus.engine.CoreEngine;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Handles the imgui lwjgl backend calls
 */
public class GuiState {
    private final int[] winWidth = new int[1];
    private final int[] winHeight = new int[1];
    private final int[] fbWidth = new int[1];
    private final int[] fbHeight = new int[1];
    private final double[] mousePosX = new double[1];
    private final double[] mousePosY = new double[1];
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private Display display;
    private float[] backgroundColor = new float[]{0.1f, 0.34f, 0.28f};

    public GuiState(Display display) {
        this.display = display;
    }

    // Here we will initialize ImGui stuff.
    public void initImGui(Context coreContext) {
        // IMPORTANT!!
        // This line is critical for ImGui to work.
        ImGui.createContext();

        // ImGui provides three different color schemas for styling. We will use the classic one here.
        ImGui.styleColorsClassic();
        // ImGui.StyleColorsDark(); // This is a default style for ImGui
        // ImGui.StyleColorsLight();

        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        coreContext.put(ImGuiIO.class, io);

        io.setIniFilename(null);
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors);
        io.setBackendPlatformName("imgui_java_impl_glfw");
        io.setBackendRendererName("imgui_java_impl_lwjgl");

        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
        io.setKeyMap(keyMap);

        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);


        // Initialize renderer itself
        imGuiGl3.init();
    }

    /**
     * Update the imgui window state
     *
     * @param deltaTime the delta
     */
    public void render(double deltaTime, CoreEngine coreEngine) {
        glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], 0.0f);

        // Get window size properties and mouse position
        glfwGetWindowSize(display.getWindow(), winWidth, winHeight);
        glfwGetFramebufferSize(display.getWindow(), fbWidth, fbHeight);
        glfwGetCursorPos(display.getWindow(), mousePosX, mousePosY);

        // IMPORTANT!!
        // We SHOULD call those methods to update ImGui state for current frame
        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(winWidth[0], winHeight[0]);
        io.setDisplayFramebufferScale((float) fbWidth[0] / winWidth[0], (float) fbHeight[0] / winHeight[0]);
        io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
        io.setDeltaTime((float) deltaTime);


        coreEngine.render((float) deltaTime);

        ImGui.newFrame();
        coreEngine.renderUi((float) deltaTime);
        ImGui.render();

        imGuiGl3.render(ImGui.getDrawData());

    }
}
