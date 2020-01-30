package nexus.core.registry;

import com.artemis.Component;
import com.google.common.collect.Maps;
import nexus.core.registry.assets.IAsset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a registry component
 */
public class Registry extends Component {
    private final HashMap<String, Pack> loadedPacks = Maps.newHashMap();

    /**
     * Gets a map of packs, with a list of nexus.core.assets by given type
     *
     * @param type the type to search for
     * @return returns map of nexus.core.assets
     */
    public Map<Pack, List<IAsset>> getAssetsByType(Class type) {
        Map<Pack, List<IAsset>> assets = new HashMap<>();
        for (Pack pack : loadedPacks.values()) {
            if (pack.hasType(type))
                assets.put(pack, pack.getAssets(type));
        }
        return assets;
    }

    /**
     * This will remove and unload everything
     *
     * @return returns the total number of unloaded nexus.core.assets
     */
    @SafeVarargs
    public final int unloadAll(Class<? extends IAsset>... types) {
        int count = 0;
        for (String packName : loadedPacks.keySet()) {
            Pack pack = loadedPacks.get(packName);
            count += pack.removeAll(types);
        }
        loadedPacks.clear();
        return count;
    }

    /**
     * Loads all nexus.core.assets with the given type
     *
     * @return returns the amount of nexus.core.assets loaded
     */
    @SafeVarargs
    public final int loadAll(Class<? extends IAsset>... types) {
        int count = 0;
        for (String packName : loadedPacks.keySet()) {
            Pack pack = loadedPacks.get(packName);
            count += pack.loadAll(types);
        }
        return count;
    }

    /**
     * Puts a domain with the given name if not already presents
     *
     * @return returns the domain if it is present
     */
    public <U extends IAsset> boolean put(String packName, U asset) {
        Pack pack = get(packName);
        asset.setPack(pack);
        return pack.put(asset.getClass(), asset);
    }


    /**
     * Gets an asset based upon the packName, and the asset name
     *
     * @param packName  the pack to search in for
     * @param assetName the asset to search for
     * @param type      the type of asset to search for
     * @param <T>       the type to return
     * @return returns the cast type
     */
    public <T extends IAsset> T get(String packName, String assetName, Class<? extends T> type) {
        Pack pack = get(packName);
        return pack.get(assetName, type);
    }

    /**
     * This will get an asset based upon the full name,
     * which includes the domain followed by the asset name
     *
     * @param fullName the full asset name including the pack
     * @param type     the type of asset to get
     * @param <T>      the type
     * @return returns the asset with the given registry
     * @implNote ImageAsset image = registry.get("core:missing", ImageAsset.class);
     */
    public <T extends IAsset> T get(String fullName, Class<? extends T> type) {
        String[] split = fullName.split(":");
        if (split.length < 2)
            return null;
        //This should get all of the pack qualifiers except the last, because the last will be the name of the asset
        StringBuilder packNameBuilder = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            packNameBuilder.append(split[i]);
            if (i != split.length - 2)
                packNameBuilder.append(":");
        }
        String packName = packNameBuilder.toString();
        String assetName = split[split.length - 1];

        return get(packName, assetName, type);
    }

    /**
     * Gets a domain by the given name if it exists
     *
     * @param packName the domain to grab
     * @return returns the given domain or null
     */
    public Pack get(String packName) {
        if (loadedPacks.containsKey(packName))
            return loadedPacks.get(packName);
        Pack pack = new Pack(packName);
        loadedPacks.put(packName, pack);
        return pack;
    }
}
