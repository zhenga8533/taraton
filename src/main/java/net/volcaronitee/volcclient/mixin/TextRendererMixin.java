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
            method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean volcclient$textRendererDrawLayer(OrderedText orderedTextInstance,
            CharacterVisitor visitor) {
        MutableText originalRichText = Text.empty().copy();
        orderedTextInstance.accept((charIndex, style, codePoint) -> {
            originalRichText.append(
                    Text.literal(String.valueOf(Character.toChars(codePoint))).setStyle(style));
            return true;
        });

        Text modifiedText = TextSubstitution.textRenderer$redirectAsOrderedText(originalRichText);
        OrderedText orderedTextToProcess;
        if (!modifiedText.equals(originalRichText)) {
            orderedTextToProcess = modifiedText.asOrderedText();
        } else {
            orderedTextToProcess = orderedTextInstance;
        }

        return orderedTextToProcess.accept(visitor);
    }
}
