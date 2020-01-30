package nexus.core.render;

import com.artemis.Component;
import lombok.Getter;
import lombok.Setter;
import nexus.core.animation.AnimatedFrame;
import org.joml.Matrix4f;
import org.python.antlr.ast.Str;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an animation
 */
public class Animation extends Component {
    @Getter
    @Setter
    private List<AnimatedFrame> frames = new ArrayList<>();
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private double duration;
    private float animationTime = 0;
    @Setter
    @Getter
    private boolean reset = false;
    @Getter
    private AnimatedFrame currentFrame;

    /**
     * Increases the current animation time which allows the animation to
     * progress. If the current animation has reached the end then the timer is
     * reset, causing the animation to loop.
     */
    private void increaseAnimationTime(float deltaTime) {
        animationTime += deltaTime;
        if (animationTime > duration) {
            this.animationTime %= duration;
        }
    }

    /**
     * Steps the frame and returns the next in queue
     *
     * @return next frame
     */
    public Matrix4f[] getNextFrame(float deltaTime) {
        increaseAnimationTime(deltaTime);
        AnimatedFrame[] frames = getNextAndPrevious();
        currentFrame = frames[1];
        float progression = calculateProgression(frames[0], frames[1]);
        return AnimatedFrame.interpolate(frames[0], frames[1], progression);
    }

    /**
     * Calculate the progression between two animations
     *
     * @param previousFrame the previous frame
     * @param nextFrame     the next frame
     * @return time between
     */
    private float calculateProgression(AnimatedFrame previousFrame, AnimatedFrame nextFrame) {
        float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
        float currentTime = animationTime - previousFrame.getTimeStamp();
        return currentTime / totalTime;
    }

    /**
     * computes the next and previous frames of the animation
     *
     * @return the next & previous frames
     */
    public AnimatedFrame[] getNextAndPrevious() {
        AnimatedFrame lastFrame = frames.get(0);
        AnimatedFrame nextFrame = frames.get(0);
        for (int i = 1; i < frames.size(); i++) {
            nextFrame = frames.get(i);
            if (nextFrame.getTimeStamp() > animationTime) {
                break;
            }
            lastFrame = frames.get(i);
        }
        return new AnimatedFrame[]{lastFrame, nextFrame};
    }

    /**
     * Adds an animated frame
     *
     * @param animatedFrame the frame to add
     */
    public void addAnimatedFrame(AnimatedFrame animatedFrame) {
        frames.add(animatedFrame);
    }

    public String toString() {
        return Arrays.toString(frames.toArray());
    }
}
