package net.volcaronitee.nar.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.volcaronitee.nar.feature.chat.TextSubstitution;
import net.volcaronitee.nar.util.ParseUtil;

/**
 * Mixin for TextRenderer to modify the OrderedText before rendering. Adapted for Minecraft 1.21.5
 * (based on provided TextRenderer source).
 */
@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @Unique
    private OrderedText nar$modifiedOrderedText = null;

    /**
     * Injects into the 'drawLayer' method that takes an OrderedText to modify the OrderedText
     * before it's processed.
     *
     * @param text The OrderedText being drawn.
     * @param x The x-coordinate for rendering.
     * @param y The y-coordinate for rendering.
     * @param color The color to apply to the text.
     * @param shadow Whether to apply a shadow to the text.
     * @param matrix The transformation matrix.
     * @param vertexConsumerProvider The vertex consumer provider.
     * @param layerType The text layer type.
     * @param backgroundColor The background color for the text.
     * @param light The light level.
     * @param swapZIndex Whether to swap Z index.
     * @param cir The callback info for returning the float (advance).
     */
    @Inject(method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
            at = @At("HEAD"))
    private void nar$drawLayerInject(OrderedText text, float x, float y, int color, boolean shadow,
            Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider,
            TextRenderer.TextLayerType layerType, int backgroundColor, int light,
            boolean swapZIndex, CallbackInfoReturnable<Float> cir) {
        Text modifiedText = ParseUtil.modifyText(text, TextSubstitution.getInstance()::modify);
        this.nar$modifiedOrderedText = modifiedText.asOrderedText();
    }

    /**
     * Redirects the accept method of OrderedText within the targeted drawLayer method to use the
     * modified OrderedText.
     *
     * @param originalOrderedText The original OrderedText that would have been accepted.
     * @param drawer The CharacterVisitor to accept the text.
     * @return True if the text was accepted, false otherwise.
     */
    @Redirect(
            method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean nar$drawLayerRedirect(OrderedText originalOrderedText,
            CharacterVisitor drawer) {
        OrderedText textToAccept =
                this.nar$modifiedOrderedText != null ? this.nar$modifiedOrderedText
                        : originalOrderedText;
        this.nar$modifiedOrderedText = null;

        return textToAccept.accept(drawer);
    }
}
