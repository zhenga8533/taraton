package net.volcaronitee.volcclient.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.feature.chat.CustomEmote;
import net.volcaronitee.volcclient.feature.chat.JoinWhitelist;
import net.volcaronitee.volcclient.feature.chat.SpamHider;
import net.volcaronitee.volcclient.feature.chat.TextSubstitution;
import net.volcaronitee.volcclient.feature.general.WidgetDisplay;

/**
 * Utility class for handling client commands.
 */
public class CommandUtil {
    private static final CommandUtil INSTANCE = new CommandUtil();

    private static final String[] ALIASES = {"vc", "volc", "volcclient"};

    /**
     * Initializes the client command registration for Volc Client.
     */
    public static void init() {
        ClientCommandRegistrationCallback.EVENT
                .register((CommandDispatcher<FabricClientCommandSource> dispatcher,
                        CommandRegistryAccess access) -> {
                    for (String alias : ALIASES) {
                        dispatcher.register(literal(alias).executes(INSTANCE::settings)

                                // Help command
                                .then(literal("help").executes(INSTANCE::help))

                                // Settings command
                                .then(literal("settings").executes(INSTANCE::settings))

                                // Toggles command
                                .then(literal("toggles").executes(INSTANCE::toggles))

                                // GUI command
                                .then(literal("gui").executes(OverlayUtil::moveGui))

                                // Debug command
                                .then(literal("debug").executes(INSTANCE::debug))

                                // Lists commands
                                .then(SpamHider.SPAM_LIST.createCommand("spamlist"))
                                .then(SpamHider.SPAM_LIST.createCommand("sl"))
                                .then(JoinWhitelist.WHITE_LIST.createCommand("whitelist"))
                                .then(JoinWhitelist.WHITE_LIST.createCommand("wl"))
                                .then(WidgetDisplay.WIDGET_LIST.createCommand("widgetlist"))
                                .then(WidgetDisplay.WIDGET_LIST.createCommand("wgl"))

                                // Maps commands
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
     * Displays help information for the Volc Client commands.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private int help(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        source.sendFeedback(Text.literal("Volc Client WIP"));
        return 1;
    }

    /**
     * Opens the settings screen for Volc Client.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private int settings(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return 0;
        }

        // Defer the screen opening to the main client thread
        client.send(() -> {
            client.setScreen(ConfigUtil.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Opens the toggles screen for Volc Client.
     *
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private int toggles(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return 0;
        }

        // Defer the screen opening to the main client thread
        client.send(() -> {
            client.setScreen(ToggleUtil.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Displays debug information for the Volc Client.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private int debug(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        String debugMessage = String.format("Volc Client Debug:\n%s\n\n%s\n\n%s",
                PlayerUtil.debugPlayer(), LocationUtil.debugLocation(), PartyUtil.debugParty());
        source.sendFeedback(Text.literal(debugMessage.strip()));
        return 1;
    }
}
