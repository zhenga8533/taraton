package net.volcaronitee.volcclient.feature.combat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ListUtil;

public class EntityHighlight {
    private static final EntityHighlight INSTANCE = new EntityHighlight();

    public static final ListUtil ENTITY_LIST = new ListUtil("Entity List",
            Text.literal("A list of entities to highlight in the game."),
            "entity_list.json");

    private static final Set<String> HIGHLIGHT_ENTITIES = new HashSet<>();
    private static final Map<String, Integer> HIGHLIGHT_COLORS = new HashMap<>();

    static {
        ENTITY_LIST.setSaveCallback(INSTANCE::onSave);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private EntityHighlight() {
    }

    /**
     * Gets the singleton instance of HighlightEntity.
     *
     * @return The singleton instance.
     */
    public static EntityHighlight getInstance() {
        return INSTANCE;
    }

    /**
     * Checks if the entity should glow based on the highlight list.
     * 
     * @param entity The entity to check.
     * @return True if the entity should glow, false otherwise.
     */
    public boolean shouldGlow(Entity entity) {
        if (!ConfigUtil.getHandler().combat.entityHighlight) {
            return false;
        }

        return HIGHLIGHT_ENTITIES.contains(entity.getType().getName().getString());
    }

    /**
     * Gets the color for the entity based on its name.
     * 
     * @param name The name of the entity type.
     * @return The color as an integer.
     */
    public int getColor(Entity entity) {
        String name = entity.getType().getName().getString();
        return HIGHLIGHT_COLORS.getOrDefault(name, setColor(name));
    }

    /**
     * Gets the color for the entity based on its type name.
     * 
     * @param name The name of the entity type.
     * @return The color as an integer.
     */
    private int setColor(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(name.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashText = no.toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            String truncatedHex = hashText.substring(0, 6);
            return Integer.parseInt(truncatedHex, 16);
        } catch (NoSuchAlgorithmException e) {
            return 0xFFFFFF;
        }
    }

    /**
     * Saves the list of entities to highlight based on the current configuration.
     */
    private void onSave() {
        HIGHLIGHT_ENTITIES.clear();
        HIGHLIGHT_COLORS.clear();

        ENTITY_LIST.getHandler().list.forEach(pair -> {
            // Skip if the entity is not enabled
            if (!pair.getValue()) {
                return;
            }

            String key = pair.getKey();
            HIGHLIGHT_ENTITIES.add(key);
            HIGHLIGHT_COLORS.put(key, setColor(key));
        });
    }
}
