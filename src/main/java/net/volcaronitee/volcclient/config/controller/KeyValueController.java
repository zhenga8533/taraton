package net.volcaronitee.volcclient.config.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
 * A controller for managing key-value pairs, allowing for separate controllers for keys and values.
 * 
 * Credit to:
 * https://github.com/hashalite/sbutils/blob/ad8938d23d9d28685c2e33c79673d5fa09a182e0/src/main/java/net/xolt/sbutils/config/KeyValueController.java
 */
public class KeyValueController<K, V> implements Controller<KeyValueController.KeyValuePair<K, V>> {
    private final Option<KeyValuePair<K, V>> option;
    private final double ratio;
    private final Controller<K> keyController;
    private final Controller<V> valueController;

    /**
     * Creates a KeyValueController with specified options for key and value controllers.
     * 
     * @param option The option that holds the key-value pair.
     * @param ratio The ratio of the width allocated to the key controller compared to the value
     *        controller.
     * @param keyName Optional name for the key controller, can be null.
     * @param keyController Function to create the key controller, taking an Option<K> as input.
     * @param valueName Optional name for the value controller, can be null.
     * @param valueController Function to create the value controller, taking an Option<V> as input.
     */
    public KeyValueController(Option<KeyValuePair<K, V>> option, double ratio,
            @Nullable String keyName, Function<Option<K>, ControllerBuilder<K>> keyController,
            @Nullable String valueName, Function<Option<V>, ControllerBuilder<V>> valueController) {
        this.option = option;
        this.ratio = ratio;

        this.keyController =
                dummyOption(keyName, keyController, () -> option.pendingValue().getKey(),
                        (newKey) -> option.requestSet(
                                new KeyValuePair<>(newKey, option.pendingValue().getValue())))
                                        .controller();

        this.valueController =
                dummyOption(valueName, valueController, () -> option.pendingValue().getValue(),
                        (newValue) -> option.requestSet(
                                new KeyValuePair<>(option.pendingValue().getKey(), newValue)))
                                        .controller();
    }

    /**
     * Creates a dummy option for the key or value controller.
     * 
     * @param <T> The type of the key or value.
     * @param name Optional name for the controller, can be null.
     * @param controller Function to create the controller, taking an Option<T> as input.
     * @param get Supplier to get the current value of the key or value.
     * @param set Consumer to set the new value of the key or value.
     * @return An Option<T> that is configured with the provided parameters.
     */
    @SuppressWarnings("deprecation")
    private static <T> Option<T> dummyOption(@Nullable String name,
            Function<Option<T>, ControllerBuilder<T>> controller, Supplier<T> get,
            Consumer<T> set) {
        return Option.<T>createBuilder()
                .name(name != null ? Text.translatable(name) : Text.literal(""))
                .binding(get.get(), get, set).instant(true).controller(controller).build();
    }

    /**
     * Builder class for creating a KeyValueController.
     */
    public static class Builder<K, V>
            implements ControllerBuilder<KeyValueController.KeyValuePair<K, V>> {
        protected final Option<KeyValueController.KeyValuePair<K, V>> option;
        private String keyName;
        private Function<Option<K>, ControllerBuilder<K>> keyController;
        private String valueName;
        private Function<Option<V>, ControllerBuilder<V>> valueController;
        private Double ratio = 0.5;

        /**
         * Creates a new Builder for KeyValueController.
         * 
         * @param option The option that holds the key-value pair.
         */
        public Builder(Option<KeyValueController.KeyValuePair<K, V>> option) {
            this.option = option;
        }

        /**
         * Creates a new Builder for KeyValueController with a specified option.
         * 
         * @param <C> The type of the key in the key-value pair.
         * @param <D> The type of the value in the key-value pair.
         * @param option The option that holds the key-value pair.
         * @return A new Builder instance for KeyValueController.
         */
        public static <C, D> Builder<C, D> create(
                Option<KeyValueController.KeyValuePair<C, D>> option) {
            return new Builder<>(option);
        }

        /**
         * Sets the key controller for the KeyValueController.
         * 
         * @param keyName Optional name for the key controller, can be null.
         * @param keyController Function that takes an Option<K> and returns a ControllerBuilder<K>.
         * @return This Builder instance for method chaining.
         */
        public Builder<K, V> keyController(String keyName,
                Function<Option<K>, ControllerBuilder<K>> keyController) {
            this.keyName = keyName;
            this.keyController = keyController;
            return this;
        }

        /**
         * Sets the value controller for the KeyValueController.
         * 
         * @param valueName Optional name for the value controller, can be null.
         * @param valueController Function that takes an Option<V> and returns a
         * @return This Builder instance for method chaining.
         */
        public Builder<K, V> valueController(String valueName,
                Function<Option<V>, ControllerBuilder<V>> valueController) {
            this.valueName = valueName;
            this.valueController = valueController;
            return this;
        }

        /**
         * Sets the ratio of the width allocated to the key controller compared to the value
         * 
         * @param ratio The ratio of the width allocated to the key controller compared to the value
         * @return This Builder instance for method chaining.
         */
        public Builder<K, V> ratio(double ratio) {
            this.ratio = ratio;
            return this;
        }

        @Override
        public Controller<KeyValueController.KeyValuePair<K, V>> build() {
            if (keyController == null || valueController == null) {
                throw new IllegalStateException(
                        "Cannot build KeyValueController without setting keyController and valueController.");
            }
            return new KeyValueController<>(option, ratio, keyName, keyController, valueName,
                    valueController);
        }
    }

