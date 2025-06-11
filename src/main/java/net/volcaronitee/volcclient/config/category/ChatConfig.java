package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class ChatConfig {
    public static ConfigCategory create(VolcClientConfig defaults, VolcClientConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Chat"))

                // Correct Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Correct"))

                        // Autocomplete Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocomplete Command"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables the autocomplete command feature. Autocompletes commands in chat.")))
                                .binding(defaults.chat.autocompleteCommand,
                                        () -> config.chat.autocompleteCommand,
                                        newVal -> config.chat.autocompleteCommand = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Autocorrect Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocorrect Command"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables the autocorrect command feature. Autocorrects commands in chat.")))
                                .binding(defaults.chat.autocorrectCommand,
                                        () -> config.chat.autocorrectCommand,
                                        newVal -> config.chat.autocorrectCommand = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Custom Emotes
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Custom Emotes"))
                                .description(OptionDescription.of(Text.literal(
                                        "Allows the use of MVP++ emotes in chat. Customize your emotes using /vc emotes.")))
                                .binding(defaults.chat.customEmotes, () -> config.chat.customEmotes,
                                        newVal -> config.chat.customEmotes = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Playtime Warning
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Playtime Warning"))
                                .description(OptionDescription.of(Text.literal(
                                        "Warns you when you have been playing for too long.")))
                                .binding(defaults.chat.playtimeWarning,
                                        () -> config.chat.playtimeWarning,
                                        newVal -> config.chat.playtimeWarning = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Word Substitution
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Text Substitution"))
                                .description(OptionDescription.of(Text.literal(
                                        "Substitutes user defined occurences of texts. Set text mapping using /vc text.")))
                                .binding(defaults.chat.textSubstitution,
                                        () -> config.chat.textSubstitution,
                                        newVal -> config.chat.textSubstitution = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Message Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Message"))

                        // Leader Commands
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Leader Commands"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables leader commands in chat. Toggle them using /vc toggles.")))
                                .binding(defaults.chat.leaderCommands,
                                        () -> config.chat.leaderCommands,
                                        newVal -> config.chat.leaderCommands = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Party Commands
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Party Commands"))
                                .description(OptionDescription.of(Text.literal(
                                        "Enables party commands in chat. Toggle them using /vc toggles.")))
                                .binding(defaults.chat.partyCommands,
                                        () -> config.chat.partyCommands,
                                        newVal -> config.chat.partyCommands = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Guild Join Message
                        .option(Option.<String>createBuilder()
                                .name(Text.literal("Guild Join Message"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sends message when anyone joins your guild. Use %player% for their name.")))
                                .binding(defaults.chat.guildJoinMessage,
                                        () -> config.chat.guildJoinMessage,
                                        newVal -> config.chat.guildJoinMessage = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // Party Join Message
                        .option(Option.<String>createBuilder()
                                .name(Text.literal("Party Join Message"))
                                .description(OptionDescription.of(Text.literal(
                                        "Sends message when anyone joins your party. Use %player% for their name.")))
                                .binding(defaults.chat.partyJoinMessage,
                                        () -> config.chat.partyJoinMessage,
                                        newVal -> config.chat.partyJoinMessage = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // Party Leader Only
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Party Leader Only"))
                                .description(OptionDescription.of(Text.literal(
                                        "Only sends party join message when you are the party leader.")))
                                .binding(defaults.chat.partyLeaderOnly,
                                        () -> config.chat.partyLeaderOnly,
                                        newVal -> config.chat.partyLeaderOnly = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        .build())

                // Party Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Message"))

                        // Anti-Ghost Party
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Anti-Ghost Party"))
                                .description(OptionDescription.of(Text.literal(
                                        "Prevents ghost parties from being created when partying multiple people.")))
                                .binding(defaults.chat.antiGhostParty,
                                        () -> config.chat.antiGhostParty,
                                        newVal -> config.chat.antiGhostParty = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Join Reparty
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Reparty"))
                                .description(OptionDescription.of(Text.literal(
                                        "Automatically accepts reparty invites send within 60 seconds.")))
                                .binding(defaults.chat.joinReparty, () -> config.chat.joinReparty,
                                        newVal -> config.chat.joinReparty = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Join Whitelist
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Whitelist"))
                                .description(OptionDescription.of(Text.literal(
                                        "Automatically accepts party invites from whitelisted players. Whitelist players using /vc wl.")))
                                .binding(defaults.chat.joinWhitelist,
                                        () -> config.chat.joinWhitelist,
                                        newVal -> config.chat.joinWhitelist = newVal)
                                .controller(VolcClientConfig::createBooleanController).build())

                        // Auto Transfer
                        .option(Option.<ChatConfig.AutoTransfer>createBuilder()
                                .name(Text.literal("Auto Transfer"))
                                .description(OptionDescription.of(Text.literal(
                                        "Automatically transfers party leadership when you leave the party.")))
                                .binding(defaults.chat.autoTransfer, () -> config.chat.autoTransfer,
                                        newVal -> config.chat.autoTransfer = newVal)
                                .controller(VolcClientConfig::createEnumController).build())

                        .build())

                .build();
    }

    // Correct Option Group
    @SerialEntry
    public boolean autocompleteCommand = false;

    @SerialEntry
    public boolean autocorrectCommand = false;

    @SerialEntry
    public boolean customEmotes = false;

    @SerialEntry
    public boolean playtimeWarning = false;

    @SerialEntry
    public boolean textSubstitution = false;

    // Message Option Group
    @SerialEntry
    public String guildJoinMessage = "";

    @SerialEntry
    public boolean leaderCommands = false;

    @SerialEntry
    public boolean partyCommands = false;

    @SerialEntry
    public String partyJoinMessage = "";

    @SerialEntry
    public boolean partyLeaderOnly = false;

    @SerialEntry
    public String serverKickAnnounce = "";

    // Party Option Group
    @SerialEntry
    public boolean antiGhostParty = false;

    @SerialEntry
    public boolean joinReparty = false;

    @SerialEntry
    public boolean joinWhitelist = false;

    @SerialEntry
    public AutoTransfer autoTransfer = AutoTransfer.OFF;

    public enum AutoTransfer implements NameableEnum {
        OFF, ON_TRANSFER, ON_KICK;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ON_TRANSFER -> Text.literal("On Transfer");
                case ON_KICK -> Text.literal("On Kick");
            };
        }
    }
}
