package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.feature.chat.TextSubstitution;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @Redirect(
            method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;"
                    + "Lnet/minecraft/client/render/VertexConsumerProvider;"
                    + "Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean volcclient$redirectOrderedTextAccept(OrderedText orderedText,
            CharacterVisitor visitor) {
        MutableText reconstructed = Text.empty();

        // Rebuild a basic MutableText from OrderedText
        orderedText.accept((index, style, codePoint) -> {
            reconstructed.append(
                    Text.literal(String.valueOf(Character.toChars(codePoint))).setStyle(style));
            return true;
        });

        // Apply your substitution logic
        Text modified = TextSubstitution.modify(reconstructed);

        // Fallback to original if unchanged
        OrderedText finalText =
                modified.equals(reconstructed) ? orderedText : modified.asOrderedText();

        return finalText.accept(visitor);
    }
}
