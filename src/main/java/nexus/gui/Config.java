package nexus.gui;

import com.artemis.Component;
import imgui.ImBool;
import imgui.ImString;

/**
 * This methods stores all of the imgui variables,
 * and can be globally accessed via the context
 */
public class Config {
    public final ImBool mainWindow = new ImBool();
    public final ImBool assetBrowser = new ImBool(true);
    public final ImBool console = new ImBool();
    public final ImString consoleInput = new ImString();
    public final ImString modelInput = new ImString();
    public Component selectedComponent;
    public int selectedEntity;
}
