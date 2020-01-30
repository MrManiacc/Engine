package nexus.core.render;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import nexus.context.Context;
import nexus.core.defaults.IteratingInitSystem;
import nexus.core.math.Transform;
import nexus.core.player.LocalPlayer;
import nexus.core.registry.Registry;
import nexus.core.registry.assets.ShaderAsset;
import nexus.util.opengl.Vao;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * Renders billboards in 3d space
 */
@All({Billboard.class, Transform.class, Bones.class})
public class BillboardRenderer extends IteratingInitSystem {
    private Context context;
    private LocalPlayer localPlayer;
    private ShaderAsset shader;
    private ComponentMapper<Billboard> mBillboard;
    private ComponentMapper<Transform> mTransform;
    private ComponentMapper<Bones> mBones;
    private Vao billboardModel;

    public BillboardRenderer(Context context) {
        this.context = context;
    }

    /**
     * Initalize parametersl
     */
    public void postInitialization() {
        this.localPlayer = context.get(LocalPlayer.class);
        this.shader = context.get(Registry.class).get("core:shaders:billboard", ShaderAsset.class);
        billboardModel = Vao.create(2);
        billboardModel.bind();
        billboardModel.createAttribute(new float[]{
                //left bottom triangle
                -0.5f, +0.5f, 0.0f, // v0 - x1, y1, z1
                -0.5f, -0.5f, 0.0f, // v1 - x2, y2, z2
                +0.5f, -0.5f, 0.0f, // v2 - x3, y3, z3
                +0.5f, +0.5f, 0.0f // v3 - x4, y4, z4
        }, 3);
        billboardModel.createAttribute(new float[]{
                0, 0,
                1, 0,
                0, 1,
                1, 1
        }, 2);

        billboardModel.createIndexBuffer(new int[]{
                0, 1, 3, // top left triangle (v0, v1, v2)
                3, 1, 2 // bottom right triangle (v3 v1 v2)
        });
        billboardModel.unbind();
    }

    /**
     * Begin shader code
     */
    protected void begin() {
        shader.start();
        shader.loadVec3("cameraRight", localPlayer.getCamera().getRight());
        shader.loadVec3("cameraUp", localPlayer.getCamera().getUp());
        shader.loadMat4("projectionMatrix", localPlayer.getCamera().getProjectionMatrix());
        shader.loadMat4("viewMatrix", localPlayer.getCamera().getViewMatrix());
    }

    /**
     * Render the billboard to the entity
     *
     * @param entityId entity to render to
     */
    protected void process(int entityId) {
        Transform transform = mTransform.get(entityId);
        Billboard billboard = mBillboard.get(entityId);
        Vector3f forward = new Vector3f();
        transform.getMatrix().positiveZ(forward);
        forward = forward.mul(0.2f);
        Vector3f center = new Vector3f(transform.getPosition()).add(forward).add(0, transform.getScale().y, 0).add(new Vector3f(0, billboard.getSize().y * 1.5f, 0));
        shader.loadVec3("center", center);
        shader.loadVec3("size", new Vector3f(billboard.getSize(), billboard.isFixed() ? 1 : 0));
        billboardModel.bind();
        glDrawElements(GL_TRIANGLES, billboardModel.getIndexCount(), GL_UNSIGNED_INT, 0);
        billboardModel.unbind();
    }

    /**
     * End shader code
     */
    protected void end() {
        shader.stop();
    }
}
