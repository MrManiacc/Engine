package nexus.core.render;

import com.artemis.Component;
import lombok.Setter;
import org.lwjgl.opengl.GL11;

/**
 * Represents a texture
 */
public class Texture extends Component {
    @Setter
    private int textureID;

    /**
     * Binds the current texture if it's loaded
     *
     * @param unit species which unit to bind to
     */
    public void bind(int unit) {
        GL11.glEnable(GL11.GL_TEXTURE_2D + unit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D + unit, textureID);
    }

    /**
     * Binds the current texture to unit 0
     */
    public void bind() {
        bind(0);
    }

    /**
     * Unbinds the current texture if it's loaded
     *
     * @param unit species which unit to bind to
     */
    public void unbind(int unit) {
        GL11.glDisable(GL11.GL_TEXTURE_2D + unit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D + unit, 0);
    }

    /**
     * Unbinds the current texture at unit 0
     */
    public void unbind() {
        unbind(0);
    }

}
