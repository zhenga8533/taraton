package net.volcaronitee.taraton.feature.container;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.TickUtil;

/**
 * Feature to display the player's equipped armor items in a custom overlay.
 */
public class ArmorDisplay {
    private static final ArmorDisplay INSTANCE = new ArmorDisplay();

    private static final List<LineContent> LINES = new ArrayList<>(
            List.of(new LineContent(Items.IRON_HELMET.getDefaultStack(), () -> true),
                    new LineContent(Items.IRON_CHESTPLATE.getDefaultStack(), () -> true),
                    new LineContent(Items.IRON_LEGGINGS.getDefaultStack(), () -> true),
                    new LineContent(Items.IRON_BOOTS.getDefaultStack(), () -> true)));
    static {
        OverlayUtil.createOverlay("armor_display",
                () -> TaratonConfig.getInstance().container.armorDisplay, LINES);
    }

    /**
     * Registers the armor display feature to update every 20 ticks (1 second).
     */
    public static void register() {
        TickUtil.register(INSTANCE::updateArmor, 20);
    }

    /**
     * Updates the armor display with the player's equipped armor items.
     * 
     * @param client The Minecraft client instance.
     */
    private void updateArmor(MinecraftClient client) {
        // Get the player's equipped armor items
        ItemStack helmet = client.player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = client.player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = client.player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = client.player.getEquippedStack(EquipmentSlot.FEET);

        // Add the equipped armor items to the list
        LINES.get(0).setItemStack(helmet);
        LINES.get(1).setItemStack(chestplate);
        LINES.get(2).setItemStack(leggings);
        LINES.get(3).setItemStack(boots);
    }
}
