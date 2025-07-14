package net.volcaronitee.taraton.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Configuration for the Farming features in Taraton.
 */
public class FarmingConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Farming features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Farming features.
     */
    public static ConfigCategory create(TaratonConfig defaults, TaratonConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Farming"))

                // Garden Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Garden"))

                        // TODO: Composter Display
                        .option(Option.<ComposterDisplay>createBuilder()
                                .name(Text.literal("Composter Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/composter_display.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when the composter is not active. Alternatively, you can choose to display activity time as an overlay on the screen."))
                                        .build())
                                .binding(defaults.farming.composterDisplay,
                                        () -> config.farming.composterDisplay,
                                        newVal -> config.farming.composterDisplay = newVal)
                                .controller(TaratonConfig::createEnumController).build())

                        // TODO: Plot Bounding Box
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Plot Bounding Box"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/plot_bounding_box.webp"))
                                        .text(Text.literal(
                                                "Displays a bounding box around the current garden plot."))
                                        .build())
                                .binding(defaults.farming.plotBoundingBox,
                                        () -> config.farming.plotBoundingBox,
                                        newVal -> config.farming.plotBoundingBox = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Visitor Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Visitor Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/visitor_display.webp"))
                                        .text(Text.literal(
                                                "Displays the current list of garden visitors. Tracks visitor activity outside of the garden."))
                                        .build())
                                .binding(defaults.farming.visitorDisplay,
                                        () -> config.farming.visitorDisplay,
                                        newVal -> config.farming.visitorDisplay = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        .build())

                // Pests Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Pests"))

                        // TODO: Desk Highlight
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Desk Highlight"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/desk_highlight.webp"))
                                        .text(Text.literal(
                                                "Highlights plots with sprays and pests in the garden desk menu."))
                                        .build())
                                .binding(defaults.farming.deskHighlight,
                                        () -> config.farming.deskHighlight,
                                        newVal -> config.farming.deskHighlight = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Infestation Warning
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Infestation Warning"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/infestation_warning.webp"))
                                        .text(Text.literal(
                                                "Displays a warning when pests are present in the garden. Sets the number of minimum number of pests to trigger the warning."))
                                        .build())
                                .binding(defaults.farming.infestationWarning,
                                        () -> config.farming.infestationWarning,
                                        newVal -> config.farming.infestationWarning = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 8).step(1))
                                .build())

                        // TODO: Pest Alert
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Pest Alert"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/pest_alert.webp"))
                                        .text(Text.literal(
                                                "Displays a chat message and title when pests spawn in the garden."))
                                        .build())
                                .binding(defaults.farming.pestAlert, () -> config.farming.pestAlert,
                                        newVal -> config.farming.pestAlert = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Spray Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Spray Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/farming/spray_display.webp"))
                                        .text(Text.literal(
                                                "Displays all active sprays on the screen."))
                                        .build())
                                .binding(defaults.farming.sprayDisplay,
                                        () -> config.farming.sprayDisplay,
                                        newVal -> config.farming.sprayDisplay = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

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
