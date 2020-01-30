package nexus.core.animation;

import lombok.Getter;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node for animation
 */
public class Node {
    @Getter
    private final List<Node> children;
    @Getter
    private final List<Matrix4f> transformations;
    @Getter
    private final List<Float> times;
    @Getter
    private final String name;
    @Getter
    private final Node parent;

    public Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
        this.transformations = new ArrayList<>();
        this.times = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    /**
     * Gets the transforms for the parent at the given frame
     *
     * @param node     the parent
     * @param framePos the current from
     * @return the transformed matrix
     */
    public static Matrix4f getParentTransforms(Node node, int framePos) {
        if (node == null)
            return new Matrix4f();
        else {
            Matrix4f parentTransform = new Matrix4f(getParentTransforms(node.getParent(), framePos));
            List<Matrix4f> transformations = node.getTransformations();
            Matrix4f nodeTransform;
            int transfSize = transformations.size();
            if (framePos < transfSize)
                nodeTransform = transformations.get(framePos);
            else if (transfSize > 0)
                nodeTransform = transformations.get(transfSize - 1);
            else
                nodeTransform = new Matrix4f();
            return parentTransform.mul(nodeTransform);
        }
    }

    /**
     * Adds a child node to this node
     *
     * @param node child node
     */
    public void addChild(Node node) {
        this.children.add(node);
    }

    /**
     * Adds a transform matrix to the current transforms
     *
     * @param transformation transform matrix
     */
    public void addTransformation(Matrix4f transformation, float time) {
        transformations.add(transformation);
        times.add(time);
    }

    /**
     * Gets a node by the node's name
     *
     * @param targetName the targeted node to find
     * @return the node searched for
     */
    public Node findByName(String targetName) {
        Node result = null;
        if (this.name.equals(targetName)) {
            result = this;
        } else {
            for (Node child : children) {
                result = child.findByName(targetName);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Gets the total animation frames for the given node
     *
     * @return total frames
     */
    public int getAnimationFrames() {
        int numFrames = this.transformations.size();
        for (Node child : children) {
            int childFrame = child.getAnimationFrames();
            numFrames = Math.max(numFrames, childFrame);
        }
        return numFrames;
    }

}
