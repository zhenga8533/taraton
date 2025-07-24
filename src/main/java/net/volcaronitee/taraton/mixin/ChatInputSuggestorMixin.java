package net.volcaronitee.taraton.mixin;

import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.volcaronitee.taraton.feature.chat.AutoCommand;

/**
 * Mixin to enhance the chat input suggestor with custom command suggestions.
 */
@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {
    @Shadow
    @Final
    private TextFieldWidget textField;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    /**
     * Injects into the refresh method to add custom, frequently-used command suggestions.
     */
    @Inject(method = "refresh", at = @At(value = "INVOKE",
            target = "Lcom/mojang/brigadier/CommandDispatcher;getCompletionSuggestions(Lcom/mojang/brigadier/ParseResults;I)Ljava/util/concurrent/CompletableFuture;",
            shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addCustomSuggestions(CallbackInfo ci) {
        // Only add suggestions if the input starts with a slash.
        String input = this.textField.getText();
        if (input.startsWith("/")) {
            // Get the original suggestions future.
            CompletableFuture<Suggestions> originalSuggestionsFuture = this.pendingSuggestions;

            // Create a new future that calls our logic in AutoCommand to combine suggestions.
            this.pendingSuggestions =
                    originalSuggestionsFuture
                            .thenApplyAsync(
                                    originalSuggestions -> AutoCommand.combineSuggestions(input,
                                            originalSuggestions),
                                    MinecraftClient.getInstance()::execute);
        }
    }
}
