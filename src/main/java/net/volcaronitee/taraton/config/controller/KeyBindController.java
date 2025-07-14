package net.volcaronitee.taraton.config.controller;

import org.lwjgl.glfw.GLFW;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.text.Text;

/**
 * A controller for setting key bindings.
 */
public class KeyBindController implements Controller<Integer> {
    private static final BiMap<Integer, Text> KEY_NAMES = HashBiMap.create();
    static {
        KEY_NAMES.put(GLFW.GLFW_KEY_UNKNOWN, Text.literal("None"));
        KEY_NAMES.put(GLFW.GLFW_KEY_SPACE, Text.literal("Space"));
        KEY_NAMES.put(GLFW.GLFW_KEY_APOSTROPHE, Text.literal("'"));
        KEY_NAMES.put(GLFW.GLFW_KEY_COMMA, Text.literal(","));
        KEY_NAMES.put(GLFW.GLFW_KEY_MINUS, Text.literal("-"));
        KEY_NAMES.put(GLFW.GLFW_KEY_PERIOD, Text.literal("."));
        KEY_NAMES.put(GLFW.GLFW_KEY_SLASH, Text.literal("/"));
        KEY_NAMES.put(GLFW.GLFW_KEY_SEMICOLON, Text.literal(";"));
        KEY_NAMES.put(GLFW.GLFW_KEY_EQUAL, Text.literal("="));
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_BRACKET, Text.literal("["));
        KEY_NAMES.put(GLFW.GLFW_KEY_BACKSLASH, Text.literal("\\"));
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_BRACKET, Text.literal("]"));
        KEY_NAMES.put(GLFW.GLFW_KEY_GRAVE_ACCENT, Text.literal("`"));
        KEY_NAMES.put(GLFW.GLFW_KEY_ESCAPE, Text.literal("Escape"));
        KEY_NAMES.put(GLFW.GLFW_KEY_ENTER, Text.literal("Enter"));
        KEY_NAMES.put(GLFW.GLFW_KEY_TAB, Text.literal("Tab"));
        KEY_NAMES.put(GLFW.GLFW_KEY_BACKSPACE, Text.literal("Backspace"));
        KEY_NAMES.put(GLFW.GLFW_KEY_INSERT, Text.literal("Insert"));
        KEY_NAMES.put(GLFW.GLFW_KEY_DELETE, Text.literal("Delete"));
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT, Text.literal("Right Arrow"));
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT, Text.literal("Left Arrow"));
        KEY_NAMES.put(GLFW.GLFW_KEY_DOWN, Text.literal("Down Arrow"));
        KEY_NAMES.put(GLFW.GLFW_KEY_UP, Text.literal("Up Arrow"));
        KEY_NAMES.put(GLFW.GLFW_KEY_PAGE_UP, Text.literal("Page Up"));
        KEY_NAMES.put(GLFW.GLFW_KEY_PAGE_DOWN, Text.literal("Page Down"));
        KEY_NAMES.put(GLFW.GLFW_KEY_HOME, Text.literal("Home"));
        KEY_NAMES.put(GLFW.GLFW_KEY_END, Text.literal("End"));
        KEY_NAMES.put(GLFW.GLFW_KEY_CAPS_LOCK, Text.literal("Caps Lock"));
        KEY_NAMES.put(GLFW.GLFW_KEY_SCROLL_LOCK, Text.literal("Scroll Lock"));
        KEY_NAMES.put(GLFW.GLFW_KEY_NUM_LOCK, Text.literal("Num Lock"));
        KEY_NAMES.put(GLFW.GLFW_KEY_PRINT_SCREEN, Text.literal("Print Screen"));
        KEY_NAMES.put(GLFW.GLFW_KEY_PAUSE, Text.literal("Pause"));
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_SHIFT, Text.literal("Left Shift"));
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_CONTROL, Text.literal("Left Ctrl"));
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_ALT, Text.literal("Left Alt"));
        KEY_NAMES.put(GLFW.GLFW_KEY_LEFT_SUPER, Text.literal("Left Super"));
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_SHIFT, Text.literal("Right Shift"));
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_CONTROL, Text.literal("Right Ctrl"));
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_ALT, Text.literal("Right Alt"));
        KEY_NAMES.put(GLFW.GLFW_KEY_RIGHT_SUPER, Text.literal("Right Super"));
        KEY_NAMES.put(GLFW.GLFW_KEY_MENU, Text.literal("Menu"));

        KEY_NAMES.put(GLFW.GLFW_KEY_KP_DECIMAL, Text.literal("Numpad ."));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_DIVIDE, Text.literal("Numpad /"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_MULTIPLY, Text.literal("Numpad *"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_SUBTRACT, Text.literal("Numpad -"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_ADD, Text.literal("Numpad +"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_ENTER, Text.literal("Numpad Enter"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_EQUAL, Text.literal("Numpad ="));

        KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_LEFT, Text.literal("Left Mouse"));
        KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT, Text.literal("Right Mouse"));
        KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, Text.literal("Middle Mouse"));

        for (int i = 3; i < 8; i++) {
            KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_1 + i, Text.literal("Mouse " + (i + 1)));
        }

        for (int i = 0; i < 25; i++) {
            KEY_NAMES.put(GLFW.GLFW_KEY_F1 + i, Text.literal("F" + (i + 1)));
        }

        for (int i = 0; i < 10; i++) {
            KEY_NAMES.put(GLFW.GLFW_KEY_KP_0 + i, Text.literal("Numpad " + i));
        }
    }

    private final Option<Integer> option;

    /**
     * Create a new KeyBindController.
     * 
     * @param option The option to control.
     */
    public KeyBindController(Option<Integer> option) {
        this.option = option;
    }

    /**
     * Get the display text for a given key code.
     * 
     * @param keyCode The GLFW key code.
     * @return The display text for the key.
     */
    public static Text getkeyText(int keyCode) {
        if (KEY_NAMES.containsKey(keyCode)) {
            return KEY_NAMES.get(keyCode);
        }

        String keyName = GLFW.glfwGetKeyName(keyCode, 0);
        if (keyName != null) {
            return Text.literal(keyName.toUpperCase());
        }

        return Text.literal("Unknown (" + keyCode + ")");
    }

    @Override
    public Option<Integer> option() {
        return this.option;
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new KeyBindControllerWidget(this, screen, widgetDimension);
    }

    @Override
    public Text formatValue() {
        return getkeyText(this.option.pendingValue());
    }

    /**
     * A builder for KeyBindController.
     */
    public static class Builder implements ControllerBuilder<Integer> {
        private final Option<Integer> option;

        /**
         * Create a new Builder.
         * 
         * @param option The option to control.
         */
        public Builder(Option<Integer> option) {
            this.option = option;
        }

        /**
         * Create a new Builder.
         * 
         * @param option The option to control.
         * @return A new Builder.
         */
        public static Builder create(Option<Integer> option) {
            return new Builder(option);
        }

        @Override
        public Controller<Integer> build() {
            return new KeyBindController(this.option);
        }
    }

    /**
     * The widget for the KeyBindController.
     */
    public static class KeyBindControllerWidget extends ControllerWidget<KeyBindController> {
        private boolean listening = false;

        /**
         * Create a new KeyBindControllerWidget.
         * 
         * @param control The KeyBindController to use.
         * @param screen The YACLScreen to use.
         * @param dim The dimensions of the widget.
         */
        public KeyBindControllerWidget(KeyBindController control, YACLScreen screen,
                Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        /**
         * Set whether the widget is listening for key input.
         * 
         * @param listening Whether the widget is listening.
         */
        private void setListening(boolean listening) {
            this.listening = listening;
            this.setFocused(listening);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.listening) {
                if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                    this.control.option().requestSet(keyCode);
                }
                this.setListening(false);
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.isAvailable() && this.isMouseOver(mouseX, mouseY)) {
                if (this.listening) {
                    this.control.option().requestSet(button);
                }
                this.setListening(!this.listening);
                return true;
            }

            if (this.listening) {
                this.setListening(false);
            }
            return false;
        }

        @Override
        public void unfocus() {
            super.unfocus();
            this.setListening(false);
        }

        @Override
        protected int getHoveredControlWidth() {
            return this.getUnhoveredControlWidth();
        }

        @Override
        protected int getUnhoveredControlWidth() {
            return this.textRenderer.getWidth(this.getValueText()) + 4;
        }

        @Override
        protected Text getValueText() {
            if (this.listening) {
                return Text.literal("> ... <");
            } else {
                return this.control.formatValue();
            }
        }

        @Override
        public SelectionType getType() {
            if (this.isFocused())
                return SelectionType.FOCUSED;
            if (this.isHovered())
                return SelectionType.HOVERED;
            return SelectionType.NONE;
        }
    }
}
