package nexus.core.render;

import com.artemis.Component;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows for a player to have multiple animations
 */
public class AnimationMap extends Component {
    @Getter
    private Map<String, Animation> animations = new HashMap<>();
    @Setter
    private Animation currentAnimation;
    @Setter
    @Getter
    private Animation nextAnimation;
    private float increment = 0;
    private float speed = 1.0f;
    private Matrix4f[] empty = new Matrix4f[150];

    public AnimationMap() {
        Arrays.fill(empty, new Matrix4f().identity());
    }

    /**
     * Plays a new animation
     *
     * @param animation
     */
    public void play(String animation, float speed) {
        if (currentAnimation == null)
            currentAnimation = animations.get(animation);
        else
            nextAnimation = animations.get(animation);
        this.speed = speed;
    }

    /**
     * Gets the current animations matrices
     */
    public Matrix4f[] update(float deltaTime) {
        if (currentAnimation == null)
            return empty;
        Matrix4f[] matrix = currentAnimation.getNextFrame(deltaTime);
        if (nextAnimation != null) {
            float blend = incrementAnimations(deltaTime);
            if (blend >= 1) {
                increment = 0;
                currentAnimation = nextAnimation;
                nextAnimation = null;
            } else {
                Matrix4f[] nextMatrix = nextAnimation.getNextFrame(deltaTime);
                Matrix4f[] lerped = new Matrix4f[nextMatrix.length];
                for (int i = 0; i < nextMatrix.length; i++)
                    lerped[i] = new Matrix4f(matrix[i]).lerp(nextMatrix[i], blend);
                return lerped;
            }
        }

        return matrix;
    }

    /**
     * Increments the animation time, used for blending between two animations
     *
     * @param deltaTime the deltaTime
     * @return the incremented time
     */
    private float incrementAnimations(float deltaTime) {
        increment += deltaTime * speed;
        return increment;
    }


    /**
     * Adds an animation to the map
     *
     * @param animation
     */
    public void addAnimation(Animation animation) {
        this.animations.put(animation.getName(), animation);
    }

    @Override
    public String toString() {
        return "AnimationMap{" + Arrays.toString(animations.values().toArray()) + "}";
    }
}
