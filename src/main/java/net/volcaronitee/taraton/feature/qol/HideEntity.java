package net.volcaronitee.taraton.feature.qol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.config.TaratonList;
import net.volcaronitee.taraton.util.LocationUtil;

/**
 * Feature to hide entities based on their distance from the main player.
 */
public class HideEntity {
    private static final HideEntity INSTANCE = new HideEntity();

    public static final TaratonList HOW_LIST = new TaratonList("Hide on World",
            Text.literal("A list of worlds where entities will be hidden."), "how_list.json");

    /**
     * Private constructor to prevent instantiation.
     */
    private HideEntity() {}

    /**
     * Returns the singleton instance of HideEntity.
     *
     * @return The singleton instance of HideEntity.
     */
    public static HideEntity getInstance() {
        return INSTANCE;
    }

    /**
     * Checks if an entity should be hidden based on its distance from the main player.
     * 
     * @param entity The entity to check.
     * @return True if the entity should be hidden, false otherwise.
     */
    public boolean shouldHide(Entity entity) {
        if (!HOW_LIST.list.isEmpty() && !HOW_LIST.list.contains(LocationUtil.getWorld().name())) {
            return false;
        }

        return isFarEntity(entity) || isClosePlayer(entity);
    }

    /**
     * Checks if the entity is too far from the main player to be rendered.
     * 
     * @param entity The entity to check.
     * @return True if the entity is too far away, false otherwise.
     */
    public boolean isFarEntity(Entity entity) {
        int maxDistance = TaratonConfig.getHandler().qol.hideFarEntities;
        if (maxDistance == 0) {
            return false;
        }

        // Get player and check if it's null or the same as the entity
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || entity == player) {
            return false;
        }

        // Return true if the entity's squared distance is greater than our maximum
        return player.squaredDistanceTo(entity) > (maxDistance * maxDistance);
    }

    /**
     * Checks if the entity is a player and is within a certain distance from the main player.
     * 
     * @param entity The entity to check.
     * @return True if the entity is a player and is within the specified distance, false otherwise.
     */
    public boolean isClosePlayer(Entity entity) {
        int minDistance = TaratonConfig.getHandler().qol.hideClosePlayers;
        if (minDistance == 0 || !(entity instanceof PlayerEntity)) {
            return false;
        }

        // Get the main player and check if it's null or the same as the entity
        PlayerEntity mainPlayer = MinecraftClient.getInstance().player;
        if (mainPlayer == null || entity == mainPlayer) {
            return false;
        }

        // Return true if the other player's squared distance is less than our minimum
        return mainPlayer.squaredDistanceTo(entity) < (minDistance * minDistance);
    }
}
