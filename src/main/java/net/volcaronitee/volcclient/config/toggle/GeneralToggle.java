package net.volcaronitee.volcclient.config.toggle;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ToggleUtil;

/**
 * Configuration for general toggles in VolcClient.
 */
public class GeneralToggle {
    /**
     * Creates a new {@link ConfigCategory} for the general toggles.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the general toggles.
     */
    public static ConfigCategory create(ToggleUtil defaults, ToggleUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Chat"))

                // Chat Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Server Status"))

                        // All Chat
                        .option(Option.<Boolean>createBuilder().name(Text.literal("XYZ"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Enables the XYZ coordinates in the server status overlay."))
                                        .build())
                                .binding(defaults.general.xyz, () -> config.general.xyz,
                                        newVal -> config.general.xyz = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Yaw/Pitch
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Yaw/Pitch"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Enables the Yaw and Pitch in the server status overlay."))
                                        .build())
                                .binding(defaults.general.yawPitch, () -> config.general.yawPitch,
                                        newVal -> config.general.yawPitch = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Direction
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Direction"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Enables the Direction in the server status overlay."))
                                        .build())
                                .binding(defaults.general.direction, () -> config.general.direction,
                                        newVal -> config.general.direction = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Ping
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Ping"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables the Ping in the server status overlay."))
                                        .build())
                                .binding(defaults.general.ping, () -> config.general.ping,
                                        newVal -> config.general.ping = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // FPS
                        .option(Option.<Boolean>createBuilder().name(Text.literal("FPS"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables the FPS in the server status overlay."))
                                        .build())
                                .binding(defaults.general.fps, () -> config.general.fps,
                                        newVal -> config.general.fps = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // TPS
                        .option(Option.<Boolean>createBuilder().name(Text.literal("TPS"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables the TPS in the server status overlay."))
                                        .build())
                                .binding(defaults.general.tps, () -> config.general.tps,
                                        newVal -> config.general.tps = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // CPS
                        .option(Option.<Boolean>createBuilder().name(Text.literal("CPS"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables the CPS in the server status overlay."))
                                        .build())
                                .binding(defaults.general.cps, () -> config.general.cps,
                                        newVal -> config.general.cps = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        // Day
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Day"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Enables the Day in the server status overlay."))
                                        .build())
                                .binding(defaults.general.day, () -> config.general.day,
                                        newVal -> config.general.day = newVal)
                                .controller(TickBoxControllerBuilder::create).build())

                        .build())

                .build();
    }

    // Server Status Option Group
    @SerialEntry
    public boolean xyz = true;

    @SerialEntry
    public boolean yawPitch = true;

    @SerialEntry
    public boolean direction = true;

    @SerialEntry
    public boolean ping = true;

    @SerialEntry
    public boolean fps = true;

    @SerialEntry
    public boolean tps = true;

    @SerialEntry
    public boolean cps = true;

    @SerialEntry
    public boolean day = true;
}
