package net.volcaronitee.volcclient.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void onDrawText(Text text, float x, float y, int color, boolean shadow,
            Matrix4f matrices, VertexConsumerProvider vertexConsumers, int light,
            CallbackInfoReturnable<Integer> cir) {
        String replaced = text.getString().replaceAll("Volcaronitee", "The Lion");

        if (!replaced.equals(text.getString())) {
            Text newText = Text.literal(replaced);
            int result = ((TextRenderer) (Object) this).draw(newText, x, y, color, shadow, matrices,
                    vertexConsumers, TextRenderer.TextLayerType.NORMAL, light, 0);
            cir.setReturnValue(result);
        }
    }
}

