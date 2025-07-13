package net.volcaronitee.nar.feature.general;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.volcaronitee.nar.NotARat;
import net.volcaronitee.nar.config.NarJson;
import net.volcaronitee.nar.mixin.accessor.HandledScreenAccessor;

public class DeveloperKey {
    private static final DeveloperKey INSTANCE = new DeveloperKey();

    private static KeyBinding devKeybind;

    /**
     * Private constructor to prevent instantiation.
     */
    private DeveloperKey() {}

    /**
     * Registers the developer keybinding.
     */
    public static void register() {
        devKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Dev Key",
                InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "Not A Rat"));
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE::onEndTick);
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register(INSTANCE::onScreenKey);
        });
    }

    /**
     * Copies the given NBT data to the clipboard and notifies the player.
     * 
     * @param client The Minecraft client instance.
     * @param nbt The NBT data to copy.
     * @param dataType The type of data being copied (for message purposes).
     */
    private void copyNbtToClipboard(MinecraftClient client, NbtElement nbt, String dataType) {
        if (client.player == null) {
            return;
        }

        try {
            // Convert NBT to JSON for better readability
            JsonElement jsonElement = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt);
            String formattedJson = NarJson.GSON.toJson(jsonElement);
            client.keyboard.setClipboard(formattedJson);

            NotARat.sendMessage(Text.literal("Copied " + dataType + " data to clipboard.")
                    .formatted(Formatting.GREEN));
        } catch (Exception e) {
            NotARat.sendMessage(Text.literal("Failed to copy " + dataType + " data to clipboard.")
                    .formatted(Formatting.RED));
            e.printStackTrace();
        }
    }

    /**
     * Reflectively extracts data from the given entity and adds it to the provided NBT compound.
     * 
     * @param nbt The NBT compound to populate with entity data.
     * @param entity The entity from which to extract data.
     */
    private void reflectLiveData(NbtCompound nbt, Entity entity) {
        NbtCompound liveData = new NbtCompound();
        Class<?> currentClass = entity.getClass();

        // Traverse the class hierarchy up to the base Entity class
        while (currentClass != null && Entity.class.isAssignableFrom(currentClass)) {
            for (Method method : currentClass.getDeclaredMethods()) {
                // We are looking for public getters with no parameters
                if (!Modifier.isPublic(method.getModifiers()) || method.getParameterCount() != 0) {
                    continue;
                }

                String methodName = method.getName();
                if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
                    continue;
                }

                try {
                    // Make the method accessible in case it's not public in a superclass
                    method.setAccessible(true);
                    Object value = method.invoke(entity);

                    // Add the value to the NBT based on its type
                    String key = methodName.startsWith("get") ? methodName.substring(3)
                            : methodName.substring(2);
                    key = Character.toLowerCase(key.charAt(0)) + key.substring(1);

                    if (value instanceof Boolean)
                        liveData.putBoolean(key, (Boolean) value);
                    else if (value instanceof Float)
                        liveData.putFloat(key, (Float) value);
                    else if (value instanceof Double)
                        liveData.putDouble(key, (Double) value);
                    else if (value instanceof Integer)
                        liveData.putInt(key, (Integer) value);
                    else if (value instanceof Long)
                        liveData.putLong(key, (Long) value);
                    else if (value instanceof Short)
                        liveData.putShort(key, (Short) value);
                    else if (value instanceof Byte)
                        liveData.putByte(key, (Byte) value);
                    else if (value instanceof String)
                        liveData.putString(key, (String) value);

                } catch (Exception e) {
                    // Ignore methods that fail to invoke (e.g., due to wrong context)
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        nbt.put("liveData", liveData);
    }

    /**
     * Handles end tick events to check for key presses in-game.
     * 
     * @param client The Minecraft client instance.
     */
    private void onEndTick(MinecraftClient client) {
        while (devKeybind.wasPressed()) {
            if (client.player == null || client.world == null) {
                return;
            }

            // Check what the player is looking at
            HitResult crosshairTarget = client.crosshairTarget;
            if (crosshairTarget == null || crosshairTarget.getType() == HitResult.Type.MISS) {
                return;
            }

            RegistryWrapper.WrapperLookup registries = client.world.getRegistryManager();

            if (crosshairTarget.getType() == HitResult.Type.ENTITY) {
                // Player is looking at entity
                Entity entity = ((EntityHitResult) crosshairTarget).getEntity();
                NbtCompound entityNbt = entity.writeNbt(new NbtCompound());
                reflectLiveData(entityNbt, entity);
                copyNbtToClipboard(client, entityNbt, "Entity");
            } else if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
                // Player is looking at block
                BlockPos blockPos = ((BlockHitResult) crosshairTarget).getBlockPos();
                BlockEntity blockEntity = client.world.getBlockEntity(blockPos);

                if (blockEntity != null) {
                    // If the block has a BlockEntity, copy its data
                    NbtCompound blockEntityNbt = blockEntity.createNbt(registries);
                    copyNbtToClipboard(client, blockEntityNbt, "Block Entity");
                } else {
                    // Otherwise, copy the BlockState data
                    BlockState blockState = client.world.getBlockState(blockPos);
                    Optional<JsonElement> result =
                            BlockState.CODEC.encodeStart(JsonOps.INSTANCE, blockState).result();

                    if (result.isPresent()) {
                        String formattedJson = NarJson.GSON.toJson(result.get());
                        client.keyboard.setClipboard(formattedJson);
                        NotARat.sendMessage(Text.literal("Copied Block State data to clipboard.")
                                .formatted(Formatting.GREEN));
                    } else {
                        NotARat.sendMessage(
                                Text.literal("Failed to copy Block State data to clipboard.")
                                        .formatted(Formatting.RED));
                    }
                }
            }
        }
    }

    /**
     * Handles key press events in screens.
     * 
     * @param screen The current screen.
     * @param keyCode The key code of the pressed key.
     * @param scanCode The scan code of the pressed key.
     * @param modifiers The modifier keys pressed.
     */
    private void onScreenKey(Screen screen, int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!devKeybind.matchesKey(keyCode, scanCode) || client == null || client.player == null
                || client.currentScreen == null || client.world == null) {
            return;
        }

        ItemStack hoveredStack = ItemStack.EMPTY;

        // Get the hovered item stack if in a handled screen
        if (client.currentScreen instanceof HandledScreen) {
            HandledScreenAccessor screenAccessor = (HandledScreenAccessor) client.currentScreen;
            if (screenAccessor.getFocusedSlot() != null) {
                hoveredStack = screenAccessor.getFocusedSlot().getStack();
            }
        }

        // If no valid item is hovered, do nothing
        if (hoveredStack.isEmpty()) {
            NotARat.sendMessage(Text.literal("No item hovered.").formatted(Formatting.RED));
            return;
        }

        // Copy the item NBT data to clipboard
        RegistryWrapper.WrapperLookup registries = client.world.getRegistryManager();
        NbtElement itemDataNbt = hoveredStack.toNbt(registries);
        copyNbtToClipboard(client, itemDataNbt, "Item");
    }
}
