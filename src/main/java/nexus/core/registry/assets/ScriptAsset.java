package nexus.core.registry.assets;

import lombok.Getter;
import lombok.Setter;
import nexus.context.Context;
import nexus.core.registry.Pack;
import nexus.util.CommonUtils;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.io.*;


/**
 * Represents an image in opengl
 */
public class ScriptAsset implements IAsset {
    @Getter
    private String name;
    @Getter
    @Setter
    private Pack pack;
    @Getter
    private File file;
    @Getter
    private boolean loaded = false;
    private Context context;

    /**
     * Creates an image and gets the file name
     *
     * @param file
     */
    public ScriptAsset(File file, Context context) {
        this.file = file;
        this.context = context;
        try {
            this.name = CommonUtils.removeExtension(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an image to opengl
     */
    public void load() {
        if (!loaded) {
            loaded = true;
            try (PythonInterpreter pyInterp = new PythonInterpreter()) {
                try {
                    pyInterp.getSystemState().path.append(new PyString(file.getParent()));
                    pyInterp.set("context", context);
                    pyInterp.execfile(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Unloads an image from opengl
     */
    public void unload() {
        if (loaded) {
            loaded = false;
        }
    }
}
