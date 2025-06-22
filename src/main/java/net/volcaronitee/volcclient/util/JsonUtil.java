package net.volcaronitee.volcclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.config.controller.KeyValueController.KeyValuePair;

/**
 * Utility class for handling JSON file operations.
 */
public class JsonUtil {
    private static final Path CONFIG_DIR =
            FabricLoader.getInstance().getConfigDir().resolve(VolcClient.MOD_ID);
    private static final String TEMPLATE_PATH = "/json/";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String DATA_DIR = "data";

    static {
        // Ensure directories exist
        try {
            Files.createDirectories(CONFIG_DIR.resolve(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a JSON file, creating it if it doesn't exist.
     * 
     * @param fileName The name of the JSON file to load.
     * @return The loaded JsonObject, or an empty JsonObject if the file doesn't exist.
     */
    public static JsonObject loadJson(String fileDir, String fileName) {
        Path filePath = CONFIG_DIR.resolve(fileDir).resolve(fileName);

        try {
            if (!Files.exists(filePath)) {
                // If the file does not exist, create it with a template or an empty JSON object
                String templateResourceName = TEMPLATE_PATH + fileName;
                try (InputStream templateStream =
                        JsonUtil.class.getResourceAsStream(templateResourceName)) {
                    if (templateStream != null) {
                        Files.copy(templateStream, filePath);
                    } else {
                        Files.write(filePath, "{}".getBytes(), StandardOpenOption.CREATE);
                    }
                }
            }

            // Read and parse the JSON file
            String content = Files.readString(filePath);
            return JsonParser.parseString(content).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonObject();
        }
    }

    /**
     * Loads a JSON file from the root JSON directory, creating it if it doesn't exist.
     * 
     * @param fileName The name of the JSON file to load.
     * @return The loaded JsonObject, or an empty JsonObject if the file doesn't exist.
     */
    public static JsonObject loadJson(String fileName) {
        return loadJson("", fileName);
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
        try (InputStream templateStream =
                JsonUtil.class.getResourceAsStream(templateResourceName)) {
            if (templateStream == null) {
                throw new IOException(
                        "Bundled template resource not found: " + templateResourceName);
            }

            // Use InputStreamReader to parse JSON from the stream
            return JsonParser.parseReader(new InputStreamReader(templateStream)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load bundled template: " + fileName, e);
        }
    }

    /**
     * Saves a JsonObject to a JSON file.
     * 
     * @param fileDir The directory extension to save the JSON file in.
     * @param fileName The name of the JSON file to save.
     * @param jsonObject The JsonObject to save.
     */
    public static void saveJson(String fileDir, String fileName, JsonObject jsonObject) {
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

    /**
     * Parses key-value pairs from a JsonObject under a specified key.
     * 
     * @param rootObject The root JsonObject containing the key-value pairs.
     * @param mapKey The key under which the key-value pairs are stored in the root object.
     * @return A list of KeyValuePair objects representing the key-value pairs found in the
     *         specified map.
     */
    public static List<KeyValuePair<String, String>> parseKeyValuePairs(JsonObject rootObject,
            String mapKey) {
        List<KeyValuePair<String, String>> result = new ArrayList<>();

        if (rootObject != null && mapKey != null && !mapKey.isEmpty()) {
            if (rootObject.has(mapKey) && rootObject.get(mapKey).isJsonObject()) {
                JsonObject targetMapObject = rootObject.getAsJsonObject(mapKey);

                for (Map.Entry<String, JsonElement> entry : targetMapObject.entrySet()) {
                    String key = entry.getKey();
                    JsonElement valueElement = entry.getValue();

                    if (valueElement.isJsonPrimitive()
                            && valueElement.getAsJsonPrimitive().isString()) {
                        String value = valueElement.getAsString();
                        result.add(new KeyValuePair<>(key, value));
                    }
                }
            }
        }
        return result;
    }
}
