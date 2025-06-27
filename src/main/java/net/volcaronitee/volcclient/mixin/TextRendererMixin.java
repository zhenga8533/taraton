package net.volcaronitee.volcclient.mixin;

import java.util.concurrent.atomic.AtomicReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.GlyphDrawable;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.feature.chat.TextSubstitution;

/**
 * Mixin for TextRenderer to modify the OrderedText before rendering.
 */
@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @Unique
    private OrderedText volcclient$modifiedOrderedText = null;

    /**
     * Injects into the prepare method of TextRenderer to modify the OrderedText
     * 
     * @param orderedText The OrderedText to prepare.
     * @param x The x-coordinate for rendering.
     * @param y The y-coordinate for rendering.
     * @param color The color to apply to the text.
     * @param shadow Whether to apply a shadow to the text.
     * @param backgroundColor The background color for the text.
     * @param cir The callback info for returning the GlyphDrawable.
     */
    @Inject(method = "prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;",
            at = @At("HEAD"))
    private void volcclient$textRendererPrepareInject(OrderedText orderedText, float x, float y,
            int color, boolean shadow, int backgroundColor,
            CallbackInfoReturnable<GlyphDrawable> cir) {
        MutableText reconstructed = Text.empty();

        StringBuilder currentTextBuilder = new StringBuilder();
        AtomicReference<Style> currentStyleRef = new AtomicReference<>();

        orderedText.accept((index, style, codePoint) -> {
            if (currentStyleRef.get() == null) {
                currentStyleRef.set(style);
            } else if (!style.equals(currentStyleRef.get())) {
                if (!currentTextBuilder.isEmpty()) {
                    Text segmentToModify = Text.literal(currentTextBuilder.toString())
                            .setStyle(currentStyleRef.get());
                    Text modified = TextSubstitution.getInstance().modify(segmentToModify);
                    reconstructed.append(modified);
                }
                currentTextBuilder.setLength(0);
                currentStyleRef.set(style);
            }

            currentTextBuilder.append(Character.toChars(codePoint));
            return true;
        });

        if (!currentTextBuilder.isEmpty()) {
            Text segmentToModify =
                    Text.literal(currentTextBuilder.toString()).setStyle(currentStyleRef.get());
            Text modified = TextSubstitution.getInstance().modify(segmentToModify);
            reconstructed.append(modified);
        }

        this.volcclient$modifiedOrderedText = reconstructed.asOrderedText();
    }

    /**
     * Redirects the accept method of OrderedText to use the modified OrderedText
     * 
     * @param originalOrderedText The original OrderedText to accept.
     * @param drawer The CharacterVisitor to accept the text.
     * @return True if the text was accepted, false otherwise.
     */
    @Redirect(
            method = "prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean volcclient$textRendererPrepareRedirect(OrderedText originalOrderedText,
            CharacterVisitor drawer) {
        OrderedText textToAccept =
                this.volcclient$modifiedOrderedText != null ? this.volcclient$modifiedOrderedText
                        : originalOrderedText;
        this.volcclient$modifiedOrderedText = null;

        return textToAccept.accept(drawer);
    }
}
