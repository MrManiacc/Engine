package nexus.core.animation;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a weight for a given vertex
 */
public class VertexWeight {
    @Getter
    private int boneId;
    @Getter
    @Setter
    private int vertexId;
    @Getter
    @Setter
    private float weight;

    public VertexWeight(int boneId, int vertexId, float weight) {
        this.boneId = boneId;
        this.vertexId = vertexId;
        this.weight = weight;
    }
}
