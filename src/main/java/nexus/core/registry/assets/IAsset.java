package nexus.core.registry.assets;

import nexus.core.registry.Pack;

import java.io.File;

/**
 * Represents a generic asset
 */
public interface IAsset {
    /**
     * The name of the asset
     *
     * @return the asset name
     */
    String getName();


    /**
     * The domain of the asset
     *
     * @return the domain asset
     */
    Pack getPack();

    /**
     * Sets the given pack
     *
     * @param pack the pack container
     */
    void setPack(Pack pack);

    /**
     * Gets the full qualifier of the asset
     *
     * @return asset qualifier
     */
    default String getQualifier() {
        return "[" + getPack().getName() + ":" + getName() + "]: " + getFile().getAbsolutePath();
    }

    /**
     * Gets an nexus.core.assets name and pack
     *
     * @return asset name
     */
    default String getRegistryName() {
        return "[" + getPack().getName() + "]: " + getName();
    }


    /**
     * If the asset is loaded or not
     *
     * @return return the loaded status
     */
    boolean isLoaded();

    /**
     * Loads a given asset
     */
    void load();

    /**
     * Unloads a given asset
     */
    void unload();

    /**
     * Gets the given file
     *
     * @return the file
     */
    File getFile();

}
