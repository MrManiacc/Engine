package nexus.core.animation;

import lombok.Getter;
import org.joml.Matrix4f;

/**
 * Represents a bone for a given model
 */
public class Bone {
    @Getter
    private final int boneId;
    @Getter
    private final String boneName;
    @Getter
    private Matrix4f offsetMatrix;

    public Bone(int boneId, String boneName, Matrix4f offsetMatrix) {
        this.boneId = boneId;
        this.boneName = boneName;
        this.offsetMatrix = offsetMatrix;
    }

}
