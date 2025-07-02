package net.volcaronitee.nar.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.volcaronitee.nar.NotARat;

/**
 * Utility class for handling JSON file operations.
 */
public class NarJson {
    private static final Path CONFIG_DIR =
            FabricLoader.getInstance().getConfigDir().resolve(NotARat.MOD_ID);
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

        // Create parent directories if they do not exist
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonObject();
        }

        try {
            // If the file does not exist, create it with a template or an empty JSON object
            if (!Files.exists(filePath)) {
                String templateResourceName = TEMPLATE_PATH + fileDir + "/" + fileName;
                try (InputStream templateStream =
                        NarJson.class.getResourceAsStream(templateResourceName)) {
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

        // Check if the template resource exists
        try (InputStream templateStream = NarJson.class.getResourceAsStream(templateResourceName)) {
            if (templateStream == null) {
                return null;
            }

            // Use InputStreamReader to parse JSON from the stream
            return JsonParser.parseReader(new InputStreamReader(templateStream)).getAsJsonObject();
        } catch (IOException e) {
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
}
