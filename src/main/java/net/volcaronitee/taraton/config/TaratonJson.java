package net.volcaronitee.taraton.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.util.TickUtil;

/**
 * Utility class for handling JSON file operations.
 */
public class TaratonJson {
    private static final Path CONFIG_DIR =
            FabricLoader.getInstance().getConfigDir().resolve(Taraton.MOD_ID);
    private static final String TEMPLATE_PATH = "/json/";

    private static final List<JsonInstance> jsonInstances = new ArrayList<>();

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Initializes the TaratonJson class by loading JSON instances and setting up periodic saving.
     */
    public static void init() {
        TickUtil.register(TaratonJson::saveInstances, 18000);
        ClientLifecycleEvents.CLIENT_STOPPING.register(TaratonJson::saveInstances);
    }

    /**
     * Loads a JSON file from the specified directory and file name.
     * 
     * @param fileDir The directory where the JSON file is located.
     * @param fileName The name of the JSON file to load.
     */
    public static JsonInstance registerJson(String fileDir, String fileName) {
        JsonInstance jsonInstance = new JsonInstance(fileDir, fileName);
        jsonInstances.add(jsonInstance);
        return jsonInstance;
    }

    /**
     * Retrieves a JSON object from the specified directory and file name.
     * 
     * @param fileDir The directory where the JSON file is located.
     * @param fileName The name of the JSON file to retrieve.
     * @return The JsonObject loaded from the specified file.
     */
    public static JsonObject loadJson(String fileDir, String fileName) {
        return new JsonInstance(fileDir, fileName).jsonObject;
    }

    /**
     * Saves a JSON object to the specified directory and file name.
     * 
     * @param fileDir The directory where the JSON file should be saved.
     * @param fileName The name of the JSON file to save.
     * @param jsonObject The JsonObject to save to the file.
     */
    public static void saveJson(String fileDir, String fileName, JsonObject jsonObject) {
        JsonInstance jsonInstance = new JsonInstance(fileDir, fileName);
        jsonInstance.jsonObject = jsonObject;
        jsonInstance.save();
    }

    /**
     * Saves all registered JSON instances to their respective files.
     * 
     * @param client The Minecraft client instance, used for context.
     */
    public static void saveInstances(MinecraftClient client) {
        for (JsonInstance jsonInstance : jsonInstances) {
            jsonInstance.save();
        }
    }

    /**
     * Loads a JSON template file from the templates directory.
     * 
     * @param fileName The name of the template file to load.
     * @return The loaded JsonObject from the template file.
     * @throws RuntimeException If the template file does not exist or cannot be read.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static JsonObject loadTemplate(String fileName) {
        String templateResourceName = TEMPLATE_PATH + fileName;

        // Check if the template resource exists
        try (InputStream templateStream =
                TaratonJson.class.getResourceAsStream(templateResourceName)) {
            if (templateStream == null) {
                return null;
            }

            // Use InputStreamReader to parse JSON from the stream
            return JsonParser.parseReader(new InputStreamReader(templateStream)).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load bundled template: " + fileName, e);
        }
    }

    public static class JsonInstance {
        private JsonObject jsonObject;
        private String fileDir;
        private String fileName;

        private JsonInstance(String fileDir, String fileName) {
            this.fileDir = fileDir;
            this.fileName = fileName;
            load();
        }

        public JsonObject getJsonObject() {
            return jsonObject;
        }

        private void load() {
            Path filePath = CONFIG_DIR.resolve(fileDir).resolve(fileName);

            // Create parent directories if they do not exist
            try {
                Files.createDirectories(filePath.getParent());
            } catch (IOException e) {
                e.printStackTrace();
                jsonObject = new JsonObject();
                return;
            }

            try {
                // If the file does not exist, create it with a template or an empty JSON object
                if (!Files.exists(filePath)) {
                    String templateResourceName = TEMPLATE_PATH + fileDir + "/" + fileName;
                    try (InputStream templateStream =
                            TaratonJson.class.getResourceAsStream(templateResourceName)) {
                        if (templateStream != null) {
                            Files.copy(templateStream, filePath);
                        } else {
                            Files.write(filePath, "{}".getBytes(), StandardOpenOption.CREATE);
                        }
                    }
                }

                // Read and parse the JSON file
                String content = Files.readString(filePath);
                jsonObject = JsonParser.parseString(content).getAsJsonObject();
            } catch (IOException e) {
                e.printStackTrace();
                jsonObject = new JsonObject();
            }
        }

        private void save() {
            Path filePath;

            // Handle root directory case
            if (fileDir == null || fileDir.isEmpty() || fileDir.equals("/")) {
                filePath = CONFIG_DIR.resolve(fileName);
            } else {
                filePath = CONFIG_DIR.resolve(fileDir).resolve(fileName);
            }

            try {
                // Write the JSON object to the file
                Files.write(filePath, GSON.toJson(jsonObject).getBytes(), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
