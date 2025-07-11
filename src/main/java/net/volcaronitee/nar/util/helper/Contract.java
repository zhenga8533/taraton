package net.volcaronitee.nar.util.helper;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.NotARat;

/**
 * Utility class for handling the contract file.
 */
public class Contract {
    private final static String CONTRACT_PATH = "/assets/nar/contract.txt";
    private final static Path CONTRACT_FILE =
            FabricLoader.getInstance().getConfigDir().resolve(NotARat.MOD_ID + "/contract.txt");

    private final static int SIGN_INDEX = 45; // Line 46 in the contract file (0-indexed)

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
            String absolutePath = CONTRACT_FILE.toAbsolutePath().toString();
            chatHud.addMessage(NotARat.MOD_TITLE.copy().append(Text.literal(
                    " Desktop is not supported! Please open the contract file manually at: ")
                    .formatted(Formatting.RED))
                    .append(Text.literal(absolutePath)
                            .setStyle(Style.EMPTY.withColor(Formatting.BLUE).withUnderline(true)
                                    .withClickEvent(new ClickEvent.CopyToClipboard(absolutePath))
                                    .withHoverEvent(new ShowText(
                                            Text.literal("Click to copy path to clipboard")
                                                    .formatted(Formatting.YELLOW))))));
            return false;
        }
    }

    /**
     * Checks if the contract is signed.
     * 
     * @return True if the contract is signed, false otherwise.
     */
    public static boolean isSigned() {
        try {
            String clientUsername = MinecraftClient.getInstance().getSession().getUsername();
            return Files.readAllLines(CONTRACT_FILE).get(SIGN_INDEX).contains(clientUsername);
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
            // Check if the contract file is valid by checking the signature line
            try {
                String line = Files.readAllLines(CONTRACT_FILE).get(SIGN_INDEX - 1);
                if (!line.equals("User (Enter IGN below):")) {
                    Files.deleteIfExists(CONTRACT_FILE);
                } else {
                    return; // Contract already exists and is valid
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Copy the contract template to the config directory
        try {
            Files.copy(Contract.class.getResourceAsStream(CONTRACT_PATH), CONTRACT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
