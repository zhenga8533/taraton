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
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().container.armorDisplay)
                || client.world == null || client.player == null) {
            return;
        }

        // Get the player's equipped armor items
        ItemStack helmet = client.player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = client.player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = client.player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = client.player.getEquippedStack(EquipmentSlot.FEET);

        // Add the equipped armor items to the list
        LINES.clear();
        LINES.add(LineContent.of(helmet, () -> true));
        LINES.add(LineContent.of(chestplate, () -> true));
        LINES.add(LineContent.of(leggings, () -> true));
        LINES.add(LineContent.of(boots, () -> true));
    }
}
