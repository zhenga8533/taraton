package net.volcaronitee.taraton.config.category;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.taraton.Taraton;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Configuration for the Template features in Taraton.
 */
public class TemplateConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Template features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Template features.
     */
    public static ConfigCategory create(TaratonConfig defaults, TaratonConfig config) {
        return ConfigCategory.createBuilder().name(Text.literal("Category"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Group"))

                        // TODO: Boolean
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Boolean"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.bool, () -> config.template.bool,
                                        newVal -> config.template.bool = newVal)
                                .controller(TaratonConfig::createBooleanController).build())

                        // TODO: Boolean With Values
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Boolean With Values"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.bool, () -> config.template.bool,
                                        newVal -> config.template.bool = newVal)
                                .controller(opt -> TaratonConfig.createBooleanController(opt,
                                        "trueString", "falseString"))
                                .build())

                        // TODO: Tick Box
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Tick Box"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.bool, () -> config.template.bool,
                                        newVal -> config.template.bool = newVal)
                                .controller(opt -> TickBoxControllerBuilder.create(opt)).build())

                        // TODO: Integer Slider
                        .option(Option.<Integer>createBuilder().name(Text.literal("Integer Slider"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.integer, () -> config.template.integer,
                                        newVal -> config.template.integer = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100).step(5))
                                .build())

                        // TODO: String
                        .option(Option.<String>createBuilder().name(Text.literal("String"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.string, () -> config.template.string,
                                        newVal -> config.template.string = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // TODO: Enum
                        .option(Option.<TemplateConfig.Imu>createBuilder()
                                .name(Text.literal("Enum"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.imu, () -> config.template.imu,
                                        newVal -> config.template.imu = newVal)
                                .controller(TaratonConfig::createEnumController).build())

                        // TODO: Color
                        .option(Option.<Color>createBuilder().name(Text.literal("Color"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.color, () -> config.template.color,
                                        newVal -> config.template.color = newVal)
                                .controller(
                                        opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())

                        // TODO: List
                        .option(ListOption.<String>createBuilder().name(Text.literal("List"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(Taraton.MOD_ID,
                                                "config/template/placeholder.webp"))
                                        .text(Text.literal("Description")).build())
                                .binding(defaults.template.list, () -> config.template.list,
                                        newVal -> config.template.list = newVal)
                                .controller(StringControllerBuilder::create).build())

                        .build())

                .build();
    }

    // Option Group
    @SerialEntry
    public boolean bool = false;

    @SerialEntry
    public int integer = 0;

    @SerialEntry
    public String string = "default";

    @SerialEntry
    public Imu imu = Imu.THE;

    public enum Imu implements NameableEnum {
        THE, EMPTY, THRONE;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case THE -> Text.literal("The");
                case EMPTY -> Text.literal("Empty");
                case THRONE -> Text.literal("Throne");
            };
        }
    }

    @SerialEntry
    public Color color = new Color(0, 0, 0, 255);

    @SerialEntry
    public List<String> list = new ArrayList<>();
}
