package net.volcaronitee.volcclient.config.toggle;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
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
                                                "Enable chat commands for public chat messages."))
                                        .build())
                                .binding(defaults.chat.allChat, () -> config.chat.allChat,
                                        newVal -> config.chat.allChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Guild Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Guild Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enable chat commands for guild chat messages."))
                                        .build())
                                .binding(defaults.chat.guildChat, () -> config.chat.guildChat,
                                        newVal -> config.chat.guildChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Party Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Party Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enable chat commands for party chat messages."))
                                        .build())
                                .binding(defaults.chat.partyChat, () -> config.chat.partyChat,
                                        newVal -> config.chat.partyChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Private Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Private Chat"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enable chat commands for private chat messages."))
                                        .build())
                                .binding(defaults.chat.privateChat, () -> config.chat.privateChat,
                                        newVal -> config.chat.privateChat = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .build())

                .build();
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
}
