package net.volcaronitee.nar.feature.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.util.TickUtil;
import net.volcaronitee.nar.util.TitleUtil;

/**
 * Feature for displaying a warning when the player's health is below a specified threshold.
 */
public class LowHealthWarning {
    private static final LowHealthWarning INSTANCE = new LowHealthWarning();

    /**
     * Registers the LowHealthWarning feature to be called every tick.
     */
    public static void register() {
        TickUtil.register(INSTANCE::onTick, 1);
    }

    /**
     * Called every tick to check the player's health and display a warning if it is below the
     * 
     * @param client
     */
    private void onTick(MinecraftClient client) {
        int healthThreshold = NarConfig.getHandler().combat.lowHealthWarning;
        if (healthThreshold == 0) {
            return;
        }

        // Get the player and calculate health ratio
        ClientPlayerEntity player = client.player;
        float healthRatio = player.getHealth() / player.getMaxHealth();
        String warningTitle = "§4§lWARNING: HEALTH BELOW §7" + healthThreshold + "%§4!";

        // Display the warning title if health is below the threshold
        if (healthRatio <= healthThreshold / 100.0f) {
            TitleUtil.createTitle(warningTitle, "", 0, 0, 10, 5);
        } else {
            TitleUtil.deleteTitle(warningTitle, "");
        }
    }
}
