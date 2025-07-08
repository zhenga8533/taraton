package net.volcaronitee.nar.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;
import net.volcaronitee.nar.config.NarData;
import net.volcaronitee.nar.config.NarToggle;
import net.volcaronitee.nar.feature.chat.ChatAlert;
import net.volcaronitee.nar.feature.chat.ChatCommands;
import net.volcaronitee.nar.feature.chat.CustomEmote;
import net.volcaronitee.nar.feature.chat.JoinWhitelist;
import net.volcaronitee.nar.feature.chat.SpamHider;
import net.volcaronitee.nar.feature.chat.TextSubstitution;
import net.volcaronitee.nar.feature.combat.EntityHighlight;
import net.volcaronitee.nar.feature.general.ImagePreview;
import net.volcaronitee.nar.feature.general.WidgetDisplay;
import net.volcaronitee.nar.util.helper.Contract;

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

                                // Proxy command
                                .then(literal("echo").executes(CommandUtil::echo)
                                        .then(argument("message", StringArgumentType.greedyString())
                                                .executes(context -> CommandUtil.echoProxy(context,
                                                        StringArgumentType.getString(context,
                                                                "message")))))

                                // Debug command
                                .then(literal("debug").executes(CommandUtil::debug))

                                // Contract command
                                .then(literal("contract").executes(CommandUtil::contract))
                                .then(literal("bindingvow").executes(CommandUtil::contract))
                                .then(literal("domainexpansion")
                                        .executes(CommandUtil::domainExpansion))
                                .then(literal("ryoikitenkai")
                                        .executes(CommandUtil::domainExpansion))

                                // Lists commands
                                .then(ChatCommands.BLACK_LIST.createCommand("blacklist"))
                                .then(ChatCommands.BLACK_LIST.createCommand("bl"))
                                .then(EntityHighlight.ENTITY_LIST.createCommand("entitylist"))
                                .then(EntityHighlight.ENTITY_LIST.createCommand("el"))
                                .then(ChatCommands.PREFIX_LIST.createCommand("prefixlist"))
                                .then(ChatCommands.PREFIX_LIST.createCommand("pl"))
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

                                // Chat/client commands
                                .then(argument("default", StringArgumentType.greedyString())
                                        .executes(CommandUtil::defaultCommand))

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
        context.getSource().sendFeedback(Text.literal("NAR WIP"));
        return 1;
    }

    /**
     * Opens the settings screen for NAR.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int settings(CommandContext<FabricClientCommandSource> context) {
        // Defer the screen opening to the main client thread
        MinecraftClient client = MinecraftClient.getInstance();
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
        // Defer the screen opening to the main client thread
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> {
            client.setScreen(NarToggle.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Echoes a message back to the player. This is a placeholder command that can be used for
     * testing.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int echo(CommandContext<FabricClientCommandSource> context) {
        return 1;
    }

    /**
     * Echoes a message back to the player. This method is used when the player provides a
     * 
     * @param context The command context containing the source and arguments.
     * @param message The message to echo back to the player.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int echoProxy(CommandContext<FabricClientCommandSource> context,
            String message) {
        context.getSource()
                .sendFeedback(NotARat.MOD_TITLE.copy().append(Text.literal(" " + message)));
        return 1;
    }

    /**
     * Displays debug information for the NAR.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int debug(CommandContext<FabricClientCommandSource> context) {
        String debugMessage = String.format("NAR Debug:\n%s\n\n%s\n\n%s", PlayerUtil.debugPlayer(),
                LocationUtil.debugLocation(), PartyUtil.debugParty());
        context.getSource().sendFeedback(Text.literal(debugMessage.strip()));
        return 1;
    }

    /**
     * Handles the NAR contract command, which is a work in progress.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int contract(CommandContext<FabricClientCommandSource> context) {
        if (Contract.openContract()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Handles the domain expansion command, which toggles the state of the domain.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int domainExpansion(CommandContext<FabricClientCommandSource> context) {
        if (!Contract.isSigned()) {
            context.getSource().sendFeedback(NotARat.MOD_TITLE.copy().append(Text.literal(
                    " Binding Vows are essentially contracts that an individual can make with one's self or another person. The act of abiding by the rules and restrictions agreed upon in these contracts can result in a greater power or the achievement of a goal, but breaking a binding vow has uncanny repercussions.")
                    .formatted(Formatting.RED)));
            return 0;
        }

        // Flip the domain expansion state
        boolean bool = NarData.getData().get("domain_expansion").getAsBoolean();
        if (bool) {
            context.getSource().sendFeedback(NotARat.MOD_TITLE.copy()
                    .append(Text.literal(" 領域展開伏魔御廚子").formatted(Formatting.RED)));
        } else {
            context.getSource().sendFeedback(NotARat.MOD_TITLE.copy()
                    .append(Text.literal(" 領域展開無量空処").formatted(Formatting.GREEN)));
        }
        NarData.getData().addProperty("domain_expansion", !bool);

        return 1;
    }

    /**
     * Handles the default command for NAR, which is a catch-all for commands not explicitly
     * defined.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int defaultCommand(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity clientPlayer = context.getSource().getPlayer();
        String command = StringArgumentType.getString(context, "default").trim();

        if (ChatCommands.getInstance().handleCommand(clientPlayer, command)) {
            return 1;
        } else if (ImagePreview.getInstance().handleCommand(command)) {
            return 1;
        } else {
            context.getSource().sendFeedback(NotARat.MOD_TITLE.copy()
                    .append(Text.literal(" Command not found!").formatted(Formatting.RED)));
            return 0;
        }
    }
}
