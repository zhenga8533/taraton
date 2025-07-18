package net.volcaronitee.taraton.feature.qol;

import org.jetbrains.annotations.Nullable;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;

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
     * Toggles the protection status of the item currently held in the player's main hand.
     * 
     * @param context The command context containing the command source.
     */
    public int protect(CommandContext<FabricClientCommandSource> context) {
        ItemStack heldStack = MinecraftClient.getInstance().player.getMainHandStack();
        String itemUuid = getItemUuid(heldStack);
        Text itemName = heldStack.getName();

        if (itemUuid == null) {
            Taraton.sendMessage(
                    Text.literal("No UUID found for the held item.").formatted(Formatting.RED));
        } else if (PROTECT_MAP.map.containsKey(itemUuid)) {
            PROTECT_MAP.removeMap(itemUuid);
            Taraton.sendMessage(Text.literal("Item removed from protect list: ")
                    .formatted(Formatting.YELLOW).append(itemName));
        } else {
            PROTECT_MAP.addMap(itemUuid, itemName.getString(), true);
            Taraton.sendMessage(Text.literal("Item added to protect list: ")
                    .formatted(Formatting.GREEN).append(itemName));
        }

        return 1;
    }

    /**
     * Retrieves the UUID of the item from its NBT data.
     * 
     * @param stack The ItemStack from which to retrieve the UUID.
     * @return The UUID of the item as a String, or null if not found.
     */
    @Nullable
    private String getItemUuid(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }

        NbtCompound nbt = customData.copyNbt();
        return nbt.getString("uuid").orElse(null);
    }

    /**
     * Checks if the item stack should be canceled from being dropped or thrown.
     * 
     * @param stack The ItemStack to check for protection.
     * @return True if the item stack is protected and should not be dropped or thrown, false
     *         otherwise.
     */
    public boolean shouldCancelStack(ItemStack stack) {
        if (!TaratonConfig.getInstance().qol.protectItem || stack == null || stack.isEmpty()) {
            return false;
        }

        String itemUuid = getItemUuid(stack);
        Text name = stack.getName();

        // Check if the item UUID is in the protect list
        if (itemUuid != null && PROTECT_MAP.map.containsKey(itemUuid)) {
            Taraton.sendMessage(
                    Text.literal("Prevented dropping: ").formatted(Formatting.YELLOW).append(name));
            return true;
        }

        return false;
    }
}
