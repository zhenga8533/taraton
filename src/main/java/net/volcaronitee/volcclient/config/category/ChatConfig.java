package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;

public class ChatConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Chat"))

                // Correct Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Correct"))

                        // TODO: Autocomplete Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocomplete Command"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/autocomplete_command.webp"))
                                        .text(Text.literal(
                                                "Enables the autocomplete command feature. Autocompletes commands in chat."))
                                        .build())
                                .binding(defaults.chat.autocompleteCommand,
                                        () -> config.chat.autocompleteCommand,
                                        newVal -> config.chat.autocompleteCommand = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Autocorrect Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocorrect Command"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/autocorrect_command.webp"))
                                        .text(Text.literal(
                                                "Enables the autocorrect command feature. Autocorrects commands in chat."))
                                        .build())
                                .binding(defaults.chat.autocorrectCommand,
                                        () -> config.chat.autocorrectCommand,
                                        newVal -> config.chat.autocorrectCommand = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Custom Emotes
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Custom Emotes"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/custom_emotes.webp"))
                                        .text(Text.literal(
                                                "Allows the use of §6MVP§c++ §femotes in chat. Customize your emotes using §e/vc em§f."))
                                        .build())
                                .binding(defaults.chat.customEmotes, () -> config.chat.customEmotes,
                                        newVal -> config.chat.customEmotes = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Playtime Warning
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Playtime Warning"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/playtime_warning.webp"))
                                        .text(Text.literal(
                                                "Warns you when you have been playing for too long."))
                                        .build())
                                .binding(defaults.chat.playtimeWarning,
                                        () -> config.chat.playtimeWarning,
                                        newVal -> config.chat.playtimeWarning = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Spam Hider
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Spam Hider"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/spam_hider.webp"))
                                        .text(Text.literal(
                                                "Hides spam messages in chat. Customize the spam filter using §e/vc sl§f."))
                                        .build())
                                .binding(defaults.chat.spamHider, () -> config.chat.spamHider,
                                        newVal -> config.chat.spamHider = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Text Substitution
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Text Substitution"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/text_substitution.webp"))
                                        .text(Text.literal(
                                                "Substitutes text keys with user defined values anywhere text is rendered. Set text mapping using §e/vc sm§f."))
                                        .build())
                                .binding(defaults.chat.textSubstitution,
                                        () -> config.chat.textSubstitution,
                                        newVal -> config.chat.textSubstitution = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Message Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Message"))

                        // TODO: Leader Commands
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Leader Commands"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/leader_commands.webp"))
                                        .text(Text.literal(
                                                "Enables leader commands in chat. Toggle them using /vc toggles."))
                                        .build())
                                .binding(defaults.chat.leaderCommands,
                                        () -> config.chat.leaderCommands,
                                        newVal -> config.chat.leaderCommands = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Party Commands
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Party Commands"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/party_commands.webp"))
                                        .text(Text.literal(
                                                "Enables party commands in chat. Toggle them using /vc toggles."))
                                        .build())
                                .binding(defaults.chat.partyCommands,
                                        () -> config.chat.partyCommands,
                                        newVal -> config.chat.partyCommands = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Guild Join Message
                        .option(Option.<String>createBuilder()
                                .name(Text.literal("Guild Join Message"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/guild_join_message.webp"))
                                        .text(Text.literal(
                                                "Sends message when anyone joins your guild. Use %player% for their name."))
                                        .build())
                                .binding(defaults.chat.guildJoinMessage,
                                        () -> config.chat.guildJoinMessage,
                                        newVal -> config.chat.guildJoinMessage = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // TODO: Party Join Message
                        .option(Option.<String>createBuilder()
                                .name(Text.literal("Party Join Message"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/party_join_message.webp"))
                                        .text(Text.literal(
                                                "Sends message when anyone joins your party. Use %player% for their name."))
                                        .build())
                                .binding(defaults.chat.partyJoinMessage,
                                        () -> config.chat.partyJoinMessage,
                                        newVal -> config.chat.partyJoinMessage = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // TODO: Party Leader Only
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Party Leader Only"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/party_leader_only.webp"))
                                        .text(Text.literal(
                                                "Only sends party join message when you are the party leader."))
                                        .build())
                                .binding(defaults.chat.partyLeaderOnly,
                                        () -> config.chat.partyLeaderOnly,
                                        newVal -> config.chat.partyLeaderOnly = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Party Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Message"))

                        // TODO: Anti-Ghost Party
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Anti-Ghost Party"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/anti_ghost_party.webp"))
                                        .text(Text.literal(
                                                "Prevents ghost parties from being created when partying multiple people."))
                                        .build())
                                .binding(defaults.chat.antiGhostParty,
                                        () -> config.chat.antiGhostParty,
                                        newVal -> config.chat.antiGhostParty = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Join Reparty
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Reparty"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/join_reparty.webp"))
                                        .text(Text.literal(
                                                "Automatically accepts reparty invites send within 60 seconds."))
                                        .build())
                                .binding(defaults.chat.joinReparty, () -> config.chat.joinReparty,
                                        newVal -> config.chat.joinReparty = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Join Whitelist
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Whitelist"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/join_whitelist.webp"))
                                        .text(Text.literal(
                                                "Automatically accepts party invites from whitelisted players. Whitelist players using /vc wl."))
                                        .build())
                                .binding(defaults.chat.joinWhitelist,
                                        () -> config.chat.joinWhitelist,
                                        newVal -> config.chat.joinWhitelist = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Auto Transfer
                        .option(Option.<ChatConfig.AutoTransfer>createBuilder()
                                .name(Text.literal("Auto Transfer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/chat/auto_transfer.webp"))
                                        .text(Text.literal(
                                                "Automatically transfers party leadership when you leave the party."))
                                        .build())
                                .binding(defaults.chat.autoTransfer, () -> config.chat.autoTransfer,
                                        newVal -> config.chat.autoTransfer = newVal)
                                .controller(ConfigUtil::createEnumController).build())

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
    public boolean spamHider = false;

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
