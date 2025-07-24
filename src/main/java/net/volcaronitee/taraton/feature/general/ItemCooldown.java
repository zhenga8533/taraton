package net.volcaronitee.taraton.feature.general;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.FeatureUtil;
import net.volcaronitee.taraton.util.ParseUtil;

public class ItemCooldown {
    private static final ItemCooldown INSTANCE = new ItemCooldown();

    public static final TaratonList COOLDOWN_MAP = new TaratonList("Cooldown Map",
            Text.literal("A list of item cooldowns for the player."), "cooldown_map.json",
            new String[] {"UUID", "Cooldown"});

    private Map<String, Long> cooldowns = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ItemCooldown() {}

    /**
     * Registers the item cooldown feature to listen for item use events and apply cooldowns.
     */
    public static void register() {
        UseItemCallback.EVENT.register(INSTANCE::onUseItem);

        // Clear cooldowns on world change
        ClientPlayConnectionEvents.DISCONNECT
                .register((handler, client) -> INSTANCE.cooldowns.clear());
    }

    /**
     * Returns the singleton instance of ItemCooldown.
     * 
     * @return The singleton instance of ItemCooldown.
     */
    public static ItemCooldown getInstance() {
        return INSTANCE;
    }

    /**
     * Renders the cooldown overlay for an item stack at the specified position.
     * 
     * @param context The DrawContext used for rendering.
     * @param stack The ItemStack for which to render the cooldown.
     * @param x The x-coordinate for rendering the cooldown overlay.
     * @param y The y-coordinate for rendering the cooldown overlay.
     */
    public void renderCooldown(DrawContext context, ItemStack stack, int x, int y) {
        if (!FeatureUtil.isEnabled(TaratonConfig.getInstance().general.itemCooldown)
                || stack.isEmpty()) {
            return;
        }

        // Check if the item has a cooldown defined in the map
        String itemUuid = ParseUtil.getItemUuid(stack);
        if (itemUuid == null || !cooldowns.containsKey(itemUuid)) {
            return;
        }

        // Get the cooldown end time and calculate remaining time
        long endTime = cooldowns.get(itemUuid);
        long currentTime = System.currentTimeMillis();

        if (currentTime >= endTime) {
            cooldowns.remove(itemUuid);
            return;
        }

        long totalCooldown = ParseUtil.parseTime(COOLDOWN_MAP.map.get(itemUuid)) * 1000;
        if (totalCooldown <= 0)
            return;

        long remainingTime = endTime - currentTime;
        float progress = (float) remainingTime / totalCooldown;

        // Render the cooldown overlay
        int overlayHeight = MathHelper.ceil(16.0F * progress);
        context.fill(RenderLayer.getGuiOverlay(), x, y + 16 - overlayHeight, x + 16, y + 16,
                0x80FFFFFF);
    }

    /**
     * Callback method that is called when a player uses an item.
     * 
     * @param player The player using the item.
     * @param world The world in which the item is used.
     * @param hand The hand in which the item is held.
     * @return ActionResult indicating the result of the item use.
     */
    private ActionResult onUseItem(PlayerEntity player, World world, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()
                || !FeatureUtil.isEnabled(TaratonConfig.getInstance().general.itemCooldown)) {
            return ActionResult.PASS;
        }

        // Check if the item has a cooldown defined in the map
        String itemUuid = ParseUtil.getItemUuid(itemStack);
        if (itemUuid == null || !COOLDOWN_MAP.map.containsKey(itemUuid)) {
            return ActionResult.PASS;
        }

        // Apply cooldown if it exists
        long cooldown = ParseUtil.parseTime(COOLDOWN_MAP.map.get(itemUuid)) * 1000;
        cooldowns.put(itemUuid, System.currentTimeMillis() + cooldown);

        return ActionResult.PASS;
    }
}
