package nexus.engine;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.utils.ImmutableBag;
import lombok.Getter;
import nexus.context.Context;
import nexus.core.defaults.InitSystem;
import nexus.core.input.InputSystem;
import nexus.core.player.CameraSystem;
import nexus.core.registry.Registry;
import nexus.core.registry.RegistrySystem;
import nexus.core.render.AnimatedMeshRenderer;
import nexus.core.render.BillboardRenderer;
import nexus.core.render.MeshRenderer;
import nexus.gui.Menu;
import nexus.window.Display;

import java.io.File;
import java.io.IOException;

/**
 * Represents the core engine of the game
 */
public class CoreEngine {
    @Getter
    private final Context coreContext = new Context();
    private final File resourceFolder;
    private Display display;
    private World world;
    private Menu menu;

    public CoreEngine(String resources, String title, int width, int height, boolean resizable, boolean vSync, int startMonitor) throws IOException {
        resourceFolder = new File(resources);
        if (!resourceFolder.exists())
            throw new IOException("Resource folder not found!");
        display = coreContext.put(Display.class, new Display(title, width, height, resizable, vSync, startMonitor, true));
        menu = new Menu(coreContext);
        preInitialization();
    }

    /**
     * Called to register anything before the window has been created
     */
    public void preInitialization() {
        registerWorld();
        preInitializeSystems();
        display.preInitialization(this);
    }

    /**
     * Register the world and core systems
     */
    private void registerWorld() {
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new RegistrySystem(resourceFolder, coreContext))
                .with(new InputSystem(coreContext))
                .with(new CameraSystem(coreContext))
                //.with(new QuadRenderer(coreContext))
                .with(new AnimatedMeshRenderer(coreContext))
                .with(new BillboardRenderer(coreContext))
                .with(new MeshRenderer(coreContext))
                .build();
        this.world = new World(config);
        coreContext.put(World.class, world);
    }

    /**
     * Call the pre initialization method on init system
     */
    private void preInitializeSystems() {
        ImmutableBag<BaseSystem> systems = world.getSystems();
        for (BaseSystem system : systems) {
            if (system instanceof InitSystem) {
                InitSystem initSys = (InitSystem) system;
                initSys.preInitialization();
            }
        }
    }


    /**
     * Called to register anything after the window has been created
     */
    public void postInitialization() {
        ImmutableBag<BaseSystem> systems = world.getSystems();
        for (BaseSystem system : systems) {
            if (system instanceof InitSystem) {
                InitSystem initSys = (InitSystem) system;
                initSys.postInitialization();
            }
        }
        menu.initialize();
    }


    /**
     * All main rendering goes on here
     *
     * @param deltaTime
     */
    public void render(float deltaTime) {
        world.setDelta(deltaTime);
        world.process();
    }

    /**
     * All gui rendering goes on ehre
     *
     * @param deltaTime
     */
    public void renderUi(float deltaTime) {
        menu.render(deltaTime);
    }

    /**
     * Request the engine to stop running
     */
    public void shutdown() {
        coreContext.get(Registry.class).unloadAll();
        world.dispose();
        display.shutdown();
    }

}
