package nexus.core.defaults;

import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;

/**
 * Represents a
 */
public abstract class IteratingInitSystem extends InitSystem {
    /**
     * We need to actually check processing now
     *
     * @return processing check
     */
    protected boolean checkProcessing() {
        return true;
    }

    /**
     * Process the entity at given index
     *
     * @param entityID the entity to process
     */
    protected abstract void process(int entityID);

    /**
     * Processing system, update entities
     */
    protected void processSystem() {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            process(ids[i]);
        }
    }
}
