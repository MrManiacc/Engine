package nexus.core.animation;

import lombok.Getter;
import lombok.Setter;
import nexus.util.CommonUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.python.antlr.ast.Str;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Represents a list of animated frames
 */
public class AnimatedFrame {
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    public static final int MAX_JOINTS = 400;
    @Getter
    @Setter
    private Matrix4f[] localMatrices;
    @Getter
    @Setter
    private Matrix4f[] parentMatrices;
    @Getter
    @Setter
    private float timeStamp = 0;
    @Setter
    @Getter
    private Matrix4f rootTransformation;

    public AnimatedFrame() {
        localMatrices = new Matrix4f[MAX_JOINTS];
        parentMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(localMatrices, IDENTITY_MATRIX);
        Arrays.fill(parentMatrices, IDENTITY_MATRIX);
    }

    public void setMatrix(int pos, Matrix4f parentMatrix, Matrix4f boneMatrix, float time) {
        parentMatrices[pos] = parentMatrix;
        localMatrices[pos] = boneMatrix;
        timeStamp = time;
    }

    /**
     * This method will take the given frames and interpolate between them
     *
     * @param last     the last frame
     * @param next     the next frame
     * @param progress the progress between them
     * @return the bone matrices
     */
    public static Matrix4f[] interpolate(AnimatedFrame last, AnimatedFrame next, float progress) {
        Matrix4f[] mats = new Matrix4f[MAX_JOINTS];
        for (int i = 0; i < MAX_JOINTS; i++) {
            Matrix4f lastLocal = new Matrix4f(last.localMatrices[i]);
            Matrix4f lastParent = new Matrix4f(last.parentMatrices[i]);
            Matrix4f lastRoot = new Matrix4f(last.rootTransformation);
            Matrix4f nextLocal = new Matrix4f(next.localMatrices[i]);
            Matrix4f nextParent = new Matrix4f(next.parentMatrices[i]);
            Matrix4f finalMatrix = lastParent.mul(lastLocal);
            Matrix4f nextFinalMatrix = nextParent.mul(nextLocal);
            Vector3f lastPos = finalMatrix.getTranslation(new Vector3f());
            Quaternionf lastRot = finalMatrix.getNormalizedRotation(new Quaternionf());
            Vector3f nextPos = nextFinalMatrix.getTranslation(new Vector3f());
            Quaternionf nextRot = nextFinalMatrix.getNormalizedRotation(new Quaternionf());
            Vector3f nextScale = nextFinalMatrix.getScale(new Vector3f());
            Vector3f newPos = new Vector3f(lastPos).lerp(nextPos, progress);
            Quaternionf newRot = CommonUtils.interpolate(lastRot, nextRot, progress);
            finalMatrix.identity().translationRotateScale(newPos, newRot, nextScale);
            finalMatrix = new Matrix4f(lastRoot).mul(finalMatrix);
            mats[i] = finalMatrix;
        }
        return mats;
    }

    public String toString() {
        StringBuilder localMats = new StringBuilder();
        for (Matrix4f matrix4f : localMatrices)
            localMats.append(matrix4f.toString(DecimalFormat.getInstance())).append("\n");
        StringBuilder parentMats = new StringBuilder();
        for (Matrix4f matrix4f : parentMatrices)
            parentMats.append(matrix4f.toString(DecimalFormat.getInstance())).append("\n");
        return "Timestamp: " + timeStamp +
                "\nRoot Transform: \n" + rootTransformation.toString(DecimalFormat.getInstance()) +
                "\nLocal Matricies: \n" + localMats.toString()
                + "\nParent Matricies: \n" + parentMats.toString();
    }
}
