package net.volcaronitee.taraton.util;

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
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonData;
import net.volcaronitee.taraton.config.TaratonJson;
import net.volcaronitee.taraton.config.TaratonToggle;
import net.volcaronitee.taraton.feature.chat.AutoKick;
import net.volcaronitee.taraton.feature.chat.ChatAlert;
import net.volcaronitee.taraton.feature.chat.ChatCommands;
import net.volcaronitee.taraton.feature.chat.CustomEmote;
import net.volcaronitee.taraton.feature.chat.JoinParty;
import net.volcaronitee.taraton.feature.chat.SpamHider;
import net.volcaronitee.taraton.feature.chat.TextSubstitution;
import net.volcaronitee.taraton.feature.combat.EntityHighlight;
import net.volcaronitee.taraton.feature.container.WardrobeSwap;
import net.volcaronitee.taraton.feature.crimson_isle.VanquisherWarp;
import net.volcaronitee.taraton.feature.general.ImagePreview;
import net.volcaronitee.taraton.feature.general.PlayerScale;
import net.volcaronitee.taraton.feature.general.WidgetDisplay;
import net.volcaronitee.taraton.feature.qol.CommandHotkey;
import net.volcaronitee.taraton.feature.qol.HideEntity;
import net.volcaronitee.taraton.feature.qol.ProtectItem;
import net.volcaronitee.taraton.util.helper.Contract;

/**
 * Utility class for handling client commands.
 */
public class CommandUtil {
    private static final String[] ALIASES = {"nar", "notarat", "taraton", "tar", "rat"};

