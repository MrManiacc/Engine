package nexus.core.render;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import nexus.context.Context;
import nexus.core.defaults.IteratingInitSystem;
import nexus.core.math.Transform;
import nexus.core.player.LocalPlayer;
import nexus.core.registry.Registry;
import nexus.core.registry.assets.ShaderAsset;

/**
 * Renders a quad to screen
 */
@All({Transform.class, Model.class, Texture.class})
@Exclude(AnimationMap.class)
public class MeshRenderer extends IteratingInitSystem {
    private Context coreContext;
    private ShaderAsset meshShader;
    public ComponentMapper<Transform> mTransform;
    public ComponentMapper<Model> mModel;
    public ComponentMapper<Texture> mTexture;
    private LocalPlayer localPlayer;

    public MeshRenderer(Context coreContext) {
        this.coreContext = coreContext;
    }

    /**
     * Setup the renderer
     */
    public void postInitialization() {
        meshShader = coreContext.get(Registry.class).get("core:shaders:mesh", ShaderAsset.class);
        localPlayer = coreContext.get(LocalPlayer.class);
    }

    /**
     * This is temporary, soon we are going
     * to remove this and put it somewhere else
     */
    protected void begin() {
        meshShader.start();
        meshShader.loadMat4("projectionMatrix", localPlayer.getCamera().getProjectionMatrix());
        meshShader.loadMat4("viewMatrix", localPlayer.getCamera().getViewMatrix());
    }

    /**
     * Render the entities
     *
     * @param entityId the entity to render
     */
    protected void process(int entityId) {
        Transform transform = mTransform.get(entityId);
        Model model = mModel.get(entityId);
        Texture texture = mTexture.get(entityId);
        texture.bind();
        meshShader.loadMat4("modelMatrix", transform.getMatrix());
        model.draw();
        texture.unbind();
    }

    /**
     * Called at the end of rendering
     */
    protected void end() {
        meshShader.stop();
    }
}
