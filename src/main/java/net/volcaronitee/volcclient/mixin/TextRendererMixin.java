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

        orderedText.accept((index, style, codePoint) -> {
            String text = String.valueOf(Character.toChars(codePoint));
            Text modified = TextSubstitution.modify(Text.literal(text).setStyle(style));
            reconstructed.append(modified);
            return true;
        });

        return reconstructed.asOrderedText().accept(visitor);
    }
}