    /**
     * Initializes the client command registration for Taraton.
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

                                // Wardrobe Swap command
                                .then(literal("wardrobe")
                                        .executes(WardrobeSwap.getInstance()::setWardrobe))

                                // Save command
                                .then(literal("save").executes(CommandUtil::save))

                                // Debug command
                                .then(literal("debug").executes(CommandUtil::debug))

                                // Protect command
                                .then(literal("protect")
                                        .executes(ProtectItem.getInstance()::protect))
                                .then(literal("protectitem")
                                        .executes(ProtectItem.getInstance()::protect))

                                // Lists commands
                                .then(ChatCommands.AVENGER_LIST.createCommand("avengerlist"))
                                .then(ChatCommands.AVENGER_LIST.createCommand("al"))
                                .then(AutoKick.BLACK_LIST.createCommand("blacklist"))
                                .then(AutoKick.BLACK_LIST.createCommand("bl"))
                                .then(EntityHighlight.ENTITY_LIST.createCommand("entitylist"))
                                .then(EntityHighlight.ENTITY_LIST.createCommand("el"))
                                .then(HideEntity.HOW_LIST.createCommand("hideonworldlist"))
                                .then(HideEntity.HOW_LIST.createCommand("howl"))
                                .then(ChatCommands.PREFIX_LIST.createCommand("prefixlist"))
                                .then(ChatCommands.PREFIX_LIST.createCommand("pl"))
                                .then(SpamHider.SPAM_LIST.createCommand("spamlist"))
                                .then(SpamHider.SPAM_LIST.createCommand("sl"))
                                .then(JoinParty.WHITE_LIST.createCommand("whitelist"))
                                .then(JoinParty.WHITE_LIST.createCommand("wl"))
                                .then(WidgetDisplay.WIDGET_LIST.createCommand("widgetlist"))
                                .then(WidgetDisplay.WIDGET_LIST.createCommand("wgl"))
                                .then(VanquisherWarp.VANQUISHER_LIST.createCommand("vanqlist"))
                                .then(VanquisherWarp.VANQUISHER_LIST.createCommand("vl"))

                                // Maps commands
                                .then(ChatAlert.CHAT_ALERT_MAP.createCommand("chatalertmap"))
                                .then(ChatAlert.CHAT_ALERT_MAP.createCommand("cam"))
                                .then(CustomEmote.EMOTE_MAP.createCommand("emotemap"))
                                .then(CustomEmote.EMOTE_MAP.createCommand("em"))
                                .then(CommandHotkey.HOTKEY_MAP.createCommand("hotkeymap"))
                                .then(CommandHotkey.HOTKEY_MAP.createCommand("hkm"))
                                .then(PlayerScale.PLAYER_SCALE_MAP.createCommand("playerscalemap"))
                                .then(PlayerScale.PLAYER_SCALE_MAP.createCommand("psm"))
                                .then(ProtectItem.PROTECT_MAP.createCommand("protectmap"))
                                .then(ProtectItem.PROTECT_MAP.createCommand("pm"))
                                .then(TextSubstitution.SUBSTITUTION_MAP.createCommand("submap"))
                                .then(TextSubstitution.SUBSTITUTION_MAP.createCommand("sm"))
                                .then(WardrobeSwap.WARDROBE_SWAP_MAP
                                        .createCommand("wardrobeswapmap"))
                                .then(WardrobeSwap.WARDROBE_SWAP_MAP.createCommand("wsm"))

                                // Chat/client commands
                                .then(argument("default", StringArgumentType.greedyString())
                                        .executes(CommandUtil::defaultCommand))

                        // Command End
                        );
                    }
                });
    }

    /**
     * Displays help information for the Taraton commands.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int help(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Taraton WIP"));
        return 1;
    }

    /**
     * Opens the settings screen for Taraton.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int settings(CommandContext<FabricClientCommandSource> context) {
        // Defer the screen opening to the main client thread
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> {
            client.setScreen(TaratonConfig.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Opens the toggles screen for Taraton.
     *
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int toggles(CommandContext<FabricClientCommandSource> context) {
        // Defer the screen opening to the main client thread
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> {
            client.setScreen(TaratonToggle.createScreen(client.currentScreen));
        });

        return 1;
    }

    /**
     * Echoes a message back to the player.
     * 
     * @param context The command context containing the source and arguments.
     * @param message The message to echo back to the player.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int echo(CommandContext<FabricClientCommandSource> context, String message) {
        Taraton.sendMessage(message);
        return 1;
    }

    /**
     * Saves the current state of Taraton data to disk.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int save(CommandContext<FabricClientCommandSource> context) {
        TaratonJson.saveInstances(MinecraftClient.getInstance());
        Taraton.sendMessage(Text.literal("Successfully saved data!").formatted(Formatting.GREEN));
        return 1;
    }

    /**
     * Displays debug information for the Taraton.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int debug(CommandContext<FabricClientCommandSource> context) {
        String debugMessage = String.format("Taraton Debug:\n%s\n\n%s\n\n%s",
                PlayerUtil.debugPlayer(), LocationUtil.debugLocation(), PartyUtil.debugParty());
        Taraton.sendMessage(Text.literal(debugMessage).formatted(Formatting.YELLOW));
        return 1;
    }

    /**
     * Handles the Taraton contract command, which is a work in progress.
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
            Taraton.sendMessage(Text.literal(
                    "Binding Vows are essentially contracts that an individual can make with one's self or another person. The act of abiding by the rules and restrictions agreed upon in these contracts can result in a greater power or the achievement of a goal, but breaking a binding vow has uncanny repercussions.")
                    .formatted(Formatting.RED));
            return 0;
        }

        // Flip the domain expansion state
        boolean bool = TaratonData.getData().get("domain_expansion").getAsBoolean();
        TaratonData.getData().addProperty("domain_expansion", !bool);

        if (bool) {
            Taraton.sendMessage(Text.literal("領域展開伏魔御廚子").formatted(Formatting.RED));
        } else {
            Taraton.sendMessage(Text.literal("領域展開無量空処").formatted(Formatting.GREEN));
        }

        return 1;
    }

    public static int hehehe(CommandContext<FabricClientCommandSource> context) {
        if (!Contract.isSigned()) {
            Taraton.sendMessage(Text.literal("2 months.").formatted(Formatting.RED));
            return 0;
        }

        // Flip the nsfw state
        boolean bool = TaratonData.getData().get("nsfw").getAsBoolean();
        if (bool) {
            Taraton.sendMessage(Text.literal("Lock the fuck in.").formatted(Formatting.RED));
        } else {
            Taraton.sendMessage(Text.literal("Hehehe, I'm horny...").formatted(Formatting.GREEN));
        }
        TaratonData.getData().addProperty("nsfw", !bool);

        return 1;
    }

    /**
     * Handles the default command for Taraton, which is a catch-all for commands not explicitly
     * defined.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int defaultCommand(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity clientPlayer = context.getSource().getPlayer();
        String command = StringArgumentType.getString(context, "default").trim();
        String[] args = command.split(" ");
        String core = args[0];

        if (core.equals("contract") || core.equals("bindingvow")) {
            return contract(context);
        } else if (core.equals("domainexpansion") || core.equals("ryoikitenkai")) {
            return domainExpansion(context);
        } else if (core.equals("echo")) {
            return echo(context, command.substring(5).trim());
        } else if (core.equals("hehehe")) {
            return hehehe(context);
        } else if (ChatCommands.getInstance().handleCommand(clientPlayer, command)) {
            return 1;
        } else if (ImagePreview.getInstance().handleCommand(command)) {
            return 1;
        } else {
            Taraton.sendMessage(Text.literal("Unknown command: ").formatted(Formatting.RED)
                    .append(Text.literal(command).formatted(Formatting.YELLOW)));
            return 0;
        }
    }
}
