package net.volcaronitee.nar.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarToggle;
import net.volcaronitee.nar.feature.chat.ChatAlert;
import net.volcaronitee.nar.feature.chat.CustomEmote;
import net.volcaronitee.nar.feature.chat.JoinWhitelist;
import net.volcaronitee.nar.feature.chat.SpamHider;
import net.volcaronitee.nar.feature.chat.TextSubstitution;
import net.volcaronitee.nar.feature.combat.EntityHighlight;
import net.volcaronitee.nar.feature.general.WidgetDisplay;

/**
 * Utility class for handling client commands.
 */
public class CommandUtil {
    private static final String[] ALIASES = {"nar", "notarat"};

    /**
     * Initializes the client command registration for NAR.
     */
    public static void init() {
        ClientCommandRegistrationCallback.EVENT
                .register((CommandDispatcher<FabricClientCommandSource> dispatcher,
                        CommandRegistryAccess access) -> {
                    for (String alias : ALIASES) {
                        dispatcher.register(literal(alias).executes(CommandUtil::settings)

                                // Help command
                                .then(literal("help").executes(CommandUtil::help))

                                // Settings command
                                .then(literal("settings").executes(CommandUtil::settings))

                                // Toggles command
                                .then(literal("toggles").executes(CommandUtil::toggles))

                                // GUI command
                                .then(literal("gui").executes(OverlayUtil::moveGui))

                                // Debug command
                                .then(literal("debug").executes(CommandUtil::debug))

                                // Lists commands
                                .then(EntityHighlight.ENTITY_LIST.createCommand("entitylist"))
                                .then(EntityHighlight.ENTITY_LIST.createCommand("el"))
                                .then(SpamHider.SPAM_LIST.createCommand("spamlist"))
                                .then(SpamHider.SPAM_LIST.createCommand("sl"))
                                .then(JoinWhitelist.WHITE_LIST.createCommand("whitelist"))
                                .then(JoinWhitelist.WHITE_LIST.createCommand("wl"))
                                .then(WidgetDisplay.WIDGET_LIST.createCommand("widgetlist"))
                                .then(WidgetDisplay.WIDGET_LIST.createCommand("wgl"))

                                // Maps commands
                                .then(ChatAlert.CHAT_ALERT_MAP.createCommand("chatalertmap"))
                                .then(ChatAlert.CHAT_ALERT_MAP.createCommand("cam"))
                                .then(CustomEmote.EMOTE_MAP.createCommand("emotemap"))
                                .then(CustomEmote.EMOTE_MAP.createCommand("em"))
                                .then(TextSubstitution.SUBSTITUTION_MAP.createCommand("submap"))
                                .then(TextSubstitution.SUBSTITUTION_MAP.createCommand("sm"))

                        // Command End
                        );
                    }
                });
    }

    /**
     * Displays help information for the NAR commands.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int help(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        source.sendFeedback(Text.literal("NAR WIP"));
        return 1;
    }

    /**
     * Opens the settings screen for NAR.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int settings(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return 0;
        }

        // Defer the screen opening to the main client thread
        client.send(() -> {
            client.setScreen(NarConfig.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Opens the toggles screen for NAR.
     *
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int toggles(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return 0;
        }

        // Defer the screen opening to the main client thread
        client.send(() -> {
            client.setScreen(NarToggle.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Displays debug information for the NAR.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int debug(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        String debugMessage = String.format("NAR Debug:\n%s\n\n%s\n\n%s", PlayerUtil.debugPlayer(),
                LocationUtil.debugLocation(), PartyUtil.debugParty());
        source.sendFeedback(Text.literal(debugMessage.strip()));
        return 1;
    }
}
