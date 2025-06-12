package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;

public class FarmingConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Farming"))

                // Garden Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Garden"))

                        // Composter Display
                        .option(Option.<ComposterDisplay>createBuilder()
                                .name(Text.literal("Composter Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a chat message and title when the composter is not active. Alternatively, you can choose to display activity time as an overlay on the screen.")))
                                .binding(defaults.farming.composterDisplay,
                                        () -> config.farming.composterDisplay,
                                        newVal -> config.farming.composterDisplay = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // Plot Bounding Box
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Plot Bounding Box"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a bounding box around the current garden plot.")))
                                .binding(defaults.farming.plotBoundingBox,
                                        () -> config.farming.plotBoundingBox,
                                        newVal -> config.farming.plotBoundingBox = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Visitor Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Visitor Display"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays the current list of garden visitors. Tracks visitor activity outside of the garden.")))
                                .binding(defaults.farming.visitorDisplay,
                                        () -> config.farming.visitorDisplay,
                                        newVal -> config.farming.visitorDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Pests Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Pests"))

                        // Desk Highlight
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Desk Highlight"))
                                .description(OptionDescription.of(Text.literal(
                                        "Highlights plots with sprays and pests in the garden desk menu.")))
                                .binding(defaults.farming.deskHighlight,
                                        () -> config.farming.deskHighlight,
                                        newVal -> config.farming.deskHighlight = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Infestation Warning
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Infestation Warning"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a warning when pests are present in the garden. Sets the number of minimum number of pests to trigger the warning.")))
                                .binding(defaults.farming.infestationWarning,
                                        () -> config.farming.infestationWarning,
                                        newVal -> config.farming.infestationWarning = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 8).step(1))
                                .build())

                        // Pest Alert
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Pest Alert"))
                                .description(OptionDescription.of(Text.literal(
                                        "Displays a chat message and title when pests spawn in the garden.")))
                                .binding(defaults.farming.pestAlert, () -> config.farming.pestAlert,
                                        newVal -> config.farming.pestAlert = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Spray Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Spray Display"))
                                .description(OptionDescription.of(
                                        Text.literal("Displays all active sprays on the screen.")))
                                .binding(defaults.farming.sprayDisplay,
                                        () -> config.farming.sprayDisplay,
                                        newVal -> config.farming.sprayDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Garden Option Group
    @SerialEntry
    public ComposterDisplay composterDisplay = ComposterDisplay.TITLE;

    public enum ComposterDisplay implements NameableEnum {
        OFF, TITLE, OVERLAY;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case TITLE -> Text.literal("Inactive Title");
                case OVERLAY -> Text.literal("Time Overlay");
            };
        }
    }

    @SerialEntry
    public boolean plotBoundingBox = false;

    @SerialEntry
    public boolean visitorDisplay = false;

    // Pests Option Group
    @SerialEntry
    public boolean deskHighlight = false;

    @SerialEntry
    public int infestationWarning = 0;

    @SerialEntry
    public boolean pestAlert = false;

    @SerialEntry
    public boolean sprayDisplay = false;
}
