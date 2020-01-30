package nexus.core.render;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import nexus.context.Context;
import nexus.core.defaults.IteratingInitSystem;
import nexus.core.math.Transform;
import nexus.core.player.LocalPlayer;
import nexus.core.registry.Registry;
import nexus.core.registry.assets.AnimationAsset;
import nexus.core.registry.assets.ShaderAsset;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.*;

/**
 * Renders a quad to screen
 */
@All({Transform.class, Model.class, Texture.class, AnimationMap.class, Bones.class})
public class AnimatedMeshRenderer extends IteratingInitSystem {
    private Context coreContext;
    private ShaderAsset meshShader;
    public ComponentMapper<Transform> mTransform;
    public ComponentMapper<Model> mModel;
    public ComponentMapper<Texture> mTexture;
    public ComponentMapper<AnimationMap> mAnimation;
    public ComponentMapper<Bones> mBones;
    private LocalPlayer localPlayer;

    public AnimatedMeshRenderer(Context coreContext) {
        this.coreContext = coreContext;
    }

    /**
     * Setup the renderer
     */
    public void postInitialization() {
        meshShader = coreContext.get(Registry.class).get("core:shaders:animated", ShaderAsset.class);
        localPlayer = coreContext.get(LocalPlayer.class);

//        System.out.println(coreContext.get(Registry.class).get("core:animations:Praying", AnimationAsset.class).toComponent());
    }

    /**
     * This is temporary, soon we are going
     * to remove this and put it somewhere else
     */
    protected void begin() {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glClearColor(0.01f, 0.2f, 0.34f, 1);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_CULL_FACE);
        GL11.glCullFace(GL_BACK);
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
        AnimationMap animation = mAnimation.get(entityId);
        Bones bones = mBones.get(entityId);
        texture.bind();
        meshShader.loadMat4("modelMatrix", transform.getMatrix());
        Matrix4f[] frame = animation.update(world.delta);
        meshShader.loadMat4Array("jointsMatrix", frame);
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
