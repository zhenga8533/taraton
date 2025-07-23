package net.volcaronitee.taraton.config.controller;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * A controller that pairs a key controller with a button that performs an action.
 */
public class KeyButtonController<K> implements Controller<K> {
    private final Option<K> option;
    private final double ratio;
    private final Controller<K> keyController;
    private final Text buttonText;
    private final Consumer<YACLScreen> buttonAction;

    /**
     * Creates a new KeyButtonController.
     * 
     * @param option The option that this controller is associated with.
     * @param ratio The ratio of the key controller's width to the total width of the controller.
     * @param keyController A function that provides a key controller for the option.
     * @param buttonText The text to display on the button.
     * @param buttonAction The action to perform when the button is clicked.
     */
    private KeyButtonController(Option<K> option, double ratio,
            Function<Option<K>, ControllerBuilder<K>> keyController, Text buttonText,
            Consumer<YACLScreen> buttonAction) {
        this.option = option;
        this.ratio = ratio;
        this.buttonText = buttonText;
        this.buttonAction = buttonAction;

        this.keyController =
                dummyOption(null, keyController, option::pendingValue, option::requestSet)
                        .controller();
    }

    /**
     * Creates a dummy option for the key controller.
     * 
     * @param <T> The type of the option.
     * @param name The name of the option, can be null.
     * @param controller A function that provides a controller for the option.
     * @param get A supplier to get the current value of the option.
     * @param set A consumer to set the value of the option.
     * @return A dummy option that can be used to create a controller.
     */
    private static <T> Option<T> dummyOption(String name,
            Function<Option<T>, ControllerBuilder<T>> controller, Supplier<T> get,
            Consumer<T> set) {
        return Option.<T>createBuilder().name(name != null ? Text.literal(name) : Text.empty())
                .stateManager(StateManager.createInstant(get.get(), get, set))
                .controller(controller).build();
    }

    @Override
    public Option<K> option() {
        return option;
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new KeyButtonControllerElement<>(this, screen, widgetDimension);
    }

    @Override
    public Text formatValue() {
        return keyController.formatValue();
    }

    /**
     * Builder for creating a KeyButtonController.
     */
    public static class Builder<K> implements ControllerBuilder<K> {
        private final Option<K> option;
        private Function<Option<K>, ControllerBuilder<K>> keyController;
        private Text buttonText = Text.literal("Action");
        private Consumer<YACLScreen> buttonAction = screen -> {
        };
        private double ratio = 0.75;

        /**
         * Creates a new Builder for KeyButtonController.
         * 
         * @param option The option that this controller will be associated with.
         */
        private Builder(Option<K> option) {
            this.option = option;
        }

        /**
         * Creates a new Builder for KeyButtonController with the given option.
         * 
         * @param <T> The type of the option.
         * @param option The option that this controller will be associated with.
         * @return A new Builder instance for KeyButtonController.
         */
        public static <T> Builder<T> create(Option<T> option) {
            return new Builder<>(option);
        }

        /**
         * Sets the key controller for this KeyButtonController.
         * 
         * @param keyController A function that provides a controller for the key option.
         * @return This Builder instance for method chaining.
         */
        public Builder<K> keyController(Function<Option<K>, ControllerBuilder<K>> keyController) {
            this.keyController = keyController;
            return this;
        }

        /**
         * Sets the key controller for this KeyButtonController using a supplier.
         * 
         * @param text The text to display on the button.
         * @param action The action to perform when the button is clicked.
         * @return This Builder instance for method chaining.
         */
        public Builder<K> button(Text text, Consumer<YACLScreen> action) {
            this.buttonText = text;
            this.buttonAction = action;
            return this;
        }

        /**
         * Sets the text to display on the button.
         * 
         * @param ratio The ratio of the key controller's width to the total width of the
         *        controller.
         * @return This Builder instance for method chaining.
         */
        public Builder<K> ratio(double ratio) {
            this.ratio = ratio;
            return this;
        }

        @Override
        public Controller<K> build() {
            return new KeyButtonController<>(option, ratio, keyController, buttonText,
                    buttonAction);
        }
    }

    /**
     * Widget that combines a key controller and a button.
     */
    private static class KeyButtonControllerElement<K>
            extends ControllerWidget<KeyButtonController<K>> {
        private final AbstractWidget keyElement;
        private final ButtonWidget buttonElement;
        private final double ratio;

        /**
         * Creates a new KeyButtonControllerElement.
         * 
         * @param control The KeyButtonController that this element represents.
         * @param screen The YACLScreen that this element is part of.
         * @param dim The dimensions of the element.
         */
        public KeyButtonControllerElement(KeyButtonController<K> control, YACLScreen screen,
                Dimension<Integer> dim) {
            super(control, screen, dim);
            this.ratio = control.ratio;

            // Create the key element with the specified width ratio
            Dimension<Integer> keyDimension = dim.withWidth((int) (dim.width() * ratio));
            this.keyElement = control.keyController.provideWidget(screen, keyDimension);

            // Create the button element with the remaining width
            Dimension<Integer> buttonDimension = dim.moved(keyDimension.width(), 0)
                    .withWidth(dim.width() - keyDimension.width());
            this.buttonElement = ButtonWidget
                    .builder(control.buttonText, (button) -> control.buttonAction.accept(screen))
                    .dimensions(buttonDimension.x(), buttonDimension.y(), buttonDimension.width(),
                            buttonDimension.height())
                    .build();
        }

        @Override
        public void setDimension(Dimension<Integer> dim) {
            Dimension<Integer> keyDimension = dim.withWidth((int) (dim.width() * ratio));
            keyElement.setDimension(keyDimension);

            Dimension<Integer> buttonDimension = dim.moved(keyDimension.width(), 0)
                    .withWidth(dim.width() - keyDimension.width());
            buttonElement.setX(buttonDimension.x());
            buttonElement.setY(buttonDimension.y());
            buttonElement.setWidth(buttonDimension.width());
            buttonElement.setHeight(buttonDimension.height());

            super.setDimension(dim);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            keyElement.render(context, mouseX, mouseY, delta);
            buttonElement.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return keyElement.mouseClicked(mouseX, mouseY, button)
                    || buttonElement.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return keyElement.mouseReleased(mouseX, mouseY, button)
                    || buttonElement.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX,
                double deltaY) {
            return keyElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                    || buttonElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount,
                double verticalAmount) {
            return keyElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
                    || buttonElement.mouseScrolled(mouseX, mouseY, horizontalAmount,
                            verticalAmount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return keyElement.keyPressed(keyCode, scanCode, modifiers)
                    || buttonElement.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return keyElement.keyReleased(keyCode, scanCode, modifiers)
                    || buttonElement.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return keyElement.charTyped(chr, modifiers) || buttonElement.charTyped(chr, modifiers);
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }
    }
}
