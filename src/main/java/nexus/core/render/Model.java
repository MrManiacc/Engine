package nexus.core.render;

import com.artemis.Component;
import nexus.util.opengl.Vao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Represents a model
 */
public class Model extends Component {
    private final List<Vao> vaos = new ArrayList<>();

    /**
     * Create a new model with a list of vaos
     *
     * @param vaos
     */
    public void addAllVaos(Vao... vaos) {
        Collections.addAll(this.vaos, vaos);
    }

    /**
     * Adds a vao to the given model
     */
    public void addVao(Vao vao) {
        vaos.add(vao);
    }


    /**
     * Binds a model with the specified attributes
     *
     * @param model the model to bind to
     */
    public boolean bind(int model) {
        if (model > vaos.size() - 1 || model < 0)
            return false;
        vaos.get(model).bind();
        return true;
    }

    /**
     * Binds the first vao
     *
     * @return returns true if bound successfully
     */
    public boolean bindFirst() {
        if (vaos.isEmpty())
            return false;
        vaos.get(0).bind();
        return true;
    }

    /**
     * Draw all vaos of this mesh
     */
    public void draw() {
        for (Vao vao : vaos) {
            vao.bind();
            glDrawElements(GL_TRIANGLES, vao.getIndexCount(), GL_UNSIGNED_INT, 0);
            vao.unbind();
        }
    }

    /**
     * Gets the index count for a given model
     *
     * @param model the model to get a count for
     * @return count for given model
     */
    public int getIndexCount(int model) {
        if (model < 0 || model > vaos.size() - 1)
            return -1;
        return vaos.get(model).getIndexCount();
    }

    /**
     * Gets the index count for the first model;
     *
     * @return the index count
     */
    public int getFirstIndexCount() {
        return getIndexCount(0);
    }

    /**
     * Unbinds the first vao
     *
     * @return returns true if unbound successfully
     */
    public boolean unbindFirst() {
        if (vaos.isEmpty())
            return false;
        vaos.get(0).unbind();
        return true;
    }

    /**
     * Binds a model with the specified attributes
     *
     * @param model the model to bind to
     */
    public boolean unbind(int model) {
        if (model > vaos.size() - 1 || model < 0)
            return false;
        vaos.get(model).unbind();
        return true;
    }
}
