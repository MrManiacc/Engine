package nexus.core.render;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import nexus.context.Context;
import nexus.core.defaults.IteratingInitSystem;
import nexus.core.math.Transform;
import nexus.core.registry.Registry;
import nexus.core.registry.assets.ImageAsset;
import nexus.core.registry.assets.ShaderAsset;
import nexus.util.opengl.Vao;

import static org.lwjgl.opengl.GL11.*;

/**
 * Renders a quad to screen
 */
@All({Transform.class, Model.class, Texture.class})
public class QuadRenderer extends IteratingInitSystem {
    private Context coreContext;
    private ShaderAsset quadShader;
    public ComponentMapper<Transform> mTransform;
    public ComponentMapper<Model> mModel;
    public ComponentMapper<Texture> mTexture;

    public QuadRenderer(Context coreContext) {
        this.coreContext = coreContext;
    }

    /**
     * Setup the renderer
     */
    public void postInitialization() {
        quadShader = coreContext.get(Registry.class).get("core:shaders:quad", ShaderAsset.class);
        setupTest();
    }

    //TODO: remove this
    private void setupTest() {
        int entity = world.create();
        Transform transform = new Transform();
        transform.setScale(0.2f, 0.2f, 0.2f);
        Texture texture = coreContext.get(Registry.class).get("core:images:box", ImageAsset.class).toComponent();
        Model quadModel = new Model();
        Vao vao = Vao.create(2);
        vao.bind();
        vao.createAttribute(new float[]{
                //left bottom triangle
                -0.5f, +0.5f, 0.0f, // v0 - x1, y1, z1
                -0.5f, -0.5f, 0.0f, // v1 - x2, y2, z2
                +0.5f, -0.5f, 0.0f, // v2 - x3, y3, z3
                +0.5f, +0.5f, 0.0f // v3 - x4, y4, z4
        }, 3);
        vao.createAttribute(new float[]{
                0, 0,
                1, 0,
                0, 1,
                1, 1
        }, 2);
        vao.createIndexBuffer(new int[]{
                0, 1, 3, // top left triangle (v0, v1, v2)
                3, 1, 2 // bottom right triangle (v3 v1 v2)
        });
        vao.unbind();
        quadModel.addAllVaos(vao);
        world.edit(entity).add(transform);
        world.edit(entity).add(quadModel);
        world.edit(entity).add(texture);
    }

    /**
     * This is temporary, soon we are going
     * to remove this and put it somewhere else
     */
    protected void begin() {
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glClearColor(0, 0, 0, 1);
        quadShader.start();
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
        quadShader.loadMat4("modelMatrix", transform.getMatrix());
        model.bindFirst();
        glDrawElements(GL_TRIANGLES, model.getFirstIndexCount(), GL_UNSIGNED_INT, 0);
        model.unbindFirst();
        texture.unbind();
    }

    /**
     * Called at the end of rendering
     */
    protected void end() {
        quadShader.stop();
    }
}
