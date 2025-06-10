package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
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

    // Message Option Group
    @SerialEntry
    public String guildJoinMessage = "";

    @SerialEntry
    public String partyJoinMessage = "";

    @SerialEntry
    public boolean partyLeaderOnly = false;

    // Party Option Group
    @SerialEntry
    public boolean antiGhostParty = false;

    @SerialEntry
    public boolean joinReparty = false;


    @SerialEntry
    public AutoTransfer autoTransfer = AutoTransfer.A;

    public enum AutoTransfer implements NameableEnum {
        A, B, C;

        @Override
        public Text getDisplayName() {
            return Text.literal("mymod.alphabet." + name().toLowerCase());
        }
    }
}
