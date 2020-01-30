package nexus.gui;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import imgui.ImString;
import imgui.enums.ImGuiInputTextFlags;
import nexus.context.Context;
import nexus.core.input.Input;
import nexus.core.math.Transform;
import nexus.core.registry.Registry;
import nexus.window.Display;

import static imgui.ImGui.*;

/**
 * represents the asset browser
 */
public class GuiEntityBrowser {
    private Context context;
    private Input input;
    private Config config;
    private Display display;
    private Registry registry;
    private World world;
    private EntitySubscription subscription;
    private Bag<Component> componentBag = new Bag<>();
    private ImString componentFilter = new ImString();
    private String filter = "";
    private boolean reclaimFocus = false;

    public GuiEntityBrowser(Context context) {
        this.context = context;
        this.input = context.get(Input.class);
        this.display = context.get(Display.class);
        this.config = context.get(Config.class);
        this.registry = context.get(Registry.class);
        this.world = context.get(World.class);
        this.subscription = world.getAspectSubscriptionManager().get(Aspect.all());
    }

    /**
     * Draw the asset browser
     */
    public void draw() {
        if (config.assetBrowser.get()) {
            IntBag entityIds = subscription.getEntities();
            if (collapsingHeader("Entities")) {
                if (beginChild("entities", 0, 300, true)) {
                    if (beginChild("subEntities", 0, 225, false)) {
                        for (int i = 0; i < entityIds.size(); i++) {
                            world.getEntity(entityIds.get(i)).getComponents(componentBag);
                            boolean passed = false;
                            if (!filter.isBlank()) {
                                for (int j = 0; j < componentBag.size(); j++) {
                                    Component component = componentBag.get(j);
                                    if (component.getClass().getSimpleName().toLowerCase().trim().startsWith(filter.toLowerCase().trim())) {
                                        passed = true;
                                    }
                                }
                            }
                            if (passed || filter.isBlank()) {
                                if (treeNode("Entity [" + entityIds.get(i) + "]")) {
                                    for (int j = 0; j < componentBag.size(); j++) {
                                        Component component = componentBag.get(j);
                                        indent(5);
                                        if (selectable(component.getClass().getSimpleName())) {
                                            config.selectedComponent = component;
                                            config.selectedEntity = entityIds.get(i);
                                        }
                                        unindent(5);
                                    }
                                    treePop();
                                }
                            }
                            componentBag.clear();
                        }
                    }
                    endChild();
                    separator();
                    labelText("##FilterLabel", "Filter");
                    pushItemWidth(430);
                    if (inputText("##Filter", componentFilter, ImGuiInputTextFlags.EnterReturnsTrue)) {
                        filter = componentFilter.get();
                        componentFilter.set("");
                        reclaimFocus = true;
                    }
                    setItemDefaultFocus();
                    if (reclaimFocus) {
                        setKeyboardFocusHere(-1);
                        reclaimFocus = false;
                    }
                    sameLine();
                    if (button("Clear##1")) {
                        componentFilter.set("");
                        filter = "";
                        config.selectedComponent = null;
                        config.selectedEntity = -1;
                    }
                    endChild();
                }
            }
        }
    }


}
