package net.volcaronitee.taraton.feature.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.mixin.accessor.HandledScreenAccessor;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.OverlayUtil.Overlay;
import net.volcaronitee.taraton.util.ScreenUtil;

/**
 * Feature to preview and save Ender Chest and Backpack data.
 */
public class ContainerPreview {
    private static final ContainerPreview INSTANCE = new ContainerPreview();
    private static final String FILE_DIR = "data/container";
    private static final int CONTAINER_SIZE = 54;

    private static final Identifier CONTAINER_TEXTURE =
            Identifier.of(Taraton.MOD_ID, "texture/gui/container.png");
    private static final int TEXTURE_WIDTH = 176;
    private static final int TEXTURE_HEIGHT = 132;
    private static final int TEXTURE_DIMENSION = 256;

    private static final List<LineContent> LINES =
            new ArrayList<>(List.of(new LineContent("Container Preview Placeholder", () -> true)));
    private static final Overlay OVERLAY = OverlayUtil.createOverlay("container_preview",
            () -> FeatureUtil.isEnabled(TaratonConfig.getInstance().container.containerPreview),
            LINES);
    static {
        OVERLAY.setOnContainer(true);
        OVERLAY.setSpecialRender(INSTANCE::render);
        OVERLAY.setFixedSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private final Map<String, JsonObject> containerJson = new HashMap<>();
    private final Map<String, List<ItemStack>> containerData = new HashMap<>();
    private final List<Integer> containerMatches = new ArrayList<>();

    private final List<ItemStack> previewItems = new ArrayList<>();
    private final List<Integer> previewMatches = new ArrayList<>();
    private String currentPreview = "";

    private static final Pattern ENDER_CHEST_PATTERN =
            Pattern.compile("^Ender Chest \\((\\d+)/9\\)$");
    private static final Pattern BACKPACK_PATTERN =
            Pattern.compile("^.*Backpack.*\\(Slot #(\\d+)\\)$");

    private static final int ENDER_CHEST_INDEX = 9;
    private static final int BACKPACK_INDEX = 27;

    /**
     * Private constructor to prevent instantiation.
     */
    private ContainerPreview() {}

    /**
     * Registers the ContainerPreview feature to handle screen events and
     */
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenEvents.remove(screen).register(INSTANCE::onScreenClose);
        });
        ItemTooltipCallback.EVENT.register(INSTANCE::onItemTooltip);
        Searchbar.addSearchbarListener(INSTANCE::searchContainers);
        INSTANCE.registerContainer("ender_chest", 9);
        INSTANCE.registerContainer("backpack", 18);
        INSTANCE.deserializeContainers();
    }

    /**
     * Searches for containers based on the search text in the Searchbar.
     */
    private void searchContainers() {
        containerMatches.clear();
        previewMatches.clear();

        String searchText = Searchbar.getText();
        if (searchText.isEmpty()) {
            return;
        }

        // Search through container data
        for (Map.Entry<String, List<ItemStack>> entry : containerData.entrySet()) {
            String title = entry.getKey();
            String[] parts = title.split("_");
            List<ItemStack> items = entry.getValue();

            // Check if any item matches the search text
            boolean match = items.stream()
                    .anyMatch(stack -> Searchbar.getInstance().itemMatchesSearch(stack));
            if (!match) {
                continue;
            }

            // Parse the title to determine the index
            int index = 0;
            if (title.startsWith("ender_chest_")) {
                index = ENDER_CHEST_INDEX;
            } else if (title.startsWith("backpack_")) {
                index = BACKPACK_INDEX;
            }

            int slot = Integer.parseInt(parts[parts.length - 1]);
            index += slot - 1;
            containerMatches.add(index);
        }

        // Search through preview items
        for (int i = 0; i < previewItems.size(); i++) {
            ItemStack itemStack = previewItems.get(i);
            if (Searchbar.getInstance().itemMatchesSearch(itemStack)) {
                previewMatches.add(i);
            }
        }
    }

    /**
     * Handles the item tooltip event to show container previews.
     * 
     * @param stack The item stack.
     * @param context The tooltip context.
     * @param type The tooltip type.
     * @param lines The list of tooltip lines.
     */
    private void onItemTooltip(ItemStack stack, TooltipContext context, TooltipType type,
            List<Text> lines) {
        String name = stack.getName().getString();
        String key = "";

        // Check if the item is a valid container
        if (name.startsWith("Ender Chest") || name.contains("Backpack")) {
            List<String> parts = List.of(name.toLowerCase().split(" "));
            if (parts.size() < 3) {
                return;
            }

            // Construct the key based on the container type
            key = String.join("_", parts.subList(0, parts.size() - 2)) + "_"
                    + parts.get(parts.size() - 1);
        }

        // Check if the key is valid and new
        if (!containerJson.containsKey(key) || name.equals(currentPreview)) {
            return;
        }
        currentPreview = name;

        // Overwrite the overlay lines with the container preview
        previewItems.clear();
        previewItems.addAll(containerData.get(key));
        searchContainers();
    }

    /**
     * Renders the container preview overlay.
     * 
     * @param context The draw context.
     * @param delta The time delta since the last render.
     */
    public void render(DrawContext context, float delta) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen == null
                || !(screen instanceof HandledScreen<?> handledScreen
                        && screen instanceof GenericContainerScreen genericContainerScreen)
                || previewItems.isEmpty()) {
            return;
        }

        // Get the screen handler and its position
        HandledScreenAccessor accessor = (HandledScreenAccessor) handledScreen;
        GenericContainerScreenHandler handler = genericContainerScreen.getScreenHandler();
        int parentX = accessor.getX();
        int parentY = accessor.getY();

        // Calculate the overlay position based on the screen handler
        int originalX = OVERLAY.getX();
        int originalY = OVERLAY.getY();

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 200);

        // Draw the container background texture
        context.drawTexture(RenderLayer::getGuiTextured, CONTAINER_TEXTURE, originalX, originalY, 0,
                0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_DIMENSION, TEXTURE_DIMENSION);

        // Draw the container title
        context.drawText(MinecraftClient.getInstance().textRenderer, currentPreview, originalX + 8,
                originalY + 6, ScreenUtil.TEXT_COLOR, false);

        // Draw the highlighted slots
        containerMatches.forEach(i -> {
            ScreenUtil.highlightSlot(context, originalX, originalY, handler.getSlot(i),
                    ScreenUtil.HIGHLIGHT_COLOR);
        });
        previewMatches.forEach(i -> {
            ScreenUtil.highlightSlot(context, parentX, parentY, handler.getSlot(i),
                    ScreenUtil.HIGHLIGHT_COLOR);
        });

        // Draw the container items in a grid layout
        for (int i = 0; i < previewItems.size(); i++) {
            ItemStack itemStack = previewItems.get(i);
            if (!itemStack.isEmpty()) {
                int slotX = originalX + 8 + (i % 9) * 18;
                int slotY = originalY + 18 + (i / 9) * 18;

                context.drawItem(itemStack, slotX, slotY);
                context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, itemStack,
                        slotX, slotY);
            }
        }

        context.getMatrices().pop();
    }

    /**
     * Registers a container with the specified name and quantity.
     * 
     * @param name The name of the container (e.g., "Ender Chest", "Backpack").
     * @param quantity The number of containers to register (e.g., 9 for Ender Chest, 18 for
     *        Backpack).
     */
    private void registerContainer(String name, int quantity) {
        for (int i = 1; i <= quantity; i++) {
            String key = String.format("%s_%d", name, i);
            String fileName = key + ".json";
            JsonObject data = TaratonJson.registerJson(FILE_DIR, fileName).getJsonObject();
            containerJson.put(key, data);
        }
    }

    /**
     * Deserializes the container data from the JSON files.
     */
    private void deserializeContainers() {
        for (Map.Entry<String, JsonObject> entry : containerJson.entrySet()) {
            String title = entry.getKey();
            JsonObject jsonObject = entry.getValue();

            // Initialize the container data with empty item stacks
            DefaultedList<ItemStack> items = DefaultedList.ofSize(CONTAINER_SIZE, ItemStack.EMPTY);

            if (jsonObject.has("items")) {
                JsonObject itemsObject = jsonObject.getAsJsonObject("items");

                // Iterate through the items in the JSON object
                for (Map.Entry<String, JsonElement> itemEntry : itemsObject.entrySet()) {
                    try {
                        int slot = Integer.parseInt(itemEntry.getKey());
                        if (slot < 0 || slot >= CONTAINER_SIZE)
                            continue;

                        // Deserialize the item data
                        JsonObject itemJson = itemEntry.getValue().getAsJsonObject();
                        Identifier itemId = Identifier.of(itemJson.get("id").getAsString());
                        Item item = Registries.ITEM.get(itemId);
                        int count = itemJson.get("count").getAsInt();
                        ItemStack stack = new ItemStack(item, count);

                        // Apply components if present
                        if (itemJson.has("components")) {
                            DataResult<ComponentChanges> result = ComponentChanges.CODEC
                                    .parse(JsonOps.INSTANCE, itemJson.get("components"));
                            result.result().ifPresent(stack::applyChanges);
                        }
                        items.set(slot, stack);
                    } catch (Exception e) {
                        System.err.println("Failed to deserialize item for container: " + title);
                        e.printStackTrace();
                    }
                }
            }
            containerData.put(title, items);
        }
    }

    /**
     * Handles the screen close event to save the container data when a screen is closed.
     * 
     * @param screen The screen that was closed.
     */
    private void onScreenClose(Screen screen) {
        // Clear previous matches and preview data
        containerMatches.clear();
        previewMatches.clear();
        previewItems.clear();
        currentPreview = "";

        // Match EC or Backpack screen title
        String title = screen.getTitle().getString();
        String key = "";
        Matcher matcher = ENDER_CHEST_PATTERN.matcher(title);
        if (matcher.matches()) {
            key = "ender_chest_" + matcher.group(1);
        } else {
            matcher = BACKPACK_PATTERN.matcher(title);
            if (matcher.matches()) {
                key = "backpack_" + matcher.group(1);
            } else {
                return;
            }
        }

        // Initialize the container screen and handlers
        if (!(screen instanceof GenericContainerScreen containerScreen)
                || !containerJson.containsKey(key)) {
            return;
        }

        GenericContainerScreenHandler handler = containerScreen.getScreenHandler();
        JsonObject itemsObject = new JsonObject();
        List<ItemStack> containerItems = containerData.get(key);

        // Iterate through container slots to save item data
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            ItemStack itemStack = handler.getSlot(i).getStack();
            containerItems.set(i, itemStack);

            if (!itemStack.isEmpty()) {
                JsonObject itemJson = new JsonObject();
                itemJson.addProperty("id", Registries.ITEM.getId(itemStack.getItem()).toString());
                itemJson.addProperty("count", itemStack.getCount());

                // Add components if present
                ComponentChanges changes = itemStack.getComponentChanges();
                if (!changes.isEmpty()) {
                    DataResult<JsonElement> result =
                            ComponentChanges.CODEC.encodeStart(JsonOps.INSTANCE, changes);
                    result.result().ifPresent(json -> itemJson.add("components", json));
                }
                itemsObject.add(String.valueOf(i), itemJson);
            }
        }

        // Save the items to the JSON file
        JsonObject containerJsonObject = containerJson.get(key);
        containerJsonObject.add("items", itemsObject);
        TaratonJson.saveJson(FILE_DIR, key + ".json", containerJsonObject);
    }
}
