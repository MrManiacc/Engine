package nexus.gui;

import static imgui.ImGui.*;

import imgui.enums.ImGuiCond;
import imgui.enums.ImGuiStyleVar;
import imgui.enums.ImGuiWindowFlags;
import nexus.context.Context;
import nexus.core.input.Input;
import nexus.window.Display;

/**
 * Represents the imgui menu
 */
public class Menu {
    private Context context;
    private Input input;
    private Config config;
    private Display display;
    /**
     * List of menus
     **/
    private GuiConsole console;
    private GuiAssetBrowser assetBrowser;
    private GuiEntityBrowser entityBrowser;

    public Menu(Context context) {
        this.context = context;
    }

    /**
     * Creates links to all the needed objects
     */
    public void initialize() {
        this.input = context.get(Input.class);
        this.display = context.get(Display.class);
        this.config = context.put(Config.class, new Config());
        this.console = context.put(GuiConsole.class, new GuiConsole(context));
        this.assetBrowser = context.put(GuiAssetBrowser.class, new GuiAssetBrowser(context));
        this.entityBrowser = context.put(GuiEntityBrowser.class, new GuiEntityBrowser(context));
    }



    /**
     * Renders the imgui menu
     *
     * @param deltaTime deltaTime
     */
    public void render(float deltaTime) {
        drawMenuBar();
        console.draw();
        drawLeftPanel();
    }

    /**
     * Draw the left panel
     */
    private void drawLeftPanel() {
        float width = display.getFrameWidth() / 5.0f;
        setNextWindowSize(width, 0);
        setNextWindowPos(0, 20, ImGuiCond.FirstUseEver);
        pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        if (begin("Assets and Entities", ImGuiWindowFlags.AlwaysAutoResize)) {
            assetBrowser.draw(width);
            entityBrowser.draw();
        }
        end();
        popStyleVar();
    }

    /**
     * Draws the menu bar
     */
    private void drawMenuBar() {
        if (beginMainMenuBar()) {
            if (beginMenu("Windows")) {
                menuItem("Asset Browser", "", config.assetBrowser);
                menuItem("Console", "", config.console);
                endMenu();
            }
            endMainMenuBar();
        }
    }


}
