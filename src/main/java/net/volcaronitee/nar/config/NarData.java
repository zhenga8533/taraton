package net.volcaronitee.nar.config;

import java.nio.file.Path;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.nar.NotARat;

/**
 * Handles the loading and saving of NAR data from a JSON file.
 */
public class NarData {
    private final static String FILE_NAME = "data.json";
    private final static Path DATA_FILE =
            FabricLoader.getInstance().getConfigDir().resolve(NotARat.MOD_ID + "/" + FILE_NAME);
    private static JsonObject data;

    /**
     * Initializes the NarData class by loading the data from the JSON file
     */
    public static void init() {
        loadData();
        ClientLifecycleEvents.CLIENT_STOPPING.register(NarData::onClientClose);
    }

    /**
     * Returns the NAR data as a JsonObject.
     * 
     * @return The NAR data as a JsonObject.
     */
    public static JsonObject getData() {
        return data;
    }

    /**
     * Loads the NAR data from the JSON file.
     */
    private static void loadData() {
        // Load the data from the JSON file if it exists
        if (DATA_FILE.toFile().exists()) {
            data = NarJson.loadJson("", FILE_NAME);
        } else {
            data = NarJson.loadTemplate(FILE_NAME);
        }
    }

    /**
     * Handles the world unload event to save playtime data.
     * 
     * @param event The event triggered when the client is stopping.
     */
    private static void onClientClose(MinecraftClient client) {
        NarJson.saveJson("", FILE_NAME, data);
    }
}
