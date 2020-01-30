package nexus.core.registry;

import com.google.common.collect.Maps;
import lombok.Getter;
import nexus.core.registry.assets.IAsset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a core pack,
 * which is like a package for a group
 * of nexus.core.assets.
 */
public class Pack {
    private final Map<Class<? extends IAsset>, Map<String, Object>> map = Maps.newConcurrentMap();
    @Getter
    private final String name;
    @Getter
    private int count = 0;

    public Pack(String name) {
        this.name = name;
    }

    /**
     * Gets the given sub map using the passed type, will
     * create a new map and insert it if it's not found
     *
     * @param type the type of map
     * @return returns the new map
     */
    private Map<String, Object> get(Class<? extends IAsset> type) {
        if (map.containsKey(type))
            return map.get(type);
        Map<String, Object> subMap = new HashMap<>();
        map.put(type, subMap);
        return subMap;
    }

    /**
     * Checks to see if this pack has a certain type of ass
     *
     * @param type the type of asst o search for
     * @return
     */
    public boolean hasType(Class<? extends IAsset> type) {
        for (Map<String, Object> values : map.values()) {
            for (Object obj : values.values()) {
                if (type.isInstance(obj))
                    return true;
            }
        }
        return false;
    }

    /**
     * Gets all nexus.core.assets with the given type
     *
     * @param type the type of asset to get
     * @return list of nexus.core.assets with given type
     */
    public List<IAsset> getAssets(Class type) {
        List<IAsset> assets = new ArrayList<>();
        Map<String, Object> assetMap = get(type);
        for (Object obj : assetMap.values())
            assets.add((IAsset) obj);
        return assets;
    }

    /**
     * Loads all of the nexus.core.assets from this pack
     * will only load the specified types,
     * or everything is no type is specified
     *
     * @param types the type of data to load
     * @return return the total number of nexus.core.assets loaded
     */
    @SafeVarargs
    public final int loadAll(Class<? extends IAsset>... types) {
        int loaded = 0;
        if (types.length == 0) {
            for (Class<? extends IAsset> type : map.keySet())
                loaded = getLoaded(loaded, type);
        } else
            for (Class<? extends IAsset> type : types)
                loaded = getLoaded(loaded, type);
        return loaded;
    }

    /**
     * Helper method to load the given type
     *
     * @param loaded the additive number of removed nexus.core.assets
     * @param type
     * @return
     */
    private int getLoaded(int loaded, Class<? extends IAsset> type) {
        Map<String, Object> subMap = get(type);
        for (String assetName : subMap.keySet()) {
            IAsset asset = type.cast(subMap.get(assetName));
            if (!asset.isLoaded()) {
                asset.load();
                if (asset.isLoaded())
                    loaded++;
            }
        }
        return loaded;
    }

    /**
     * Puts a given element into the pack
     *
     * @return returns true if the asset with name was
     * added successfully, false if asset with type and name
     * already exists in pack
     */
    public <U extends IAsset> boolean put(Class<? extends IAsset> type, U object) {
        Map<String, Object> subMap = get(type);
        if (subMap.containsKey(object.getName()))
            return false;
        subMap.put(object.getName(), object);
        count++;
        return true;
    }

    /**
     * Removes an asset and returns it by using the typel
     *
     * @param assetName the asset name to remove
     * @param type      the type of asset
     * @param <T>       the type
     * @return returns the removed asset or null
     */
    public <T extends IAsset> T remove(String assetName, Class<T> type) {
        Map<String, Object> subMap = get(type);
        if (subMap.containsKey(assetName)) {
            T asset = type.cast(subMap.remove(assetName));
            asset.unload();
            count--;
            return asset;
        }
        return null;
    }

    /**
     * Removes all of the nexus.core.assets from this pack,
     * making sure to unload them first
     *
     * @param types the type of data to remove
     * @return return the total number of nexus.core.assets before purging
     */
    @SafeVarargs
    public final int removeAll(Class<? extends IAsset>... types) {
        int removed = 0;
        if (types.length == 0)
            for (Class<? extends IAsset> subMapKey : map.keySet()) removed = getRemoved(removed, subMapKey);
        else
            for (Class<? extends IAsset> type : types) removed = getRemoved(removed, type);
        map.clear();
        return removed;
    }

    /**
     * Helper method to remove a type
     *
     * @param removed the additive number of removed nexus.core.assets
     * @param type
     * @return
     */
    private int getRemoved(int removed, Class<? extends IAsset> type) {
        Map<String, Object> subMap = map.get(type);
        for (String assetName : subMap.keySet()) {
            IAsset asset = (IAsset) subMap.get(assetName);
            asset.unload();
            removed++;
            count--;
        }
        return removed;
    }

    /**
     * Gets a given asset with the given asset name and casts it to type
     *
     * @param assetName the asset to get
     * @param type      the type of asset
     * @param <T>       the type cast
     * @return the cast object with given type
     */
    public <T extends IAsset> T get(String assetName, Class<T> type) {
        if (map.containsKey(type)) {
            Map<String, Object> subMap = map.get(type);
            if (subMap.containsKey(assetName)) {
                Object obj = subMap.get(assetName);
                return type.cast(obj);
            }
        }
        return null;
    }

    /**
     * Checks to see if this pack is empty
     *
     * @return return true if empty
     */
    public boolean isEmpty() {
        return count <= 0;
    }
}
