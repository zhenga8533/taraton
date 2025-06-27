package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;

/**
 * Configuration for the Container features in VolcClient.
 */
public class ContainerConfig {
    /**
     * Creates a new {@link ConfigCategory} for the Container features.
     * 
     * @param defaults The default configuration values.
     * @param config The current configuration values.
     * @return A new {@link ConfigCategory} for the Container features.
     */
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Container"))

                // Container Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Container"))

                        // TODO: Container Preview
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Container Preview"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/container_preview.webp"))
                                        .text(Text.literal(
                                                "Displays a preview of a backpack or ender chest when hovered."))
                                        .build())
                                .binding(defaults.container.containerPreview,
                                        () -> config.container.containerPreview,
                                        newVal -> config.container.containerPreview = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Searchbar
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Searchbar"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/searchbar.webp"))
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

                        // TODO: Container Buttons
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Container Buttons"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/container_buttons.webp"))
                                        .text(Text.literal(
                                                "Creates clickable hotkey buttons for container inventories. Set buttons using /vc buttons."))
                                        .build())
                                .binding(defaults.container.containerButtons,
                                        () -> config.container.containerButtons,
                                        newVal -> config.container.containerButtons = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Slot Binding
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Slot Binding"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/slot_binding.webp"))
                                        .text(Text.literal(
                                                "Allows you to bind inventory slots to one another. Ctrl + LC to swap binded slots. Set bindings using /vc bind."))
                                        .build())
                                .binding(defaults.container.slotBinding,
                                        () -> config.container.slotBinding,
                                        newVal -> config.container.slotBinding = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Wardrobe Swap
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Wardrobe Swap"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/wardrobe_swap.webp"))
                                        .text(Text.literal(
                                                "Allows you to fast swap armor in the wardrobe using keybinds. Set keybinds using /vc wardrobe."))
                                        .build())
                                .binding(defaults.container.wardrobeSwap,
                                        () -> config.container.wardrobeSwap,
                                        newVal -> config.container.wardrobeSwap = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Items Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Items"))

                        // TODO: Attribute Abbreviation
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Attribute Abbreviation"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/attribute_abbreviation.webp"))
                                        .text(Text.literal(
                                                "Displays attribute abbreviations directly over items."))
                                        .build())
                                .binding(defaults.container.attributeAbbreviation,
                                        () -> config.container.attributeAbbreviation,
                                        newVal -> config.container.attributeAbbreviation = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Armor Display
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Armor Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/armor_display.webp"))
                                        .text(Text.literal(
                                                "Displays current armor set on the screen."))
                                        .build())
                                .binding(defaults.container.armorDisplay,
                                        () -> config.container.armorDisplay,
                                        newVal -> config.container.armorDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Equipment Display
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Equipment Display"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/equipment_display.webp"))
                                        .text(Text.literal(
                                                "Displays current equipment gear on the screen."))
                                        .build())
                                .binding(defaults.container.equipmentDisplay,
                                        () -> config.container.equipmentDisplay,
                                        newVal -> config.container.equipmentDisplay = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Max Supercraft
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Max Supercraft"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/container/max_supercraft.webp"))
                                        .text(Text.literal(
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
    public boolean containerButtons = true;

    @SerialEntry
    public boolean slotBinding = true;

    @SerialEntry
    public boolean wardrobeSwap = true;

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
