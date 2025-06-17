package net.volcaronitee.volcclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @Redirect(
            method = "draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/text/Text;asOrderedText()Lnet/minecraft/text/OrderedText;"))
    private OrderedText redirectAsOrderedText(Text originalText) {
        String originalString = originalText.getString();
        String replacedString = originalString.replaceAll("Volcaronitee", "The Lion");
        Text newText = Text.literal(replacedString);
        return newText.asOrderedText();
    }
}
