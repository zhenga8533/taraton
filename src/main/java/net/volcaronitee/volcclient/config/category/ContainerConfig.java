package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;

public class ContainerConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Container"))

                // Container Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Container"))

                        // Container Preview
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Container Preview"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Displays a preview of a backpack or ender chest when hovered."))
                                        .build())
                                .binding(defaults.container.containerPreview,
                                        () -> config.container.containerPreview,
                                        newVal -> config.container.containerPreview = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Searchbar
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Searchbar"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Adds a search bar to container inventories."))
                                        .build())
                                .binding(defaults.container.searchbar,
                                        () -> config.container.searchbar,
                                        newVal -> config.container.searchbar = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Container Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Hotkey"))

                        // Container Buttons
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Container Buttons"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Creates clickable hotkey buttons for container inventories. Set buttons using /vc buttons."))
                                        .build())
                                .binding(defaults.container.containerButtons,
                                        () -> config.container.containerButtons,
                                        newVal -> config.container.containerButtons = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Slot Binding
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Slot Binding"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Allows you to bind inventory slots to one another. Ctrl + LC to swap binded slots. Set bindings using /vc bind."))
                                        .build())
                                .binding(defaults.container.slotBinding,
                                        () -> config.container.slotBinding,
                                        newVal -> config.container.slotBinding = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Wardrobe Swap
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Wardrobe Swap"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Allows you to fast swap armor in the wardrobe using keybinds. Set keybinds using /vc wardrobe."))
                                        .build())
                                .binding(defaults.container.wardrobeSwap,
                                        () -> config.container.wardrobeSwap,
                                        newVal -> config.container.wardrobeSwap = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Items Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Items"))

                        // Attribute Abbreviation
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Attribute Abbreviation"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Displays attribute abbreviations directly over items."))
                                        .build())
                                .binding(defaults.container.attributeAbbreviation,
                                        () -> config.container.attributeAbbreviation,
                                        newVal -> config.container.attributeAbbreviation = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Armor Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Armor Display"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Displays current armor set on the screen."))
                                        .build())
                                .binding(defaults.container.armorDisplay,
                                        () -> config.container.armorDisplay,
                                        newVal -> config.container.armorDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Equipment Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Equipment Display"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal(
                                                "Displays current equipment gear on the screen."))
                                        .build())
                                .binding(defaults.container.equipmentDisplay,
                                        () -> config.container.equipmentDisplay,
                                        newVal -> config.container.equipmentDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // Max Supercraft
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Max Supercraft"))
                                .description(OptionDescription.createBuilder().text(Text.literal(
                                        "Displays the maximum supercraft quantity in the crafting description."))
                                        .build())
                                .binding(defaults.container.maxSupercraft,
                                        () -> config.container.maxSupercraft,
                                        newVal -> config.container.maxSupercraft = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                .build();
    }

    // Container Option Group
    @SerialEntry
    public boolean containerPreview = false;

    @SerialEntry
    public boolean searchbar = false;

    // Hotkey Option Group
    @SerialEntry
    public boolean containerButtons = false;

    @SerialEntry
    public boolean slotBinding = false;

    @SerialEntry
    public boolean wardrobeSwap = false;

    // Items Option Group
    @SerialEntry
    public boolean attributeAbbreviation = false;

    @SerialEntry
    public boolean armorDisplay = false;

    @SerialEntry
    public boolean equipmentDisplay = false;

    @SerialEntry
    public boolean maxSupercraft = true;
}
