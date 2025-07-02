package net.volcaronitee.nar.util.render;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class BeaconBeam {

    private static final Identifier BEACON_BEAM_TEXTURE =
            Identifier.of("minecraft", "textures/entity/beacon_beam.png");

    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            double entityX, double entityY, double entityZ, float partialTicks, int color,
            float height) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null)
            return;
        long worldTime = client.world.getTime();

        float g = (float) entityY * 0.01F;
        float h = (float) (worldTime % 100L) / 100.0F + g;

        VertexConsumer buffer =
                vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(BEACON_BEAM_TEXTURE, false));

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float gb = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float alpha = 0.5F;

        matrices.push();
        matrices.translate(entityX, entityY, entityZ);

        float beamWidth = 0.25F;

        drawBeamQuad(matrices, buffer, r, gb, b, alpha, BEACON_BEAM_TEXTURE, h, height, beamWidth,
                new Vector3f(0.0F, 0.0F, 1.0F));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

        drawBeamQuad(matrices, buffer, r, gb, b, alpha, BEACON_BEAM_TEXTURE, h, height, beamWidth,
                new Vector3f(0.0F, 0.0F, 1.0F));

        matrices.pop();
    }


    private static void drawBeamQuad(MatrixStack matrices, VertexConsumer buffer, float r, float g,
            float b, float a, Identifier texture, float vStart, float beamHeight, float width,
            Vector3f faceNormal) {
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
        Vector3f transformedNormal = normalMatrix.transform(faceNormal);

        buffer.vertex(matrices.peek().getPositionMatrix(), -width / 2, 0, width / 2)
                .color(r, g, b, a).texture(0.0F, vStart + beamHeight).overlay(0).light(15728880)
                .normal(transformedNormal.x(), transformedNormal.y(), transformedNormal.z());

        buffer.vertex(matrices.peek().getPositionMatrix(), -width / 2, beamHeight, width / 2)
                .color(r, g, b, a).texture(0.0F, vStart).overlay(0).light(15728880)
                .normal(transformedNormal.x(), transformedNormal.y(), transformedNormal.z());

        buffer.vertex(matrices.peek().getPositionMatrix(), width / 2, beamHeight, width / 2)
                .color(r, g, b, a).texture(1.0F, vStart).overlay(0).light(15728880)
                .normal(transformedNormal.x(), transformedNormal.y(), transformedNormal.z());

        buffer.vertex(matrices.peek().getPositionMatrix(), width / 2, 0, width / 2)
                .color(r, g, b, a).texture(1.0F, vStart + beamHeight).overlay(0).light(15728880)
                .normal(transformedNormal.x(), transformedNormal.y(), transformedNormal.z());
    }
}
