package net.volcaronitee.volcclient.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.volcaronitee.volcclient.VolcClient;

/**
 * Utility class for handling JSON file operations.
 */
public class JsonUtil {
    private static final Path CONFIG_DIR =
            FabricLoader.getInstance().getConfigDir().resolve(VolcClient.MOD_ID);
    private static final Path JSON_DIR = CONFIG_DIR.resolve("json");
    private static final Path TEMPLATE_DIR = CONFIG_DIR.resolve("json_templates");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String DATA_DIR = "data";

    static {
        // Ensure directories exist
        try {
            Files.createDirectories(JSON_DIR);
            Files.createDirectories(TEMPLATE_DIR);
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
        Path filePath = JSON_DIR.resolve(fileDir).resolve(fileName);

        try {
            if (!Files.exists(filePath)) {
                // Create from template if it doesn't exist
                Path templatePath = TEMPLATE_DIR.resolve(fileName);
                if (Files.exists(templatePath)) {
                    // Copy template to JSON directory
                    Files.copy(templatePath, filePath);
                } else {
                    // Create an empty JSON file if no template exists
                    Files.write(filePath, "{}".getBytes(), StandardOpenOption.CREATE);
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
     * @throws RuntimeException if the template file does not exist or cannot be read.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static JsonObject loadTemplate(String fileName) {
        Path templatePath = TEMPLATE_DIR.resolve(fileName);

        try {
            if (!Files.exists(templatePath)) {
                throw new IOException("Template file does not exist: " + templatePath);
            }

            String content = Files.readString(templatePath);
            return JsonParser.parseString(content).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load template: " + fileName, e);
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
            filePath = JSON_DIR.resolve(fileName);
        } else {
            filePath = JSON_DIR.resolve(fileDir).resolve(fileName);
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
