package net.volcaronitee.taraton.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import net.volcaronitee.taraton.feature.container.SlotBinding;
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
        ClientCommandRegistrationCallback.EVENT.register(CommandUtil::register);
    }

    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess access) {
        for (String alias : ALIASES) {
            LiteralArgumentBuilder<FabricClientCommandSource> command = literal(alias)
                    .executes(CommandUtil::settingsCommand)
                    .then(literal("help").executes(CommandUtil::helpCommand))
                    .then(literal("settings").executes(CommandUtil::settingsCommand))
                    .then(literal("toggles").executes(CommandUtil::togglesCommand))
                    .then(literal("gui").executes(OverlayUtil::moveGui))
                    .then(literal("save").executes(CommandUtil::saveCommand))
                    .then(literal("debug").executes(CommandUtil::debugCommand))
                    .then(literal("protect").executes(ProtectItem.getInstance()::protect))
                    .then(literal("protectitem").executes(ProtectItem.getInstance()::protect))
                    .then(literal("wardrobe").executes(WardrobeSwap.getInstance()::setWardrobe))
                    .then(literal("slotbinding")
                            .executes(SlotBinding.getInstance()::setSlotBinding))
                    .then(argument("dynamic_command", StringArgumentType.greedyString())
                            .executes(CommandUtil::dynamicCommandHandler));

            registerListCommands(command);
            registerMapCommands(command);

            dispatcher.register(command);
        }
    }

    private static void registerListCommands(
            LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command.then(ChatCommands.AVENGER_LIST.createCommand("avengerlist"));
        command.then(ChatCommands.AVENGER_LIST.createCommand("al"));
        command.then(AutoKick.BLACK_LIST.createCommand("blacklist"));
        command.then(AutoKick.BLACK_LIST.createCommand("bl"));
        command.then(EntityHighlight.ENTITY_LIST.createCommand("entitylist"));
        command.then(EntityHighlight.ENTITY_LIST.createCommand("el"));
        command.then(HideEntity.HOW_LIST.createCommand("hideonworldlist"));
        command.then(HideEntity.HOW_LIST.createCommand("howl"));
        command.then(ChatCommands.PREFIX_LIST.createCommand("prefixlist"));
        command.then(ChatCommands.PREFIX_LIST.createCommand("pl"));
        command.then(SpamHider.SPAM_LIST.createCommand("spamlist"));
        command.then(SpamHider.SPAM_LIST.createCommand("sl"));
        command.then(JoinParty.WHITE_LIST.createCommand("whitelist"));
        command.then(JoinParty.WHITE_LIST.createCommand("wl"));
        command.then(WidgetDisplay.WIDGET_LIST.createCommand("widgetlist"));
        command.then(WidgetDisplay.WIDGET_LIST.createCommand("wgl"));
        command.then(VanquisherWarp.VANQUISHER_LIST.createCommand("vanqlist"));
        command.then(VanquisherWarp.VANQUISHER_LIST.createCommand("vl"));
    }

    private static void registerMapCommands(
            LiteralArgumentBuilder<FabricClientCommandSource> command) {
        command.then(ChatAlert.CHAT_ALERT_MAP.createCommand("chatalertmap"));
        command.then(ChatAlert.CHAT_ALERT_MAP.createCommand("cam"));
        command.then(CustomEmote.EMOTE_MAP.createCommand("emotemap"));
        command.then(CustomEmote.EMOTE_MAP.createCommand("em"));
        command.then(CommandHotkey.HOTKEY_MAP.createCommand("hotkeymap"));
        command.then(CommandHotkey.HOTKEY_MAP.createCommand("hkm"));
        command.then(PlayerScale.PLAYER_SCALE_MAP.createCommand("playerscalemap"));
        command.then(PlayerScale.PLAYER_SCALE_MAP.createCommand("psm"));
        command.then(ProtectItem.PROTECT_MAP.createCommand("protectmap"));
        command.then(ProtectItem.PROTECT_MAP.createCommand("pm"));
        command.then(SlotBinding.SLOT_BINDING_MAP.createCommand("slotbindingmap"));
        command.then(SlotBinding.SLOT_BINDING_MAP.createCommand("sbm"));
        command.then(TextSubstitution.SUBSTITUTION_MAP.createCommand("submap"));
        command.then(TextSubstitution.SUBSTITUTION_MAP.createCommand("sm"));
        command.then(WardrobeSwap.WARDROBE_SWAP_MAP.createCommand("wardrobeswapmap"));
        command.then(WardrobeSwap.WARDROBE_SWAP_MAP.createCommand("wsm"));
    }

    /**
     * Displays help information for the Taraton commands.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int helpCommand(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Taraton WIP"));
        return 1;
    }

    /**
     * Opens the settings screen for Taraton.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int settingsCommand(CommandContext<FabricClientCommandSource> context) {
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
    private static int togglesCommand(CommandContext<FabricClientCommandSource> context) {
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
    private static int echoCommand(CommandContext<FabricClientCommandSource> context,
            String message) {
        Taraton.sendMessage(message);
        return 1;
    }

    /**
     * Saves the current state of Taraton data to disk.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int saveCommand(CommandContext<FabricClientCommandSource> context) {
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
    private static int debugCommand(CommandContext<FabricClientCommandSource> context) {
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
    private static int contractCommand(CommandContext<FabricClientCommandSource> context) {
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
    private static int domainExpansionCommand(CommandContext<FabricClientCommandSource> context) {
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

    /**
     * Handles the hehehe command, which toggles the NSFW state.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int heheheCommand(CommandContext<FabricClientCommandSource> context) {
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
    private static int dynamicCommandHandler(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity clientPlayer = context.getSource().getPlayer();
        String command = StringArgumentType.getString(context, "default").trim();
        String[] args = command.split(" ");
        String core = args[0];

        if (core.equals("contract") || core.equals("bindingvow")) {
            return contractCommand(context);
        } else if (core.equals("domainexpansion") || core.equals("ryoikitenkai")) {
            return domainExpansionCommand(context);
        } else if (core.equals("echo")) {
            return echoCommand(context, command.substring(5).trim());
        } else if (core.equals("hehehe") || core.equals("nsfw")) {
            return heheheCommand(context);
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
