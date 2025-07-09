package net.volcaronitee.nar.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarConfig;

/**
 * Configuration for the Chat features in NotARat.
 */
public class ChatConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Chat features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Chat features.
     */
    public static ConfigCategory create(NarConfig defaults, NarConfig config) {
        // Party Leader Only Option
        Option<Boolean> partyLeaderOnlyOption = Option.<Boolean>createBuilder()
                .name(Text.literal("Party Leader Only"))
                .description(OptionDescription.createBuilder()
                        .webpImage(
                                Identifier.of(NotARat.MOD_ID, "config/chat/party_leader_only.webp"))
                        .text(Text.literal(
                                "Only sends party join message when you are the party leader."))
                        .build())
                .binding(defaults.chat.partyLeaderOnly, () -> config.chat.partyLeaderOnly,
                        newVal -> config.chat.partyLeaderOnly = newVal)
                .controller(NarConfig::createBooleanController).build();

        // Party Join Message Option
        Option<String> partyJoinMessageOption = Option.<String>createBuilder()
                .name(Text.literal("Party Join Message"))
                .description(OptionDescription.createBuilder()
                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                "config/chat/party_join_message.webp"))
                        .text(Text.literal(
                                "Sends message when anyone joins your party. Use {player} to substitute the player's name."))
                        .build())
                .binding(defaults.chat.partyJoinMessage, () -> config.chat.partyJoinMessage,
                        newVal -> config.chat.partyJoinMessage = newVal)
                .addListener((opt, val) -> partyLeaderOnlyOption
                        .setAvailable(!config.chat.partyJoinMessage.isEmpty()))
                .controller(opt -> StringControllerBuilder.create(opt)).build();

        return ConfigCategory.createBuilder().name(Text.literal("Chat"))

                // Chat Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Correct"))

                        // Chat Alert
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Chat Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/chat_alert.webp"))
                                        .text(Text.literal(
                                                "Alerts you when certain messages are sent in chat. Customize your alerts using §e/vc cam§f."))
                                        .build())
                                .binding(defaults.chat.chatAlert, () -> config.chat.chatAlert,
                                        newVal -> config.chat.chatAlert = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Copy Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Copy Chat"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/copy_chat.webp"))
                                        .text(Text.literal(
                                                "Copies chat messages to clipboard when clicked on while holding shift."))
                                        .build())
                                .binding(defaults.chat.copyChat, () -> config.chat.copyChat,
                                        newVal -> config.chat.copyChat = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Custom Emotes
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Custom Emotes"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/custom_emotes.webp"))
                                        .text(Text.literal(
                                                "Allows the use of §6MVP§c++ §femotes in chat. Customize your emotes using §e/vc em§f."))
                                        .build())
                                .binding(defaults.chat.customEmotes, () -> config.chat.customEmotes,
                                        newVal -> config.chat.customEmotes = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Spam Hider
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Spam Hider"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/spam_hider.webp"))
                                        .text(Text.literal(
                                                "Hides spam messages in chat. Customize the spam filter using §e/vc sl§f."))
                                        .build())
                                .binding(defaults.chat.spamHider, () -> config.chat.spamHider,
                                        newVal -> config.chat.spamHider = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Text Substitution
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Text Substitution"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/text_substitution.webp"))
                                        .text(Text.literal(
                                                "Substitutes text keys with user defined values anywhere text is rendered. Set text mapping using §e/vc sm§f."))
                                        .build())
                                .binding(defaults.chat.textSubstitution,
                                        () -> config.chat.textSubstitution,
                                        newVal -> config.chat.textSubstitution = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Correct Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Correct"))

                        // TODO: Autocomplete Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocomplete Command"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/autocomplete_command.webp"))
                                        .text(Text.literal(
                                                "Enables the autocomplete command feature. Autocompletes commands in chat."))
                                        .build())
                                .binding(defaults.chat.autocompleteCommand,
                                        () -> config.chat.autocompleteCommand,
                                        newVal -> config.chat.autocompleteCommand = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // TODO: Autocorrect Command
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Autocorrect Command"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/autocorrect_command.webp"))
                                        .text(Text.literal(
                                                "Enables the autocorrect command feature. Autocorrects commands in chat."))
                                        .build())
                                .binding(defaults.chat.autocorrectCommand,
                                        () -> config.chat.autocorrectCommand,
                                        newVal -> config.chat.autocorrectCommand = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Playtime Warning
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Playtime Warning"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/playtime_warning.webp"))
                                        .text(Text.literal(
                                                "Warns you when you have been playing for too long."))
                                        .build())
                                .binding(defaults.chat.playtimeWarning,
                                        () -> config.chat.playtimeWarning,
                                        newVal -> config.chat.playtimeWarning = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                // Message Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Message"))

                        // Leader Commands
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Leader Commands"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/leader_commands.webp"))
                                        .text(Text.literal(
                                                "Enables leader commands in chat. Toggle them using /vc toggles."))
                                        .build())
                                .binding(defaults.chat.leaderCommands,
                                        () -> config.chat.leaderCommands,
                                        newVal -> config.chat.leaderCommands = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Party Commands
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Party Commands"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/party_commands.webp"))
                                        .text(Text.literal(
                                                "Enables party commands in chat. Toggle them using /vc toggles."))
                                        .build())
                                .binding(defaults.chat.partyCommands,
                                        () -> config.chat.partyCommands,
                                        newVal -> config.chat.partyCommands = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Status Commands
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Status Commands"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/status_commands.webp"))
                                        .text(Text.literal(
                                                "Enables status commands in chat. Toggle them using /vc toggles."))
                                        .build())
                                .binding(defaults.chat.statusCommands,
                                        () -> config.chat.statusCommands,
                                        newVal -> config.chat.statusCommands = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Guild Join Message
                        .option(Option.<String>createBuilder()
                                .name(Text.literal("Guild Join Message"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/guild_join_message.webp"))
                                        .text(Text.literal(
                                                "Sends message when anyone joins your guild. Use {player} to substitute the player's name."))
                                        .build())
                                .binding(defaults.chat.guildJoinMessage,
                                        () -> config.chat.guildJoinMessage,
                                        newVal -> config.chat.guildJoinMessage = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // Party Join Message
                        .option(partyJoinMessageOption)

                        // Party Leader Only
                        .option(partyLeaderOnlyOption)

                        .build())

                // Party Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Message"))

                        // TODO: Anti-Ghost Party
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Anti-Ghost Party"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/anti_ghost_party.webp"))
                                        .text(Text.literal(
                                                "Prevents ghost parties from being created when partying multiple people."))
                                        .build())
                                .binding(defaults.chat.antiGhostParty,
                                        () -> config.chat.antiGhostParty,
                                        newVal -> config.chat.antiGhostParty = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Auto Transfer
                        .option(Option.<ChatConfig.AutoTransfer>createBuilder()
                                .name(Text.literal("Auto Transfer"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/auto_transfer.webp"))
                                        .text(Text.literal(
                                                "Transfers party leadership if it is transfered to you."))
                                        .build())
                                .binding(defaults.chat.autoTransfer, () -> config.chat.autoTransfer,
                                        newVal -> config.chat.autoTransfer = newVal)
                                .controller(NarConfig::createEnumController).build())

                        // Auto Kick
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Auto Kick"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/auto_kick.webp"))
                                        .text(Text.literal(
                                                "Kicks players from your party if they are in the blacklist. Customize your blacklist using §e/vc bl§f."))
                                        .build())
                                .binding(defaults.chat.autoKick, () -> config.chat.autoKick,
                                        newVal -> config.chat.autoKick = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Join Reparty
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Reparty"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/join_reparty.webp"))
                                        .text(Text.literal("Accepts reparty invites.")).build())
                                .binding(defaults.chat.joinReparty, () -> config.chat.joinReparty,
                                        newVal -> config.chat.joinReparty = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        // Join Whitelist
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Join Whitelist"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(NotARat.MOD_ID,
                                                "config/chat/join_whitelist.webp"))
                                        .text(Text.literal(
                                                "Automatically accepts party invites from whitelisted players. Whitelist players using /vc wl."))
                                        .build())
                                .binding(defaults.chat.joinWhitelist,
                                        () -> config.chat.joinWhitelist,
                                        newVal -> config.chat.joinWhitelist = newVal)
                                .controller(NarConfig::createBooleanController).build())

                        .build())

                .build();
    }

    // Chat Option Group
    @SerialEntry
    public boolean chatAlert = true;

    @SerialEntry
    public boolean copyChat = true;

    @SerialEntry
    public boolean customEmotes = false;

    @SerialEntry
    public boolean spamHider = true;

    @SerialEntry
    public boolean textSubstitution = true;

    // Correct Option Group
    @SerialEntry
    public boolean autocompleteCommand = false;

    @SerialEntry
    public boolean autocorrectCommand = true;

    @SerialEntry
    public boolean playtimeWarning = true;

    // Message Option Group
    @SerialEntry
    public String guildJoinMessage = "";

    @SerialEntry
    public String partyJoinMessage = "";

    @SerialEntry
    public boolean partyLeaderOnly = false;

    @SerialEntry
    public boolean leaderCommands = false;

    @SerialEntry
    public boolean partyCommands = false;

    @SerialEntry
    public boolean statusCommands = false;

    @SerialEntry
    public String serverKickAnnounce = "";

    // Party Option Group
    @SerialEntry
    public boolean antiGhostParty = false;

    @SerialEntry
    public boolean autoKick = false;

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

    @SerialEntry
    public boolean joinReparty = false;

    @SerialEntry
    public boolean joinWhitelist = true;
}
