package net.volcaronitee.volcclient.config.toggle;

import java.util.List;
import java.util.Map;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ToggleUtil;

public class ChatToggle {
    public static ConfigCategory create(ToggleUtil defaults, ToggleUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Chat"))

                // Chat Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Correct"))

                        // All Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("All Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables chat commands for public chat messages."))
                                        .build())
                                .binding(defaults.chat.allChat, () -> config.chat.allChat,
                                        newVal -> config.chat.allChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Guild Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Guild Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables chat commands for guild chat messages."))
                                        .build())
                                .binding(defaults.chat.guildChat, () -> config.chat.guildChat,
                                        newVal -> config.chat.guildChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Party Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Party Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables chat commands for party chat messages."))
                                        .build())
                                .binding(defaults.chat.partyChat, () -> config.chat.partyChat,
                                        newVal -> config.chat.partyChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Private Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Private Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables chat commands for private chat messages."))
                                        .build())
                                .binding(defaults.chat.privateChat, () -> config.chat.privateChat,
                                        newVal -> config.chat.privateChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Whitelist Lock
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Whitelist Lock"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Enables a whitelist lock for chat commands. This will only allow commands from whitelisted players."))
                                        .build())
                                .binding(defaults.chat.whitelistLock,
                                        () -> config.chat.whitelistLock,
                                        newVal -> config.chat.whitelistLock = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .build())

                // Leader Commands Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Leader Commands"))

                        // All Invite
                        .option(Option.<Boolean>createBuilder().name(Text.literal("All Invite"))
                                .description(createDescription("Enables all invite requests.",
                                        List.of("allinvite", "allinv")))
                                .binding(defaults.chat.allInvite, () -> config.chat.allInvite,
                                        newVal -> config.chat.allInvite = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Mute
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Mute"))
                                .description(createDescription("Enables party mute requests.",
                                        List.of("mute")))
                                .binding(defaults.chat.mute, () -> config.chat.mute,
                                        newVal -> config.chat.mute = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Stream
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Stream Open"))
                                .description(createDescription("Enables stream open requests.",
                                        List.of("streamopen", "stream"),
                                        Map.of("[size]", "The maximum size of the party.")))
                                .binding(defaults.chat.stream, () -> config.chat.stream,
                                        newVal -> config.chat.stream = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Warp
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Warp"))
                                .description(createDescription("Enables party warp requests.",
                                        List.of("warp")))
                                .binding(defaults.chat.warp, () -> config.chat.warp,
                                        newVal -> config.chat.warp = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Join Instance
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Instance"))
                                .description(createDescription("Enables join instance requests.",
                                        List.of("join", "instance"),
                                        Map.of("<instance>",
                                                "The name of the instance to join. Valid instances currently include §bF1-F7§r for Catacombs, §bM1-M7§r for Master Mode Catacombs, and §bT1-T5§r for Kuudra.")))
                                .binding(defaults.chat.instance, () -> config.chat.instance,
                                        newVal -> config.chat.instance = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Invite
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Invite"))
                                .description(createDescription("Enables party invite requests.",
                                        List.of("invite", "inv"),
                                        Map.of("<player>", "The player to invite to the party.")))
                                .binding(defaults.chat.invite, () -> config.chat.invite,
                                        newVal -> config.chat.invite = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Kick
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Kick"))
                                .description(createDescription("Enables party kick requests.",
                                        List.of("kick"),
                                        Map.of("[player]",
                                                "The player to kick from the party. If not specified, the player who sent the command will be kicked.")))
                                .binding(defaults.chat.kick, () -> config.chat.kick,
                                        newVal -> config.chat.kick = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Transfer
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Transfer"))
                                .description(createDescription("Enables party transfer requests.",
                                        List.of("transfer", "ptme", "pt", "pm"),
                                        Map.of("[player]",
                                                "The player to transfer the party leadership to. If not specified, the party will be transferred to the player who sent the command.")))
                                .binding(defaults.chat.transfer, () -> config.chat.transfer,
                                        newVal -> config.chat.transfer = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Promote
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Promote"))
                                .description(createDescription("Enables party promote requests.",
                                        List.of("promote"),
                                        Map.of("[player]",
                                                "The player to promote to party leader. If not specified, the player who sent the command will be promoted.")))
                                .binding(defaults.chat.promote, () -> config.chat.promote,
                                        newVal -> config.chat.promote = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Demote
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Demote"))
                                .description(createDescription("Enables party demote requests.",
                                        List.of("demote"),
                                        Map.of("[player]",
                                                "The player to demote from party leader. If not specified, the player who sent the command will be demoted.")))
                                .binding(defaults.chat.demote, () -> config.chat.demote,
                                        newVal -> config.chat.demote = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Leader Help
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Help"))
                                .description(createDescription("Enables leader help requests.",
                                        List.of("help", "leaderhelp", "lh", "lhelp")))
                                .binding(defaults.chat.leaderHelp, () -> config.chat.leaderHelp,
                                        newVal -> config.chat.leaderHelp = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .build())

                // Party Commands Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Party Commands"))

                        // Eight Ball
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Eight Ball"))
                                .description(createDescription("Enables the eight ball command.",
                                        List.of("8ball"),
                                        Map.of("[question]",
                                                "The question to ask the eight ball.")))
                                .binding(defaults.chat.eightBall, () -> config.chat.eightBall,
                                        newVal -> config.chat.eightBall = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Coin Flip
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Coin Flip"))
                                .description(createDescription("Enables the coin flip command.",
                                        List.of("coin", "flip", "cf")))
                                .binding(defaults.chat.coinFlip, () -> config.chat.coinFlip,
                                        newVal -> config.chat.coinFlip = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Dice Roll
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Dice Roll"))
                                .description(createDescription("Enables the dice roll command.",
                                        List.of("dice", "roll"),
                                        Map.of("[sides]",
                                                "The number of sides on the dice. Defaults to 6 if not specified.")))
                                .binding(defaults.chat.diceRoll, () -> config.chat.diceRoll,
                                        newVal -> config.chat.diceRoll = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Slander
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Slander"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Enables the slander command. I made a severe and continuous lapse in my judgment, and I don't expect to be forgiven."))
                                        .build())
                                .binding(defaults.chat.slander, () -> config.chat.slander,
                                        newVal -> config.chat.slander = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Waifu
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Waifu"))
                                .description(createDescription("Enables the waifu command.",
                                        List.of("waifu", "women", "w")))
                                .binding(defaults.chat.waifu, () -> config.chat.waifu,
                                        newVal -> config.chat.waifu = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .build())

                .build();
    }

    private static OptionDescription createDescription(String text, List<String> aliases,
            Map<String, String> arguments) {
        MutableText description = Text.literal(text);
        description.append(Text.literal(" This will scan for the following:\n\n"));

        // Format the aliases and arguments
        String argumentsStr = "";
        String argumentsText = "\nArgument descriptions:\n\n";
        argumentsText +=
                "- §b<prefix>§r: Any command prefix used to trigger the command. Set prefixes using §e/vc pl§r.";

        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            argumentsStr += entry.getKey() + " ";

            argumentsText += "- §b" + entry.getKey() + "§r: " + entry.getValue();
            if (entry.getKey().endsWith("]")) {
                argumentsText += "  §7(Optional)";
            }
            argumentsText += "\n";
        }

        // Append each alias with the argument string
        for (String alias : aliases) {
            description.append(Text.literal("- §b<prefix>" + alias + " " + argumentsStr + "§r\n"));
        }

        // Append the arguments text if there are any
        description.append(Text.literal(argumentsText));

        return OptionDescription.createBuilder().text(description).build();
    }

    private static OptionDescription createDescription(String text, List<String> aliases) {
        return createDescription(text, aliases, Map.of());
    }

    // Chat Option Group
    @SerialEntry
    public boolean allChat = false;

    @SerialEntry
    public boolean guildChat = false;

    @SerialEntry
    public boolean partyChat = true;

    @SerialEntry
    public boolean privateChat = true;

    @SerialEntry
    public boolean whitelistLock = true;

    // Leader Commands Option Group
    @SerialEntry
    public boolean allInvite = true;

    @SerialEntry
    public boolean mute = true;

    @SerialEntry
    public boolean stream = true;

    @SerialEntry
    public boolean warp = true;

    @SerialEntry
    public boolean instance = true;

    @SerialEntry
    public boolean invite = true;

    @SerialEntry
    public boolean kick = true;

    @SerialEntry
    public boolean transfer = true;

    @SerialEntry
    public boolean promote = true;

    @SerialEntry
    public boolean demote = true;

    @SerialEntry
    public boolean leaderHelp = true;

    // Party Commands Option Group
    @SerialEntry
    public boolean eightBall = true;

    @SerialEntry
    public boolean coinFlip = true;

    @SerialEntry
    public boolean diceRoll = true;

    @SerialEntry
    public boolean slander = true;

    @SerialEntry
    public boolean waifu = true;
}
