package nexus.core.registry;

import com.artemis.annotations.All;
import nexus.context.Context;
import nexus.core.defaults.InitSystem;
import nexus.core.math.Transform;
import nexus.core.registry.assets.*;
import nexus.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This system is responsible for loading all of the resources related
 * to files, for example it will load
 */
@All(Transform.class)
public class RegistrySystem extends InitSystem {
    private File resourceFolder;
    private Context coreContext;
    private Registry coreRegistry;

    public RegistrySystem(File resourceFolder, Context coreContext) {
        this.resourceFolder = resourceFolder;
        this.coreContext = coreContext;
        this.coreRegistry = new Registry();
        coreContext.put(Registry.class, coreRegistry);
    }

    /**
     * Called before the  window is created.
     * This should be used for file discovery.
     */
    public void preInitialization() {
        initializeRegistry();
        populateRegistry();
        int meshesLoaded = coreRegistry.loadAll(MeshAsset.class);
        System.out.println("[" + meshesLoaded + "] total meshes loaded");

        int animationsLoaded = coreRegistry.loadAll(AnimationAsset.class);
        System.out.println("[" + animationsLoaded + "] total animations loaded");
    }

    /**
     * Create the registry component
     */
    private void initializeRegistry() {
        int registryEntity = world.create();
        world.edit(registryEntity).add(coreRegistry);
    }

    /**
     * This method will populate the domains
     */
    private void populateRegistry() {
        try {
            Files.walk(Paths.get(resourceFolder.getAbsolutePath()))
                    .filter(CommonUtils::isValidDirectory)
                    .forEach(p -> Arrays.asList(Objects.requireNonNull(new File(p.toUri()).listFiles())).forEach(handleResources));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for parsing all the json files,
     * and then correctly mapping them to the corresponding domain
     */
    private Consumer<File> handleResources = file -> {
        if (!file.isFile()) return;
        try {
            String extension = CommonUtils.getExtension(file);
            String pack = CommonUtils.resolveDomain(file);
            switch (extension.toLowerCase()) {
                case "png":
                case "jpeg":
                case "jpg":
                    coreRegistry.put(pack, new ImageAsset(file));
                    break;
                case "fbx":
                case "obj":
                case "dae":
                case "max":
                case "mb":
                    coreRegistry.put(pack, new MeshAsset(file, false));
                    coreRegistry.put(pack, new AnimationAsset(file, false));
                    break;
                case "bin":
                    coreRegistry.put(pack, new MeshAsset(file, true));
                    break;
                case "animation":
                    coreRegistry.put(pack, new AnimationAsset(file, true));
                    break;
                case "glsl":
                    coreRegistry.put(pack, new ShaderAsset(file));
                    break;
                case "py":
                    coreRegistry.put(pack, new ScriptAsset(file, coreContext));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };


    /**
     * Called after the opengl context is made,
     * this is used to load nexus.core.assets like textures,
     * animations, models etc
     */
    public void postInitialization() {
        int imagesLoaded = coreRegistry.loadAll(ImageAsset.class);
        System.out.println("[" + imagesLoaded + "] total images loaded");

        int shadersLoaded = coreRegistry.loadAll(ShaderAsset.class);
        System.out.println("[" + shadersLoaded + "] total shaders loaded");

        int scriptsLoaded = coreRegistry.loadAll(ScriptAsset.class);
        System.out.println("[" + scriptsLoaded + "] total scripts loaded");

    }
}
