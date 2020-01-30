package nexus.gui;

import imgui.enums.*;
import nexus.context.Context;
import nexus.core.input.Input;
import nexus.core.registry.Registry;
import nexus.core.registry.assets.IAsset;
import nexus.core.registry.Pack;
import nexus.core.registry.assets.ImageAsset;
import nexus.core.registry.assets.MeshAsset;
import nexus.core.registry.assets.ShaderAsset;
import nexus.window.Display;

import java.util.List;
import java.util.Map;

import static imgui.ImGui.*;
import static imgui.ImGui.popStyleVar;

/**
 * represents the asset browser
 */
public class GuiAssetBrowser {
    private Context context;
    private Input input;
    private Config config;
    private Display display;
    private Registry registry;
    private Map<Pack, List<IAsset>> meshAssets;
    private Map<Pack, List<IAsset>> shaderAssets;
    private Map<Pack, List<IAsset>> imageAssets;
    private int activeAsset = 0;

    public GuiAssetBrowser(Context context) {
        this.context = context;
        this.input = context.get(Input.class);
        this.display = context.get(Display.class);
        this.config = context.get(Config.class);
        this.registry = context.get(Registry.class);
        updateAssets();
    }

    /**
     * updates the nexus.core.assets from the registry
     */
    private void updateAssets() {
        meshAssets = registry.getAssetsByType(MeshAsset.class);
        shaderAssets = registry.getAssetsByType(ShaderAsset.class);
        imageAssets = registry.getAssetsByType(ImageAsset.class);
    }

    /**
     * Draw the asset browser
     */
    public void draw(float parentSize) {
        if (config.assetBrowser.get()) {
            if (collapsingHeader("Assets")) {
                if (beginChild("models", 0, 300, true)) {
                    boolean pushed = false;
                    if (activeAsset == 0) {
                        pushStyleColor(ImGuiCol.Button, 0.5f, 0.5f, 0.5f, 1.0f);
                        pushed = true;
                    }
                    if (button("Models"))
                        activeAsset = 0;
                    if (pushed) {
                        popStyleColor();
                        pushed = false;
                    }
                    if (activeAsset == 1) {
                        pushStyleColor(ImGuiCol.Button, 0.5f, 0.5f, 0.5f, 1.0f);
                        pushed = true;
                    }
                    sameLine();
                    if (button("Shaders"))
                        activeAsset = 1;
                    if (pushed) {
                        popStyleColor();
                        pushed = false;
                    }
                    sameLine();
                    if (activeAsset == 2) {
                        pushStyleColor(ImGuiCol.Button, 0.5f, 0.5f, 0.5f, 1.0f);
                        pushed = true;
                    }
                    if (button("Images"))
                        activeAsset = 2;
                    if (pushed) {
                        popStyleColor();
                        pushed = false;
                    }
                    separator();
                    if (activeAsset == 0)
                        drawAssets(parentSize, meshAssets, "subMeshes");
                    if (activeAsset == 1)
                        drawAssets(parentSize, shaderAssets, "subShaders");
                    if (activeAsset == 2)
                        drawAssets(parentSize, imageAssets, "subImages");
                    endChild();
                }
            }
        }
    }

    /**
     * Draws a list of nexus.core.assets
     *
     * @param parentSize the size of the container
     * @param assetMap   the map of nexus.core.assets to render
     * @param childName  the child window name
     */
    private void drawAssets(float parentSize, Map<Pack, List<IAsset>> assetMap, String childName) {
        if (beginChild(childName, 0, 0)) {
            pushItemWidth(parentSize - 40);
            for (Pack pack : assetMap.keySet()) {
                List<IAsset> assets = assetMap.get(pack);
                if (treeNode(pack.getName())) {
                    for (IAsset asset : assets) {
                        indent(5);
                        selectable(asset.getName() + "##" + pack.getName());
                        unindent(5);
                    }
                    treePop();
                }
            }
            popItemWidth();
        }
        endChild();
    }

    private void drawTitle(String title) {
        indent(5);
        textColored(0, 0.3f, 0.85f, 1.0f, title);
        separator();
        dummy(0, 3);
    }


}
