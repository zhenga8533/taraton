package net.volcaronitee.taraton.feature.container;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.helper.LineContent;

/**
 * Feature to display the player's equipped armor items in a custom overlay.
 */
public class ArmorDisplay {
    private static final ArmorDisplay INSTANCE = new ArmorDisplay();

    private static final List<LineContent> LINES =
            new ArrayList<>(List.of(LineContent.of(Items.IRON_HELMET.getDefaultStack(), () -> true),
                    LineContent.of(Items.IRON_CHESTPLATE.getDefaultStack(), () -> true),
                    LineContent.of(Items.IRON_LEGGINGS.getDefaultStack(), () -> true),
                    LineContent.of(Items.IRON_BOOTS.getDefaultStack(), () -> true)));
    static {
        OverlayUtil.createOverlay("armor_display",
                () -> FeatureUtil.isEnabled(TaratonConfig.getInstance().container.armorDisplay),
                LINES);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ArmorDisplay() {}

    /**
     * Registers the armor display feature to update every 20 ticks (1 second).
     */
    public static void register() {
        TickUtil.register(INSTANCE::updateArmor, 20);
    }

    /**
     * Gets the ItemStack in the specified equipment slot.
     * 
     * @param slot The equipment slot to check.
     * @param client The Minecraft client instance.
     * @return The ItemStack in the specified slot, or a barrier item if empty or invalid.
     */
    private ItemStack getItemStack(EquipmentSlot slot, MinecraftClient client) {
        if (client.player == null || slot == null) {
            return Items.BARRIER.getDefaultStack();
        }

        // Get the item stack in the specified equipment slot
        ItemStack stack = client.player.getEquippedStack(slot);
        if (stack == null || stack.isEmpty()) {
            return Items.BARRIER.getDefaultStack();
        }

        return stack;
    }

    /**
     * Updates the armor display with the player's equipped armor items.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateArmor(MinecraftClient client) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().container.armorDisplay)
                || client.world == null || client.player == null) {
            return;
        }

        // Get the player's equipped armor items
        ItemStack helmet = getItemStack(EquipmentSlot.HEAD, client);
        ItemStack chestplate = getItemStack(EquipmentSlot.CHEST, client);
        ItemStack leggings = getItemStack(EquipmentSlot.LEGS, client);
        ItemStack boots = getItemStack(EquipmentSlot.FEET, client);

        // Add the equipped armor items to the list
        LINES.clear();
        LINES.add(LineContent.of(helmet, () -> true));
        LINES.add(LineContent.of(chestplate, () -> true));
        LINES.add(LineContent.of(leggings, () -> true));
        LINES.add(LineContent.of(boots, () -> true));
    }
}
