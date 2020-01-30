package nexus.core.render;

import com.artemis.Component;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Contains data for 3d billboards
 */
public class Billboard extends Component {
    @Getter
    @Setter
    private String text;
    @Setter
    @Getter
    private Vector3f color;
    @Getter
    @Setter
    private Vector2f size = new Vector2f(1, 1);
    @Getter
    @Setter
    private Vector3f offset = new Vector3f(0, 0, 0);
    @Getter
    @Setter
    private boolean fixed = false;
}
