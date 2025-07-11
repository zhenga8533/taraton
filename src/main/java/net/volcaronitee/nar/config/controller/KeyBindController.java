package net.volcaronitee.nar.config.controller;

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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * A self-contained controller for managing a keybind option in YACL3.
 */
public class KeyBindController implements Controller<Integer> {
    private static final BiMap<Integer, Text> KEY_NAMES = HashBiMap.create();

    static {
        // Manually map all common non-printable keys and mouse buttons
        KEY_NAMES.put(GLFW.GLFW_KEY_UNKNOWN, Text.literal("None"));
        KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_LEFT, Text.literal("Left Mouse Button"));
        KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT, Text.literal("Right Mouse Button"));
        KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, Text.literal("Middle Mouse Button"));
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

        for (int i = 0; i < 25; i++) {
            KEY_NAMES.put(GLFW.GLFW_KEY_F1 + i, Text.literal("F" + (i + 1)));
        }
        for (int i = 0; i < 10; i++) {
            KEY_NAMES.put(GLFW.GLFW_KEY_KP_0 + i, Text.literal("Numpad " + i));
        }
        for (int i = 0; i < 8; i++) {
            KEY_NAMES.put(GLFW.GLFW_MOUSE_BUTTON_1 + i, Text.literal("Mouse Button " + (i + 1)));
        }

        KEY_NAMES.put(GLFW.GLFW_KEY_KP_DECIMAL, Text.literal("Numpad ."));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_DIVIDE, Text.literal("Numpad /"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_MULTIPLY, Text.literal("Numpad *"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_SUBTRACT, Text.literal("Numpad -"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_ADD, Text.literal("Numpad +"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_ENTER, Text.literal("Numpad Enter"));
        KEY_NAMES.put(GLFW.GLFW_KEY_KP_EQUAL, Text.literal("Numpad ="));
    }

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

    // --- Controller Implementation ---

    private final Option<Integer> option;

    public KeyBindController(Option<Integer> option) {
        this.option = option;
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


    // --- Nested Builder Class ---

    public static class Builder implements ControllerBuilder<Integer> {
        private final Option<Integer> option;

        public Builder(Option<Integer> option) {
            this.option = option;
        }

        public static Builder create(Option<Integer> option) {
            return new Builder(option);
        }

        @Override
        public Controller<Integer> build() {
            return new KeyBindController(this.option);
        }
    }


    // --- Nested Widget Class ---

    public static class KeyBindControllerWidget extends ControllerWidget<KeyBindController> {
        private boolean listening = false;

        public KeyBindControllerWidget(KeyBindController control, YACLScreen screen,
                Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        private void setListening(boolean listening) {
            this.listening = listening;
            // When we start listening, we want to be focused.
            // When we stop, we can unfocus.
            this.setFocused(listening);
        }

        private Text getMessage() {
            if (this.listening) {
                return Text.literal("> ").append(Text.literal("..."));
            } else {
                return this.control.formatValue();
            }
        }

        @Override
        public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
            // We override render completely because this widget is a single button,
            // not a label + control like the base class assumes.
            this.hovered = this.isMouseOver(mouseX, mouseY);

            // Render a simple button background
            graphics.fill(this.getDimension().x(), this.getDimension().y(),
                    this.getDimension().x() + this.getDimension().width(),
                    this.getDimension().y() + this.getDimension().height(), 0x80000000);
            if (this.isHovered() || this.isFocused()) {
                graphics.drawBorder(this.getDimension().x(), this.getDimension().y(),
                        this.getDimension().width(), this.getDimension().height(), 0xFFFFFFFF);
            }

            // Render the text
            int textColor =
                    this.isAvailable() ? (isFocused() ? 0xFFFFFFFF : 0xFFE0E0E0) : 0xFFa0a0a0;
            graphics.drawCenteredTextWithShadow(this.textRenderer, this.getMessage(),
                    this.getDimension().x() + this.getDimension().width() / 2,
                    this.getDimension().y() + (this.getDimension().height() - 8) / 2, textColor);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.listening) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    this.setListening(false);
                } else {
                    this.control.option().requestSet(keyCode);
                    this.setListening(false);
                }
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.isMouseOver(mouseX, mouseY) && this.isAvailable()) {
                if (this.listening) {
                    this.control.option().requestSet(button - 100);
                    this.setListening(false);
                } else {
                    this.setListening(true);
                }
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
            return this.textRenderer.getWidth(this.getMessage()) + 4;
        }

        @Override
        protected Text getValueText() {
            return getMessage();
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
