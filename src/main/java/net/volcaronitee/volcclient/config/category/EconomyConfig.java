package net.volcaronitee.volcclient.config.category;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.volcaronitee.volcclient.VolcClient;
import net.volcaronitee.volcclient.util.ConfigUtil;

public class EconomyConfig {
    public static ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal("Economy"))

                // Economy Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Economy"))

                        // TODO: Coin Tracker
                        .option(Option.<Integer>createBuilder().name(Text.literal("Coin Tracker"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/coin_tracker.webp"))
                                        .text(Text.literal(
                                                "Tracks coin purse gain/loss progress on the screen. Sets time in minutes of inactivity before tracking stops."))
                                        .build())
                                .binding(defaults.economy.coinTracker,
                                        () -> config.economy.coinTracker,
                                        newVal -> config.economy.coinTracker = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 10).step(1))
                                .build())

                        // TODO: No Bits Warning
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("No Bits Warning"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/no_bits_warning.webp"))
                                        .text(Text.literal(
                                                "Displays a chat warning and title when bits pool is empty."))
                                        .build())
                                .binding(defaults.economy.noBitsWarning,
                                        () -> config.economy.noBitsWarning,
                                        newVal -> config.economy.noBitsWarning = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        .build())

                // Item Value Option Group
                .group(OptionGroup.createBuilder().name(Text.literal("Item Value"))

                        // TODO: Container Value
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Container Value"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/container_value.webp"))
                                        .text(Text.literal(
                                                "Displays item values in container inventories. Set number of items to display before cutoff."))
                                        .build())
                                .binding(defaults.economy.containerValue,
                                        () -> config.economy.containerValue,
                                        newVal -> config.economy.containerValue = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 54).step(1))
                                .build())

                        // TODO: Item Price
                        .option(Option.<EconomyConfig.ItemPrice>createBuilder()
                                .name(Text.literal("Item Price"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/item_price.webp"))
                                        .text(Text.literal(
                                                "Sets how item prices are displayed in the game."))
                                        .build())
                                .binding(defaults.economy.itemPrice, () -> config.economy.itemPrice,
                                        newVal -> config.economy.itemPrice = newVal)
                                .controller(ConfigUtil::createEnumController).build())

                        // TODO: Single Attribute
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Single Attribute"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/single_attribute.webp"))
                                        .text(Text.literal(
                                                "Displays only a single attribute for item prices."))
                                        .build())
                                .binding(defaults.economy.singleAttribute,
                                        () -> config.economy.singleAttribute,
                                        newVal -> config.economy.singleAttribute = newVal)
                                .controller(ConfigUtil::createBooleanController).build())

                        // TODO: Price Type
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Price Type"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/price_type.webp"))
                                        .text(Text.literal(
                                                "Sets the type of bazaar pricing used in item calculations."))
                                        .build())
                                .binding(defaults.economy.priceType, () -> config.economy.priceType,
                                        newVal -> config.economy.priceType = newVal)
                                .controller(opt -> ConfigUtil.createBooleanController(opt, "Order",
                                        "Insta"))
                                .build())

                        // TODO: Trade Evaluation
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Trade Evaluation"))
                                .description(OptionDescription.createBuilder()
                                        .webpImage(Identifier.of(VolcClient.MOD_ID,
                                                "config/economy/trade_evaluation.webp"))
                                        .text(Text.literal(
                                                "Enables value comparison display when in trade menus."))
                                        .build())
                                .binding(defaults.economy.tradeEvaluation,
                                        () -> config.economy.tradeEvaluation,
                                        newVal -> config.economy.tradeEvaluation = newVal)
                                .controller(opt -> BooleanControllerBuilder.create(opt)).build())

                        .build())

                .build();
    }

    // Economy Option Group
    @SerialEntry
    public int coinTracker = 0;

    @SerialEntry
    public boolean noBitsWarning = true;

    // Item Value Option Group
    @SerialEntry
    public int containerValue = 0;

    @SerialEntry
    public ItemPrice itemPrice = ItemPrice.OFF;

    public enum ItemPrice implements NameableEnum {
        OFF, ADVANCED, TOOLTIP, OMNI;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case OFF -> Text.literal("Disabled");
                case ADVANCED -> Text.literal("Advanced View");
                case TOOLTIP -> Text.literal("Tooltip View");
                case OMNI -> Text.literal("Omnipotent View");
            };
        }
    }

    @SerialEntry
    public boolean singleAttribute = true;

    @SerialEntry
    public boolean priceType = true; // true = Order, false = Insta

    @SerialEntry
    public boolean tradeEvaluation = false;
}
