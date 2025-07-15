package net.volcaronitee.taraton.feature.container;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.volcaronitee.taraton.config.TaratonJson;

/**
 * Feature to preview and save Ender Chest and Backpack data.
 */
public class ContainerPreview {
    private static final ContainerPreview INSTANCE = new ContainerPreview();
    private static final String FILE_DIR = "data/container";
    private static final int CONTAINER_SIZE = 54;

    private final Map<String, JsonObject> containerJson = new HashMap<>();
    private final Map<String, DefaultedList<ItemStack>> containerData = new HashMap<>();

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
        INSTANCE.registerContainer("Ender Chest", 9);
        INSTANCE.registerContainer("Backpack", 18);
        INSTANCE.deserializeContainers();
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
            String key = String.format("%s %d", name, i);
            String fileName = key.toLowerCase().replace(" ", "_") + ".json";
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

            DefaultedList<ItemStack> items = DefaultedList.ofSize(CONTAINER_SIZE, ItemStack.EMPTY);
            if (jsonObject.has("items")) {
                JsonObject itemsObject = jsonObject.getAsJsonObject("items");
                for (Map.Entry<String, JsonElement> itemEntry : itemsObject.entrySet()) {
                    try {
                        int slot = Integer.parseInt(itemEntry.getKey());
                        if (slot < 0 || slot >= CONTAINER_SIZE)
                            continue;

                        JsonObject itemJson = itemEntry.getValue().getAsJsonObject();
                        Identifier itemId = Identifier.of(itemJson.get("id").getAsString());
                        Item item = Registries.ITEM.get(itemId);
                        int count = itemJson.get("count").getAsInt();
                        ItemStack stack = new ItemStack(item, count);

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
        String title = screen.getTitle().getString();
        if (!(screen instanceof GenericContainerScreen) || !containerJson.containsKey(title)) {
            return;
        }

        GenericContainerScreen containerScreen = (GenericContainerScreen) screen;
        GenericContainerScreenHandler handler = containerScreen.getScreenHandler();
        JsonObject itemsObject = new JsonObject();

        for (int i = 0; i < CONTAINER_SIZE; i++) {
            ItemStack itemStack = handler.getSlot(i).getStack();
            if (!itemStack.isEmpty()) {
                JsonObject itemJson = new JsonObject();
                itemJson.addProperty("id", Registries.ITEM.getId(itemStack.getItem()).toString());
                itemJson.addProperty("count", itemStack.getCount());

                ComponentChanges changes = itemStack.getComponentChanges();
                if (!changes.isEmpty()) {
                    DataResult<JsonElement> result =
                            ComponentChanges.CODEC.encodeStart(JsonOps.INSTANCE, changes);
                    result.result().ifPresent(json -> itemJson.add("components", json));
                }
                itemsObject.add(String.valueOf(i), itemJson);
            }
        }

        JsonObject containerJsonObject = containerJson.get(title);
        containerJsonObject.add("items", itemsObject);

        String fileName = title.toLowerCase().replace(" ", "_") + ".json";
        TaratonJson.saveJson(FILE_DIR, fileName, containerJsonObject);
    }
}
