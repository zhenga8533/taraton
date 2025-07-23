package net.volcaronitee.taraton.feature.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.util.OverlayUtil;

/**
 * Feature for adding customizable buttons to container screens.
 */
public class ContainerButtons {
    private static final ContainerButtons INSTANCE = new ContainerButtons();

    private static final Map<String, Button> BUTTONS = new HashMap<>();
    private static final String FILE_DIR = "data/buttons";
    private static boolean isEditing = false;

    /**
     * Registers the container buttons feature, loading existing buttons and setting up the overlay.
     */
    public static void register() {
        loadButtons();

        OverlayUtil.createOverlay("container_buttons", () -> !isEditing, new ArrayList<>())
                .setSpecialRender(ContainerButtons::render);
    }

    /**
     * Gets the singleton instance of the ContainerButtons feature.
     *
     * @return The instance of ContainerButtons.
     */
    public static ContainerButtons getInstance() {
        return INSTANCE;
    }

    /**
     * Creates the command for editing container buttons.
     * 
     * @param name The name of the command.
     * @return The command builder for editing buttons.
     */
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(String name) {
        return ClientCommandManager.literal(name).then(ClientCommandManager.literal("edit")
                .then(ClientCommandManager.literal("inv").executes(context -> enterEditMode(true)))
                .then(ClientCommandManager.literal("chest")
                        .executes(context -> enterEditMode(false))));
    }

    /**
     * Enters edit mode for container buttons, allowing customization of button commands and icons.
     * 
     * @param isInventory True if editing inventory buttons, false for generic container buttons.
     * @return 1 if successful, 0 otherwise.
     */
    private int enterEditMode(boolean isInventory) {
        isEditing = true;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.currentScreen != null) {
            return 0;
        }

        ClientPlayerEntity player = client.player;
        PlayerInventory inventory = player.getInventory();

