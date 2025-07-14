package net.volcaronitee.taraton.config;

import java.nio.file.Path;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.util.TickUtil;

/**
 * Handles the loading and saving of Taraton data from a JSON file.
 */
public class TaratonData {
    private final static String FILE_NAME = "data.json";
    private final static Path DATA_FILE =
            FabricLoader.getInstance().getConfigDir().resolve(Taraton.MOD_ID + "/" + FILE_NAME);
    private static JsonObject data;

    /**
     * Initializes the TaratonData class by loading the data from the JSON file
     */
    public static void init() {
        loadData();
        TickUtil.register(TaratonData::saveData, 18000);
        ClientLifecycleEvents.CLIENT_STOPPING.register(TaratonData::saveData);
    }

    /**
     * Returns the Taraton data as a JsonObject.
     * 
     * @return The Taraton data as a JsonObject.
     */
    public static JsonObject getData() {
        return data;
    }

    private static void saveData(MinecraftClient client) {
        TaratonJson.saveJson("", FILE_NAME, data);
    }

    /**
     * Loads the Taraton data from the JSON file.
     */
    private static void loadData() {
        JsonObject templateData = TaratonJson.loadTemplate(FILE_NAME);

        if (DATA_FILE.toFile().exists()) {
            // Load existing data from the file
            data = TaratonJson.loadJson("", FILE_NAME);
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
