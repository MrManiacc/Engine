package nexus.context;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Represents a context. This is used to pass around
 * variables in a thread safe convient way
 */
public class Context {
    private final Map<Class<?>, Object> map = Maps.newConcurrentMap();

    /**
     * Gets the given type of class from the context
     *
     * @param type the type to get
     * @param <T>  the defined type
     * @return the object with the given type
     */
    public <T> T get(Class<? extends T> type) {
        if (type == Context.class) {
            return type.cast(this);
        }
        return type.cast(map.get(type));
    }

    /**
     * Puts an object of the given type into the map
     *
     * @param type   the type of object
     * @param object the object to put
     * @param <T>    the defined type
     * @param <U>    the defined u type
     */
    public <T, U extends T> U put(Class<T> type, U object) {
        map.put(type, object);
        return object;
    }

    /**
     * Checks to see if the given type is present
     *
     * @param type the type to get
     * @param <T>  the type
     * @return returns true if type is present
     */
    public <T> boolean has(Class<T> type) {
        return map.containsKey(type);
    }

}
