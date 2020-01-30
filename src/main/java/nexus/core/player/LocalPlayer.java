package nexus.core.player;

import com.artemis.World;
import lombok.Getter;
import nexus.context.Context;
import nexus.core.math.Transform;

public class LocalPlayer {
    @Getter
    private int entityID;
    @Getter
    private Camera camera;
    @Getter
    private Transform transform;
    @Getter
    private CameraSettings cameraSettings;
    /**
     * Creates the local player instance
     *
     * @param context
     */
    public LocalPlayer(Context context) {
        this.camera = Camera.newCamera(context, 90, 0.01f, 1000f);
        this.transform = new Transform();
        this.transform.setPosition(0, 0, 0);
        this.cameraSettings = new CameraSettings();
        World world = context.get(World.class);
        entityID = world.create();
        world.edit(entityID).add(camera);
        world.edit(entityID).add(transform);
        world.edit(entityID).add(cameraSettings);
    }
}
