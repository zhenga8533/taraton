package net.volcaronitee.taraton.feature.fishing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.volcaronitee.taraton.config.TaratonConfig;
import net.volcaronitee.taraton.util.OverlayUtil;
import net.volcaronitee.taraton.util.OverlayUtil.LineContent;
import net.volcaronitee.taraton.util.ScheduleUtil;
import net.volcaronitee.taraton.util.TickUtil;
import net.volcaronitee.taraton.util.helper.Contract;

/**
 * Feature that simulates fishing by automatically casting and recasting the fishing rod when the
 * player is near a specific armor stand entity.
 */
public class HookLineAndSinker {
    private static final List<LineContent> LINES =
            new ArrayList<>(List.of(new LineContent("§c§l!!!", () -> true)));

    static {
        OverlayUtil.createOverlay("hook_line_and_sinker",
                () -> TaratonConfig.getHandler().fishing.hookLineAndSinker, LINES);
        LINES.clear();
    }

    private static final HookLineAndSinker INSTANCE = new HookLineAndSinker();

    @SuppressWarnings("unchecked")
    private static final EntityType<Entity> ARMOR_STAND =
            (EntityType<Entity>) Registries.ENTITY_TYPE.get(Identifier.of("minecraft:armor_stand"));

    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d(\\.\\d+)?$");

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

    /**
     * Handles the tick event for the fishing feature.
     * 
     * @param client The Minecraft client instance.
     */
    private void onTick(MinecraftClient client) {
        // Clear lines if not hooked to avoid stale data
        if (!INSTANCE.hooked) {
            LINES.clear();
        }

        // Check if the feature is enabled and the player is in a valid state
        if (!TaratonConfig.getHandler().fishing.hookLineAndSinker || client.player == null
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

        // Loop through all armor stands in the sensing box
        for (Entity armorStand : armorStands) {
            Text name = armorStand.getName();

            // Check if the armor stand has a valid name format
            boolean validFormat = false;
            for (Text sibling : name.getSiblings()) {
                Style siblingStyle = sibling.getStyle();
                TextColor textColor = siblingStyle.getColor();
                if (textColor == null) {
                    continue;
                }

                if ((textColor.equals(TextColor.fromFormatting(Formatting.RED))
                        || textColor.equals(TextColor.fromFormatting(Formatting.YELLOW)))
                        && siblingStyle.isBold()) {
                    validFormat = true;
                }
            }
            if (!validFormat) {
                continue;
            }

            // Check if the name matches the time pattern or is "!!!"
            if (TIME_PATTERN.matcher(name.getString()).matches()) {
                LINES.add(new LineContent(name, () -> true));
                continue;
            }
            if (!name.getString().equals("!!!")) {
                continue;
            }
            LINES.add(new LineContent(name, () -> true));

            // Check if the use key is pressed and if a contract is signed
            KeyBinding useKey = MinecraftClient.getInstance().options.useKey;
            if (useKey == null || useKey.isPressed() || !Contract.isSigned()) {
                break;
            }
            INSTANCE.hooked = true;

            int hookDelay = (int) (Math.random() * 3 + 1);
            int fishDelay = (int) (Math.random() * 2 + 5);

            // Interact with the fishing rod
            ScheduleUtil.schedule(() -> {
                useKey.setPressed(true);
            }, hookDelay);
            ScheduleUtil.schedule(() -> {
                useKey.setPressed(false);
            }, hookDelay + 1);

            // Wait and recast the fishing rod
            ScheduleUtil.schedule(() -> {
                useKey.setPressed(true);
            }, hookDelay + fishDelay);
            ScheduleUtil.schedule(() -> {
                INSTANCE.hooked = false;
                useKey.setPressed(false);
            }, hookDelay + fishDelay + 1);
            break;
        }
    }
}