    @Override
    public Option<KeyValuePair<K, V>> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return keyController.formatValue().copy().append(" | ")
                .append(valueController.formatValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        Dimension<Integer> keyDimension =
                widgetDimension.withWidth((int) ((double) widgetDimension.width() * ratio));
        Dimension<Integer> valueDimension = widgetDimension.moved(keyDimension.width(), 0)
                .withWidth((int) ((double) widgetDimension.width() - keyDimension.width()));
        AbstractWidget keyControllerElement = keyController.provideWidget(screen, keyDimension);
        AbstractWidget valueControllerElement =
                valueController.provideWidget(screen, valueDimension);
        return new KeyValueControllerElement(this, screen, widgetDimension, keyControllerElement,
                valueControllerElement, ratio);
    }

    /**
     * A widget that combines two controllers (key and value) into a single element.
     */
    public static class KeyValueControllerElement
            extends ControllerWidget<KeyValueController<?, ?>> {
        private final AbstractWidget keyElement;
        private final AbstractWidget valueElement;
        private final double ratio;

        /**
         * Creates a KeyValueControllerElement that combines key and value controllers.
         * 
         * @param control The KeyValueController that this element controls.
         * @param screen The YACLScreen that this element is part of.
         * @param dim The dimension of the element.
         * @param keyElement The widget for the key controller.
         * @param valueElement The widget for the value controller.
         * @param ratio The ratio of the width allocated to the key controller compared to the value
         *        controller.
         */
        public KeyValueControllerElement(KeyValueController<?, ?> control, YACLScreen screen,
                Dimension<Integer> dim, AbstractWidget keyElement, AbstractWidget valueElement,
                double ratio) {
            super(control, screen, dim);
            this.keyElement = keyElement;
            this.valueElement = valueElement;
            this.ratio = ratio;
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            keyElement.mouseMoved(mouseX, mouseY);
            valueElement.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean key = keyElement.mouseClicked(mouseX, mouseY, button);
            boolean value = valueElement.mouseClicked(mouseX, mouseY, button);
            return key || value;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean key = keyElement.mouseReleased(mouseX, mouseY, button);
            boolean value = valueElement.mouseReleased(mouseX, mouseY, button);
            return key || value;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX,
                double deltaY) {
            boolean key = keyElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            boolean value = valueElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return key || value;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount,
                double verticalAmount) {
            boolean key =
                    keyElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            boolean value =
                    valueElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            return key || value;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            boolean key = keyElement.keyPressed(keyCode, scanCode, modifiers);
            boolean value = valueElement.keyPressed(keyCode, scanCode, modifiers);
            return key || value;
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            boolean key = keyElement.keyReleased(keyCode, scanCode, modifiers);
            boolean value = valueElement.keyReleased(keyCode, scanCode, modifiers);
            return key || value;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            boolean key = keyElement.charTyped(chr, modifiers);
            boolean value = valueElement.charTyped(chr, modifiers);
            return key || value;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public void setDimension(Dimension<Integer> dim) {
            Dimension<Integer> keyDimension = dim.withWidth((int) ((double) dim.width() * ratio));
            Dimension<Integer> valueDimension = dim.moved(keyDimension.width(), 0)
                    .withWidth((int) ((double) dim.width() - keyDimension.width()));
            keyElement.setDimension(keyDimension);
            valueElement.setDimension(valueDimension);
            super.setDimension(dim);
        }

        @Override
        public void unfocus() {
            keyElement.unfocus();
            valueElement.unfocus();
            super.unfocus();
        }

        @Override
        public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
            keyElement.render(graphics, mouseX, mouseY, delta);
            valueElement.render(graphics, mouseX, mouseY, delta);
        }
    }

    /**
     * A simple key-value pair class that holds a key and a value.
     */
    public static class KeyValuePair<K, V> {
        private K key;
        private V value;

        /**
         * Creates a KeyValuePair with the specified key and value.
         * 
         * @param key The key of the key-value pair.
         * @param value The value of the key-value pair.
         */
        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Gets the key of the key-value pair.
         * 
         * @return The key of the key-value pair.
         */
        public K getKey() {
            return key;
        }

        /**
         * Gets the value of the key-value pair.
         * 
         * @return The value of the key-value pair.
         */
        public V getValue() {
            return value;
        }

        /**
         * Sets the key of the key-value pair.
         * 
         * @param key The new key for the key-value pair.
         */
        public void setKey(K key) {
            this.key = key;
        }

        /**
         * Sets the value of the key-value pair.
         * 
         * @param value The new value for the key-value pair.
         */
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof KeyValuePair<?, ?> other))
                return false;
            return this.key.equals(other.key) && this.value.equals(other.value);
        }

        /**
         * A Gson TypeAdapter for serializing and deserializing KeyValuePair objects.
         */
        public static class KeyValueTypeAdapter implements JsonSerializer<KeyValuePair<?, ?>>,
                JsonDeserializer<KeyValuePair<?, ?>> {
            @Override
            public KeyValuePair<?, ?> deserialize(JsonElement jsonElement, Type type,
                    JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = jsonElement.getAsJsonObject();
                JsonElement key = object.get("key");
                JsonElement value = object.get("value");
                Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();
                return new KeyValuePair<>(context.deserialize(key, typeArgs[0]),
                        context.deserialize(value, typeArgs[1]));
            }

            @Override
            public JsonElement serialize(KeyValuePair<?, ?> pair, Type type,
                    JsonSerializationContext context) {
                JsonObject result = new JsonObject();
                result.add("key", context.serialize(pair.key));
                result.add("value", context.serialize(pair.value));
                return result;
            }
        }
    }
}
