package net.volcaronitee.volcclient.feature.general;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.volcaronitee.volcclient.util.ConfigUtil;

/**
 * Feature to remove the selfie mode by toggling perspective.
 */
public class RemoveSelfieMode {
    private static final RemoveSelfieMode INSTANCE = new RemoveSelfieMode();

    /**
     * Private constructor to prevent instantiation.
     */
    private RemoveSelfieMode() {}

    /**
     * Registers the RemoveSelfieMode feature.
     */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::onClientTick);
    }

    /**
     * Handles the client tick event to toggle perspective
     * 
     * @param client The Minecraft client instance.
     */
    private void onClientTick(MinecraftClient client) {
        GameOptions options = client.options;
        if (!ConfigUtil.getHandler().general.removeSelfieMode || options == null)
            return;

        if (options.togglePerspectiveKey.isPressed()) {
            Perspective current = options.getPerspective();
            if (current == Perspective.THIRD_PERSON_FRONT) {
                options.setPerspective(Perspective.FIRST_PERSON);
            }
        }
    }
}
