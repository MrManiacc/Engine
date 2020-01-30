package nexus.core.registry.assets;

import lombok.Getter;
import lombok.Setter;
import nexus.core.registry.Pack;
import nexus.util.CommonUtils;

import java.io.File;
import java.io.IOException;

/**
 * Represents a material
 */
public class MaterialAsset implements IAsset{
    @Getter
    private String name;
    @Getter
    @Setter
    private Pack pack;
    @Getter
    private File file;
    @Getter
    private boolean loaded = false;

    /**
     * Creates an image and gets the file name
     *
     * @param file
     */
    public MaterialAsset(File file) {
        this.file = file;
        try {
            this.name = CommonUtils.removeExtension(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Object toComponent() {
        return null;
    }

    public void load() {

    }

    public void unload() {

    }

}
