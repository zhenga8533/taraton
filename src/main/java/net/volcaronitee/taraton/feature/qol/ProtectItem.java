package net.volcaronitee.taraton.feature.qol;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.ParseUtil;

/**
 * Feature to protect specific items from being dropped or thrown in the game.
 */
public class ProtectItem {
    private static final ProtectItem INSTANCE = new ProtectItem();

    public static final TaratonList PROTECT_MAP = new TaratonList("Protect Map",
            Text.literal("A list of items to protect from being dropped."), "protect_map.json",
            new String[] {"UUID", "Name"});
    static {
        PROTECT_MAP.setIsMap(true);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ProtectItem() {}

    /**
     * Returns the singleton instance of ProtectItem.
     * 
     * @return The singleton instance of ProtectItem.
     */
    public static ProtectItem getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a command to toggle the protection status of the item currently held in the player's
     * main hand.
     * 
     * @param name The name of the command to create.
     * @return A LiteralArgumentBuilder for the command that toggles item protection.
     */
    public LiteralArgumentBuilder<FabricClientCommandSource> createCommand(String name) {
        return ClientCommandManager.literal(name).executes(context -> protect());
    }

    /**
     * Toggles the protection status of the item currently held in the player's main hand.
     * 
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    public int protect() {
        ItemStack heldStack = MinecraftClient.getInstance().player.getMainHandStack();
        String itemUuid = ParseUtil.getItemUuid(heldStack);
        Text itemName = heldStack.getName();

        if (itemUuid == null) {
            Taraton.sendMessage(
                    Text.literal("No UUID found for the held item.").formatted(Formatting.RED));
        } else if (PROTECT_MAP.map.containsKey(itemUuid)) {
            PROTECT_MAP.removeMap(itemUuid);
            Taraton.sendMessage(Text.literal("Item removed from protect list: ")
                    .formatted(Formatting.RED).append(itemName));
        } else {
            PROTECT_MAP.addMap(itemUuid, itemName.getString(), true);
            Taraton.sendMessage(Text.literal("Item added to protect list: ")
                    .formatted(Formatting.GREEN).append(itemName));
        }

        return 1;
    }

    /**
     * Checks if the item stack should be canceled from being dropped or thrown.
     * 
     * @param stack The ItemStack to check for protection.
     * @return True if the item stack is protected and should not be dropped or thrown, false
     *         otherwise.
     */
    public boolean shouldCancelStack(ItemStack stack) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().qol.protectItem) || stack == null
                || stack.isEmpty()) {
            return false;
        }

        String itemUuid = ParseUtil.getItemUuid(stack);
        Text name = stack.getName();

        // Check if the item UUID is in the protect list
        if (itemUuid != null && PROTECT_MAP.map.containsKey(itemUuid)) {
            Taraton.sendMessage(
                    Text.literal("Protected: ").formatted(Formatting.YELLOW).append(name));
            return true;
        }

        return false;
    }
}
