package net.volcaronitee.taraton.mixin;

import java.util.concurrent.CompletableFuture;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.volcaronitee.taraton.feature.chat.AutoCommand;

/**
 * Mixin for the ChatInputSuggestor to add custom command suggestions.
 */
@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Shadow
    @Final
    private TextFieldWidget textField;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    /**
     * Injects custom logic into the refresh method of ChatInputSuggestor.
     * 
     * @param ci The callback information for the injection.
     */
    @Inject(method = "refresh", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;pendingSuggestions:Ljava/util/concurrent/CompletableFuture;",
            opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void onRefresh(CallbackInfo ci) {
        String input = this.textField.getText();

        if (input.startsWith("/") && this.pendingSuggestions != null) {
            // Chain our custom logic onto the future that was just assigned by the game.
            this.pendingSuggestions =
                    this.pendingSuggestions
                            .thenApplyAsync(
                                    originalSuggestions -> AutoCommand.combineSuggestions(input,
                                            originalSuggestions),
                                    MinecraftClient.getInstance()::execute);
        }
    }
}
