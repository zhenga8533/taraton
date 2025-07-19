package net.volcaronitee.taraton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonData;
import net.volcaronitee.taraton.config.TaratonToggle;
import net.volcaronitee.taraton.util.CommandUtil;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.LocationUtil;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.PartyUtil;
import net.volcaronitee.taraton.util.PlayerUtil;
import net.volcaronitee.taraton.util.ScheduleUtil;
import net.volcaronitee.taraton.util.ScoreboardUtil;
import net.volcaronitee.taraton.util.TablistUtil;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.TitleUtil;
import net.volcaronitee.taraton.util.helper.Contract;

public class Taraton implements ClientModInitializer {
    public static final String MOD_ID = "taraton";
    public static final Text MOD_TITLE = Text.literal("[").formatted(Formatting.DARK_GRAY)
            .append(Text.literal("Taraton").formatted(Formatting.GOLD, Formatting.BOLD))
            .append(Text.literal("]").formatted(Formatting.DARK_GRAY));
    public static final ModContainer MOD_CONTAINER =
            net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(MOD_ID)
                    .orElseThrow(() -> new IllegalStateException("Mod container not found"));
    public static final String MOD_VERSION =
            MOD_CONTAINER.getMetadata().getVersion().getFriendlyString();

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        Contract.init();

        LocationUtil.init();
        TaratonConfig.init();
        TaratonData.init();
        TaratonToggle.init();
        OverlayUtil.init();
        PartyUtil.init();
        PlayerUtil.init();
        ScheduleUtil.init();
        ScoreboardUtil.init();
        TablistUtil.init();
        TickUtil.init();
        TitleUtil.init();

        FeatureUtil.init();
        CommandUtil.init();
    }

    /**
     * Sends a message to the in-game chat.
     * 
     * @param message The message to send.
     * @return True if the message was sent successfully, false otherwise.
     */
    public static boolean sendMessage(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.inGameHud == null || client.player == null) {
            return false;
        }

        client.inGameHud.getChatHud()
                .addMessage(MOD_TITLE.copy().append(Text.literal(" ")).append(message));
        return true;
    }

    /**
     * Sends a message to the in-game chat.
     * 
     * @param message The message to send.
     * @return True if the message was sent successfully, false otherwise.
     */
    public static boolean sendMessage(String message) {
        return sendMessage(Text.literal(message));
    }
}
