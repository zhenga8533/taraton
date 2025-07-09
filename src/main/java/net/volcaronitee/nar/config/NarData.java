package net.volcaronitee.nar.config;

import java.nio.file.Path;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.util.TickUtil;

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
        TickUtil.register(NarData::saveData, 18000);
        ClientLifecycleEvents.CLIENT_STOPPING.register(NarData::saveData);
    }

    /**
     * Returns the NAR data as a JsonObject.
     * 
     * @return The NAR data as a JsonObject.
     */
    public static JsonObject getData() {
        return data;
    }

    private static void saveData(MinecraftClient client) {
        NarJson.saveJson("", FILE_NAME, data);
    }

    /**
     * Loads the NAR data from the JSON file.
     */
    private static void loadData() {
        JsonObject templateData = NarJson.loadTemplate(FILE_NAME);

        if (DATA_FILE.toFile().exists()) {
            // Load existing data from the file
            data = NarJson.loadJson("", FILE_NAME);
            if (data == null) {
                data = templateData;
            } else {
                // Ensure all keys from the template are present in the data
                for (String key : templateData.keySet()) {
                    if (!data.has(key)) {
                        data.add(key, templateData.get(key));
                    }
                }
            }
        } else {
            // Use template if data file does not exist
            data = templateData;
        }
    }
}
