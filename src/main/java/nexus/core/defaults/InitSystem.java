package nexus.core.defaults;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.BaseSystem;

/**
 * Represents a system that only needs to run it's initializers and shutdowns
 */
public abstract class InitSystem extends BaseEntitySystem {
    /**
     * We don't need any processing
     *
     * @return returns false for no processing
     */
    protected boolean checkProcessing() {
        return false;
    }

    /**
     * Called before other initialization
     */
    public void preInitialization() {
    }

    /**
     * Called after the other initialization
     */
    public void postInitialization() {
    }

    /**
     * Overridden because we're going to have our own initializers
     */
    protected void initialize() {
    }

    /**
     * Overridden so that it's not implemented, because this system doesn't need
     * to process anything, only do initializers
     */
    protected void processSystem() {
    }
}
