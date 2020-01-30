package nexus.core.terrain;

import lombok.Getter;
import lombok.Setter;

public class TerrainConfig {
    @Getter
    @Setter
    private float scaleY;
    @Getter
    @Setter
    private float scaleXZ;
    @Getter
    @Setter
    private int[] lodRange = new int[8];
    @Getter
    @Setter
    private int[] lodMorphArea = new int[8];
}
