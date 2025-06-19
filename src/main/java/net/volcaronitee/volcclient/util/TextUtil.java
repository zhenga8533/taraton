package net.volcaronitee.volcclient.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Utility class for text formatting and constants.
 */
public class TextUtil {
    public static final Text MOD_TITLE =
            Text.empty().append("[").formatted(Formatting.DARK_GRAY).append("Volc Client")
                    .formatted(Formatting.GOLD).append("]").formatted(Formatting.DARK_GRAY);

    /**
     * Private constructor to prevent instantiation.
     */
    private TextUtil() {}
}
