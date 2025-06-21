package net.volcaronitee.volcclient.mixin;

import java.util.concurrent.atomic.AtomicReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
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

        StringBuilder currentTextBuilder = new StringBuilder();
        AtomicReference<Style> currentStyleRef = new AtomicReference<>();

        // Use the visitor to reconstruct the text with substitutions applied
        orderedText.accept((index, style, codePoint) -> {
            if (currentStyleRef.get() == null) {
                currentStyleRef.set(style);
            } else if (!style.equals(currentStyleRef.get())) {
                if (currentTextBuilder.length() > 0) {
                    Text segmentToModify = Text.literal(currentTextBuilder.toString())
                            .setStyle(currentStyleRef.get());
                    Text modified = TextSubstitution.modify(segmentToModify);
                    reconstructed.append(modified);
                }
                currentTextBuilder.setLength(0);
                currentStyleRef.set(style);
            }

            currentTextBuilder.append(Character.toChars(codePoint));

            return true;
        });

        // If there's any remaining text to process after the loop
        if (currentTextBuilder.length() > 0) {
            Text segmentToModify =
                    Text.literal(currentTextBuilder.toString()).setStyle(currentStyleRef.get());
            Text modified = TextSubstitution.modify(segmentToModify);
            reconstructed.append(modified);
        }

        return reconstructed.asOrderedText().accept(visitor);
    }
}
