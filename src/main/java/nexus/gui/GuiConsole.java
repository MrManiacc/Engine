package nexus.gui;

import imgui.ImVec2;
import imgui.enums.ImGuiCol;
import imgui.enums.ImGuiInputTextFlags;
import imgui.enums.ImGuiStyleVar;
import imgui.enums.ImGuiWindowFlags;
import nexus.context.Context;
import nexus.core.input.Input;
import nexus.window.Display;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static imgui.ImGui.*;

/**
 * Represents a console in imgui
 */
public class GuiConsole {
    private Context context;
    private Input input;
    private Config config;
    private Display display;
    private List<String> lines = new ArrayList<>();
    private boolean scrollToBottom = false;
    private boolean reclaimFocus = false;
    private final Date date = new Date();
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public GuiConsole(Context context) {
        this.context = context;
        this.input = context.get(Input.class);
        this.display = context.get(Display.class);
        this.config = context.get(Config.class);
    }

    /**
     * Draw the console
     */
    public void draw() {
        if (config.console.get()) {
            float w = display.getFrameWidth() / 2.0f;
            float h = display.getFrameHeight() / 3.0f;
            float x = (display.getFrameWidth() / 2.0f) - w / 2.0f;
            float y = (display.getFrameHeight() - h) - 100;
            if (config.assetBrowser.get()) {
                x = display.getFrameWidth() / 5.0f;
                w = display.getFrameWidth() - x;
                y += 100;
            }
            setNextWindowSize(w, h);
            setNextWindowPos(x, y);
            if (begin("Console", config.console, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse)) {
                float footerHeight = 62;
                if (beginChild("Scrolling region", w - 10, h - footerHeight, false)) {
                    for (String line : lines) {
                        boolean pop = false;
                        if (line.startsWith("[error]")) {
                            pushStyleColor(ImGuiCol.Text, 0.8f, 0.1f, 0.1f, 1.0f);
                            pop = true;
                        } else if (line.startsWith("[warning]")) {
                            pushStyleColor(ImGuiCol.Text, 0.87f, 0.901f, 0.125f, 1.0f);
                            pop = true;
                        } else if (line.startsWith("[info]")) {
                            pushStyleColor(ImGuiCol.Text, 0.815f, 0.812f, 0.772f, 1.0f);
                            pop = true;
                        }
                        String[] parts = line.split("-");
                        textUnformatted(parts[0].trim());
                        if (pop)
                            popStyleColor();
                        sameLine();
                        textUnformatted("- " + parts[1]);
                    }
                }
                if (scrollToBottom) {
                    setScrollHereY(1.0f);
                    scrollToBottom = false;
                }

                endChild();
                separator();
                pushItemWidth(w - 70);
                if (inputText("##console", config.consoleInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
                    info(config.consoleInput.get());
                    config.consoleInput.set("");
                    scrollToBottom = true;
                    reclaimFocus = true;
                }
                popItemWidth();
                setItemDefaultFocus();
                if (reclaimFocus) {
                    setKeyboardFocusHere(-1);
                    reclaimFocus = false;
                }
                sameLine();
                if (button("Clear")) {
                    lines.clear();
                }

                end();
            }
        }
    }

    /**
     * Add a line to the console
     *
     * @param log
     */
    public void addLog(String log) {
        lines.add(log);
    }

    /**
     * Logs info to the console
     *
     * @param info the info to log
     */
    public void info(String info) {
        String date = getDateTime();
        lines.add("[info]" + " [" + date + "]-" + info);
    }

    /**
     * Logs an error to the console
     *
     * @param error error to log
     */
    public void error(String error) {
        String date = getDateTime();
        lines.add("[error]" + " [" + date + "]-" + error);
    }

    /**
     * Logs an warning to the console
     *
     * @param warning warning to log
     */
    public void warn(String warning) {
        String date = getDateTime();
        lines.add("[warning]" + " [" + date + "]-" + warning);
    }

    /**
     * Gets the date time
     *
     * @return date time
     */
    private String getDateTime() {
        return formatter.format(date);
    }

}
