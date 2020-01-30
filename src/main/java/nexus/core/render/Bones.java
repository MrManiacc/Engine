package nexus.core.render;

import com.artemis.Component;
import lombok.Getter;
import org.joml.Vector3f;

public class Bones extends Component {
    @Getter
    private Vector3f[] bones = new Vector3f[150];

    /**
     * Gets a bone, or null if the bone is empty
     *
     * @param index bone index
     * @return bone at index
     */
    public Vector3f getBone(int index) {
        if (bones[index].x == 0 && bones[index].y == 0 && bones[index].z == 0)
            return null;
        return bones[index];
    }
}