        client.send(() -> {
            // Create the edit screen based on the type of container
            Screen editScreen = isInventory ? new InventoryScreen(player)
                    : new GenericContainerScreen(ScreenHandlerType.GENERIC_9X6.create(0, inventory),
                            inventory, Text.literal("Button Editing"));

            client.setScreen(new ButtonEditScreen(editScreen));
        });
        return 1;
    }

    /**
     * Renders the container buttons overlay on the screen.
     */
    private static void render(DrawContext context, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof HandledScreen))
            return;

        // Check if the screen is a HandledScreen (like inventory or container)
        HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;
        int x = (screen.width - 176) / 2;
        int y = (screen.height - 166) / 2;

        // Draw the buttons at their respective locations
        for (Button button : BUTTONS.values()) {
            button.draw(context, x, y, screen instanceof InventoryScreen);
        }
    }

    /**
     * Loads the buttons configuration from JSON.
     */
    private static void loadButtons() {
        JsonObject buttonsJson = TaratonJson.loadJson(FILE_DIR, "buttons.json");
        if (buttonsJson == null)
            return;

        // Iterate through the JSON object and create Button instances
        for (Map.Entry<String, JsonElement> entry : buttonsJson.entrySet()) {
            JsonObject buttonData = entry.getValue().getAsJsonObject();
            Button button =
                    new Button(ButtonLocation.valueOf(buttonData.get("location").getAsString()),
                            buttonData.get("index").getAsInt(),
                            buttonData.get("command").getAsString(), new ItemStack(Registries.ITEM
                                    .get(Identifier.of(buttonData.get("icon").getAsString()))));
            BUTTONS.put(entry.getKey(), button);
        }
    }

    /**
     * Saves the current buttons configuration to JSON.
     */
    private static void saveButtons() {
        JsonObject buttonsJson = new JsonObject();
        for (Map.Entry<String, Button> entry : BUTTONS.entrySet()) {
            buttonsJson.add(entry.getKey(), entry.getValue().toJson());
        }
        TaratonJson.saveJson(FILE_DIR, "buttons.json", buttonsJson);
    }

    /**
     * Enum representing the possible locations for buttons on the screen.
     */
    private enum ButtonLocation {
        TOP(8, -18), RIGHT(178, 12), BOTTOM(8, 166), LEFT(-18, 12), INV(80, 8);

        public final int x, y;

        /**
         * Creates a new ButtonLocation with the specified x and y coordinates.
         * 
         * @param x The x-coordinate of the button location.
         * @param y The y-coordinate of the button location.
         */
        ButtonLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Represents a customizable button in the container screen.
     */
    private static class Button {
        private final ButtonLocation location;
        private final int index;
        private String command;
        private ItemStack icon;

        /**
         * Creates a new button with the specified parameters.
         * 
         * @param location The location of the button on the screen.
         * @param index The index of the button in its location (0-8 for inventory, etc.).
         * @param command The command to execute when the button is clicked.
         * @param icon The icon to display for the button.
         */
        public Button(ButtonLocation location, int index, String command, ItemStack icon) {
            this.location = location;
            this.index = index;
            this.command = command;
            this.icon = icon;
        }

        /**
         * Draws the button on the screen at the specified location.
         * 
         * @param context The draw context for rendering.
         * @param screenX The x-coordinate of the screen.
         * @param screenY The y-coordinate of the screen.
         * @param isInventory True if the current screen is the inventory, false otherwise.
         */
        public void draw(DrawContext context, int screenX, int screenY, boolean isInventory) {
            if (location == ButtonLocation.INV && !isInventory)
                return;

            int x = screenX + location.x + (index % 9) * 18;
            int y = screenY + location.y + (index / 9) * 18;

            context.drawItem(icon, x, y);
        }

        /**
         * Converts the button to a JSON object for saving.
         * 
         * @return A JSON representation of the button.
         */
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("location", location.name());
            obj.addProperty("index", index);
            obj.addProperty("command", command);
            obj.addProperty("icon", Registries.ITEM.getId(icon.getItem()).toString());
            return obj;
        }
    }

    /**
     * Screen for editing buttons in the container screen.
     */
    private static class ButtonEditScreen extends Screen {
        private final Screen backgroundScreen;
        private TextFieldWidget commandField;
        private TextFieldWidget iconField;
        private Button selectedButton;

        /**
         * Creates a new ButtonEditScreen with the specified background screen.
         * 
         * @param backgroundScreen The screen to display in the background while editing buttons.
         */
        protected ButtonEditScreen(Screen backgroundScreen) {
            super(Text.literal("Button Editor"));
            this.backgroundScreen = backgroundScreen;
        }

        @Override
        protected void init() {
            this.backgroundScreen.init(client, width, height);

            commandField = new TextFieldWidget(textRenderer, width / 2 - 100, 40, 200, 20,
                    Text.literal("Command"));
            iconField = new TextFieldWidget(textRenderer, width / 2 - 100, 80, 200, 20,
                    Text.literal("Icon (e.g. minecraft:diamond)"));
            addDrawableChild(commandField);
            addDrawableChild(iconField);

            addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), button -> {
                if (selectedButton != null) {
                    selectedButton.command = commandField.getText();
                    Identifier iconId = Identifier.tryParse(iconField.getText());
                    if (iconId != null && Registries.ITEM.containsId(iconId)) {
                        selectedButton.icon = new ItemStack(Registries.ITEM.get(iconId));
                    }
                }
                saveButtons();
                close();
            }).dimensions(width / 2 - 100, 120, 200, 20).build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            backgroundScreen.render(context, -1, -1, delta); // Render background
            context.fill(0, 0, this.width, this.height, 0x80000000); // Darken background
            super.render(context, mouseX, mouseY, delta); // Render widgets
        }

        @Override
        public void close() {
            isEditing = false;
            this.client.setScreen(null);
        }
    }
}
