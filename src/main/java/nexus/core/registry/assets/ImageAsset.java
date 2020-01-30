package nexus.core.registry.assets;

import lombok.Getter;
import lombok.Setter;
import nexus.core.registry.Pack;
import nexus.core.render.Texture;
import nexus.util.CommonUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;

/**
 * Represents an image in opengl
 */
public class ImageAsset implements IAsset {
    @Getter
    private String name;
    @Getter
    @Setter
    private Pack pack;
    @Getter
    private File file;
    @Getter
    private boolean loaded = false;
    @Getter
    private int id; //The id of the texture
    @Getter
    private int width, height;

    /**
     * Creates an image and gets the file name
     *
     * @param file
     */
    public ImageAsset(File file) {
        this.file = file;
        try {
            this.name = CommonUtils.removeExtension(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the component only if loaded
     *
     * @return returns the component
     */
    public Texture toComponent() {
        Texture texture = new Texture();
        texture.setTextureID(this.id);
        return texture;
    }

    /**
     * Loads an image to opengl
     */
    public void load() {
        if (!loaded) {
            try {
                IntBuffer width = BufferUtils.createIntBuffer(1);
                IntBuffer height = BufferUtils.createIntBuffer(1);
                IntBuffer components = BufferUtils.createIntBuffer(1);
                //The image buffer data
                ByteBuffer bufferData = STBImage.stbi_load_from_memory(CommonUtils.loadImageToByteBuffer(file, 1024), width, height, components, 4);
                id = glGenTextures();

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);

                this.width = width.get(0);
                this.height = height.get(0);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bufferData);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
                STBImage.stbi_image_free(bufferData);
                loaded = true;
            } catch (IOException e) {
                loaded = false;
                System.err.println("Failed to load - " + getQualifier());
            }
        }
    }


    /**
     * Unloads an image from opengl
     */
    public void unload() {
        if (loaded) {
            //TODO unload image
            this.width = 0;
            this.height = 0;
            glDeleteTextures(id);
            loaded = false;
        }
    }
}
