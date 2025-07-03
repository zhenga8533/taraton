package net.volcaronitee.nar.util.helper;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarJson;

/**
 * Utility class for handling the contract file.
 */
public class Contract {
    private final static String CONTRACT_PATH = "/assets/nar/contract.txt";
    private final static Path CONTRACT_FILE =
            FabricLoader.getInstance().getConfigDir().resolve(NotARat.MOD_ID + "/contract.txt");

    /**
     * Initializes the contract by creating the contract file if it does not exist.
     */
    public static void init() {
        createContract();
    }

    /**
     * Opens the contract file in the default text editor.
     * 
     * @return True if the contract file was opened successfully, false otherwise.
     */
    public static boolean openContract() {
        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();

        // Check if the desktop is supported for opening files
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                if (CONTRACT_FILE.toFile().exists()) {
                    desktop.open(CONTRACT_FILE.toFile());
                } else {
                    createContract();
                    desktop.open(CONTRACT_FILE.toFile());
                }
                return true;
            } catch (Exception e) {
                chatHud.addMessage(NotARat.MOD_TITLE.copy().append(
                        Text.literal(" Failed to open contract file!").formatted(Formatting.RED)));
                return false;
            }
        } else {
            chatHud.addMessage(NotARat.MOD_TITLE.copy()
                    .append(Text.literal(" Desktop is not supported!").formatted(Formatting.RED)));
            return false;
        }
    }

    /**
     * Checks if the contract is signed.
     * 
     * @return True if the contract is signed, false otherwise.
     */
    public static boolean isSigned() {
        // Check if line 43 is signed
        try {
            String clientUsername = MinecraftClient.getInstance().getSession().getUsername();
            return Files.readAllLines(CONTRACT_FILE).get(42).contains(clientUsername);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates the contract file if it does not exist.
     */
    private static void createContract() {
        // Check if the contract file already exists
        if (CONTRACT_FILE.toFile().exists()) {
            return;
        }

        // Copy the contract template to the config directory
        try {
            Files.copy(Path.of(NarJson.class.getResource(CONTRACT_PATH).toURI()), CONTRACT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
