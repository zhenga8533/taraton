package net.volcaronitee.volcclient.config.category;

import java.awt.Color;
import dev.isxander.yacl3.api.ConfigCategory;
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
import net.volcaronitee.volcclient.util.ConfigUtil;

public class TemplateConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Category"))

                // Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Group"))

                        // Boolean
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Boolean"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.bool, () -> config.template.bool,
                                        newVal -> config.template.bool = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Boolean With Values
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Boolean With Values"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.bool, () -> config.template.bool,
                                        newVal -> config.template.bool = newVal)
                                .controller(opt -> ConfigUtil.createBooleanController(opt,
                                        "trueString", "falseString"))
                                .build())

                        // Tick Box
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Tick Box"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.bool, () -> config.template.bool,
                                        newVal -> config.template.bool = newVal)
                                .controller(opt -> TickBoxControllerBuilder.create(opt)).build())

                        // Integer Slider
                        .option(Option.<Integer>createBuilder().name(Text.literal("Integer Slider"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.integer, () -> config.template.integer,
                                        newVal -> config.template.integer = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100).step(5))
                                .build())

                        // String
                        .option(Option.<String>createBuilder().name(Text.literal("String"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.string, () -> config.template.string,
                                        newVal -> config.template.string = newVal)
                                .controller(opt -> StringControllerBuilder.create(opt)).build())

                        // Enum
                        .option(Option.<TemplateConfig.Imu>createBuilder()
                                .name(Text.literal("Enum"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.imu, () -> config.template.imu,
                                        newVal -> config.template.imu = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // Color
                        .option(Option.<Color>createBuilder().name(Text.literal("Color"))
                                .description(OptionDescription.of(Text.literal("Description")))
                                .binding(defaults.template.color, () -> config.template.color,
                                        newVal -> config.template.color = newVal)
                                .controller(
                                        opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())

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
}
