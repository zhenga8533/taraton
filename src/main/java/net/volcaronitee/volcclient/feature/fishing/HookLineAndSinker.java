package net.volcaronitee.volcclient.feature.fishing;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ScheduleUtil;
import net.volcaronitee.volcclient.util.TickUtil;

public class HookLineAndSinker {
    private static final HookLineAndSinker INSTANCE = new HookLineAndSinker();

    @SuppressWarnings("unchecked")
    private static final EntityType<Entity> ARMOR_STAND =
            (EntityType<Entity>) Registries.ENTITY_TYPE.get(Identifier.of("minecraft:armor_stand"));

    private boolean hooked = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private HookLineAndSinker() {}

    /**
     * Returns the singleton instance of HookLineAndSinker.
     * 
     * @return the singleton instance of HookLineAndSinker
     */
    public static HookLineAndSinker getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the fishing feature to the TickUtil.
     */
    public static void register() {
        TickUtil.register(INSTANCE::onTick, 2);
    }

    private void onTick(MinecraftClient client) {
        if (!ConfigUtil.getHandler().fishing.hookLineAndSinker || client.player == null
                || client.world == null || INSTANCE.hooked) {
            return;
        }

        // Check if the player is holding a fishing rod
        Item heldItem = client.player.getMainHandStack().getItem();
        String itemId = Registries.ITEM.getId(heldItem).toString();
        if (!itemId.equals("minecraft:fishing_rod")) {
            return;
        }

        // Create a sensing box around the player
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();
        Box senseBox = new Box(x - 16, y - 16, z - 16, x + 16, y + 16, z + 16);
        List<Entity> armorStands =
                client.world.getEntitiesByType(ARMOR_STAND, senseBox, entity -> true);

        for (Entity armorStand : armorStands) {
            String name = armorStand.getName().getString();
            if (!name.equals("!!!")) {
                continue;
            }

            INSTANCE.hooked = true;
            int hookDelay = (int) (Math.random() * 8 + 1);
            int fishDelay = (int) (Math.random() * 4 + 1);

            // Interact with the fishing rod
            ScheduleUtil.schedule(() -> {
                MinecraftClient.getInstance().options.useKey.setPressed(true);
            }, hookDelay);
            ScheduleUtil.schedule(() -> {
                MinecraftClient.getInstance().options.useKey.setPressed(false);
            }, hookDelay + 1);

            // Wait and recast the fishing rod
            ScheduleUtil.schedule(() -> {
                MinecraftClient.getInstance().options.useKey.setPressed(true);
            }, hookDelay + fishDelay);
            ScheduleUtil.schedule(() -> {
                MinecraftClient.getInstance().options.useKey.setPressed(false);
                INSTANCE.hooked = false;
            }, hookDelay + fishDelay + 1);
        }
    }
}
