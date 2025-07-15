package net.volcaronitee.taraton.feature.general;

import org.joml.Quaternionf;
import net.minecraft.client.util.math.MatrixStack;
import net.volcaronitee.taraton.config.TaratonConfig;

/**
 * Feature to apply custom transformations to held item rendering.
 */
public class HeldItemTransformer {
    public static final HeldItemTransformer INSTANCE = new HeldItemTransformer();

    /**
     * Private constructor to prevent instantiation.
     */
    private HeldItemTransformer() {}

    /**
     * Applies custom transformations to the held item rendering.
     * 
     * @param matrices The matrix stack to apply transformations to.
     */
    public void applyTransformations(MatrixStack matrices) {
        // Rotation
        float rotationX =
                (float) Math.toRadians(TaratonConfig.getHandler().general.heldItemRotationX);
        float rotationY =
                (float) Math.toRadians(TaratonConfig.getHandler().general.heldItemRotationY);
        float rotationZ =
                (float) Math.toRadians(TaratonConfig.getHandler().general.heldItemRotationZ);
        matrices.multiply(new Quaternionf().rotateXYZ(rotationX, rotationY, rotationZ));

        // Scale
        float scale = (float) TaratonConfig.getHandler().general.heldItemScale;
        matrices.scale(scale, scale, scale);
    }
}
