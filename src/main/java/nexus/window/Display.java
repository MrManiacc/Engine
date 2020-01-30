package nexus.window;

import lombok.Getter;
import nexus.context.Context;
import nexus.engine.CoreEngine;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Represents a display (a.k.a a window)
 */
public class Display {
    @Getter
    private long window;
    @Getter
    private int width, height;
    @Getter
    private int frameWidth, frameHeight;
    @Getter
    private String title;
    @Getter
    private boolean resizable, vsync;
    @Getter
    private int monitor;
    @Getter
    private boolean dev;
    @Getter
    private GuiState guiState;

    public Display(String title, int width, int height, boolean resizable, boolean vsync, int monitor, boolean dev) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.frameWidth = width;
        this.frameHeight = height;
        this.resizable = resizable;
        this.vsync = vsync;
        this.monitor = monitor;
        this.dev = dev;
        this.guiState = new GuiState(this);
    }

    /**
     * Called to create the window context
     */
    public void preInitialization(CoreEngine coreEngine) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);

        window = glfwCreateWindow(width, height, "DEV ENGINE", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
        if (resizable) {
            glfwSetFramebufferSizeCallback(window, (handle, frameWidth, frameHeight) -> {
                this.frameWidth = frameWidth;
                this.frameHeight = frameHeight;
                glViewport(0, 0, frameWidth, frameHeight);
            });
        }

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode;
            int[] startX = new int[]{0}, startY = new int[]{0};
            if (monitor == -1)
                vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            else {
                PointerBuffer monitorBuf = glfwGetMonitors();
                vidmode = glfwGetVideoMode(monitorBuf.get(monitor));
                glfwGetMonitorPos(monitorBuf.get(monitor), startX, startY);
            }
            // Center the window
            glfwSetWindowPos(
                    window,
                    startX[0] + (vidmode.width() - pWidth.get(0)) / 2,
                    startY[0] + (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically
        postInitialization(coreEngine);
    }


    /**
     * Called after the window has been created
     */
    public void postInitialization(CoreEngine coreEngine) {
        glfwMakeContextCurrent(window);
        glfwSwapInterval(vsync ? GLFW_TRUE : GLFW_FALSE);
        glfwShowWindow(window);
        GL.createCapabilities();
        guiState.initImGui(coreEngine.getCoreContext());
        coreEngine.postInitialization();
        mainLoop(coreEngine);
        coreEngine.shutdown();
    }

    /**
     * The main game loop
     */
    private void mainLoop(CoreEngine coreEngine) {
        double time = 0;
        while (!glfwWindowShouldClose(window)) {
            // Count frame delta value
            final double currentTime = glfwGetTime();
            final double deltaTime = (time > 0) ? (currentTime - time) : 1f / 60f;
            time = currentTime;

            guiState.render(deltaTime, coreEngine);

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();
        }
    }

    /**
     * Called to cleanup all of the initialized values from the context
     */
    public void shutdown() {
        if (!dev) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }
}
