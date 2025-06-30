package net.volcaronitee.volcclient.feature.combat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.volcaronitee.volcclient.util.ConfigUtil;
import net.volcaronitee.volcclient.util.ListUtil;
import net.volcaronitee.volcclient.util.OverlayUtil;
import net.volcaronitee.volcclient.util.OverlayUtil.LineContent;
import net.volcaronitee.volcclient.util.TextUtil;
import net.volcaronitee.volcclient.util.TickUtil;

/**
 * Feature that highlights entities in the game based on a configurable list.
 */
public class EntityHighlight {
    private static final EntityHighlight INSTANCE = new EntityHighlight();

    private static final List<LineContent> LINES =
            new ArrayList<>(List.of(new LineContent("§e§lEntity Counter:", "", () -> true),
                    new LineContent(" §fLion: §6", "1", () -> true),
                    new LineContent(" §7Total: §e", "1", () -> true)));

    public static final ListUtil ENTITY_LIST = new ListUtil("Entity List", Text
            .literal("A list of entities to highlight in the game.\n\nUse ")
            .append(TextUtil.getInstance().createLink("digminecraft.com",
                    "https://www.digminecraft.com/lists/entity_list_pc.php"))
            .append(Text.literal(
                    " to find vanilla entity names. If an entity ID is not found, it will be used to identify custom armor stand names.\n\n\n§lOptions:§r\n\n"
                            + " --beacon §7Highlights entities that are within the beacon range.\n"
                            + " --color [hex] §7Sets the color for the entity highlight. Use hex colors like #FF0000.\n")),
            "entity_list.json");

    private static final Map<Entity, Highlight> HIGHLIGHTED_ENTITIES = new HashMap<>();

    private static final Set<Identifier> HIGHLIGHT_ENTITIES = new HashSet<>();
    private static final Map<Identifier, Highlight> ENTITY_HIGHLIGHT = new HashMap<>();

    private static final Set<String> HIGHLIGHT_NAMES = new HashSet<>();
    private static final Map<String, Highlight> NAME_HIGHLIGHT = new HashMap<>();

    private static final EntityType<?> ARMOR_STAND =
            Registries.ENTITY_TYPE.get(Identifier.of("minecraft:armor_stand"));

    static {
        OverlayUtil.createOverlay("entity_counter",
                () -> ConfigUtil.getHandler().combat.entityCounter, LINES);

        ENTITY_LIST.setSaveCallback(INSTANCE::onSave);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private EntityHighlight() {}

    /**
     * Gets the singleton instance of HighlightEntity.
     *
     * @return The singleton instance.
     */
    public static EntityHighlight getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the entity highlight feature to scan the world for entities.
     */
    public static void register() {
        TickUtil.register(INSTANCE::scanWorld, 10);
    }

    /**
     * Scans the world for entities that should be highlighted.
     * 
     * @param client The Minecraft client instance.
     */
    private void scanWorld(MinecraftClient client) {
        HIGHLIGHTED_ENTITIES.clear();
        if (!ConfigUtil.getHandler().combat.entityHighlight || client.world == null) {
            return;
        }

        // Prepare the overlay lines
        LINES.subList(1, LINES.size() - 1).clear();
        Map<String, Integer> entityCount = new HashMap<>();
        AtomicInteger totalCount = new AtomicInteger(0);

        // Loop through all entities in the world
        client.world.getEntities().forEach(entity -> {
            // Get the Identifier for the entity's type
            EntityType<?> entityType = entity.getType();
            Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);
            if (entityId == null) {
                return;
            }

            // Check if the entity ID is in the highlight list
            if (HIGHLIGHT_ENTITIES.contains(entityId)) {
                Highlight highlight = ENTITY_HIGHLIGHT.get(entityId);
                HIGHLIGHTED_ENTITIES.put(entity, highlight);

                entityCount.merge(highlight.name, 1, Integer::sum);
                totalCount.incrementAndGet();
            } else if (entityType == ARMOR_STAND) { // Special case for armor stands
                String customName = entity.getCustomName() != null
                        ? entity.getCustomName().getString().toLowerCase()
                        : "";
                if (customName.isEmpty()) {
                    return;
                }

                // Check if the custom name matches any of the highlight names
                for (String key : HIGHLIGHT_NAMES) {
                    if (customName.contains(key)) {
                        // Add closest entity to the highlight list
                        Box boundingBox = entity.getBoundingBox().expand(0.5, 1, 0.5);
                        List<Entity> nearbyEntities = client.world.getOtherEntities(entity,
                                boundingBox, e -> e.getType() != ARMOR_STAND);

                        if (!nearbyEntities.isEmpty()) {
                            Entity closestEntity = nearbyEntities.get(0);
                            HIGHLIGHTED_ENTITIES.put(closestEntity, NAME_HIGHLIGHT.get(key));

                            entityCount.merge(NAME_HIGHLIGHT.get(key).name, 1, Integer::sum);
                            totalCount.incrementAndGet();
                        }
                    }
                }
            }
        });

        // Update the overlay lines with the entity counts
        for (Map.Entry<String, Integer> entry : entityCount.entrySet()) {
            String entityName = entry.getKey();
            int count = entry.getValue();

            LINES.add(LINES.size() - 1, new LineContent(" §f" + entityName + ": §6",
                    String.valueOf(count), () -> true));
        }
        LINES.getLast().setText(String.valueOf(totalCount.get()));
    }

    /**
     * Checks if the entity should glow based on the highlight list.
     * 
     * @param entity The entity to check.
     * @return True if the entity should glow, false otherwise.
     */
    public boolean getGlow(Entity entity) {
        return HIGHLIGHTED_ENTITIES.containsKey(entity);
    }

    /**
     * Gets the color for the entity based on its identifier.
     * 
     * @param entity The entity to get the color for.
     * @return The color as an integer, or white (0xFFFFFF) if not found.
     */
    public int getColor(Entity entity) {
        if (HIGHLIGHTED_ENTITIES.containsKey(entity)) {
            return HIGHLIGHTED_ENTITIES.get(entity).color;
        }
        return 0xFFFFFF;
    }

    /**
     * Generates a color based on the entity identifier using MD5 hashing.
     * 
     * @param id The identifier of the entity.
     * @return The color as an integer.
     */
    private int setColor(String id) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(id.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashText = no.toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            // Take the first 6 characters for an RGB color
            String truncatedHex = hashText.substring(0, 6);
            return Integer.parseInt(truncatedHex, 16);
        } catch (NoSuchAlgorithmException e) {
            return 0xFFFFFF;
        }
    }

    /**
     * Sets the color for an entity based on its identifier.
     * 
     * @param identifier The identifier of the entity.
     * @return The color as an integer.
     */
    private int setColor(Identifier identifier) {
        return setColor(identifier.toString());
    }

    /**
     * Saves the list of entities to highlight based on the current configuration.
     */
    private void onSave() {
        HIGHLIGHT_ENTITIES.clear();
        ENTITY_HIGHLIGHT.clear();
        HIGHLIGHT_NAMES.clear();
        NAME_HIGHLIGHT.clear();

        ENTITY_LIST.getHandler().list.forEach(pair -> {
            if (!pair.getValue()) {
                return;
            }

            Identifier identifier = null;
            String inputKey = "";
            boolean beacon = false;
            int color = -1;

            // Parse the input key and options
            String[] args = pair.getKey().toLowerCase().trim().split(" --");
            for (String arg : args) {
                if (arg.equals(args[0])) {
                    // The first argument is the identifier or name
                    inputKey = arg.trim();
                    identifier = Identifier.tryParse(inputKey);
                } else if (arg.startsWith("beacon ")) {
                    // This argument indicates a beacon highlight
                    beacon = true;
                } else if (arg.startsWith("color ")) {
                    // This argument sets the color for the highlight
                    String colorHex = arg.substring("color ".length()).trim();
                    try {
                        color = Integer.parseInt(colorHex.replace("#", ""), 16);
                    } catch (NumberFormatException e) {
                        color = -1;
                    }
                }
            }

            // Handle invalid Identifier format
            if (identifier == null) {
                return;
            }

            // Check if the identifier exists in the entity registry
            if (Registries.ENTITY_TYPE.containsId(identifier)) {
                HIGHLIGHT_ENTITIES.add(identifier);
                color = (color != -1) ? color : setColor(identifier);
                ENTITY_HIGHLIGHT.put(identifier, new Highlight(inputKey, beacon, color));
            } else {
                HIGHLIGHT_NAMES.add(inputKey);
                color = (color != -1) ? color : setColor(inputKey);
                NAME_HIGHLIGHT.put(inputKey, new Highlight(inputKey, beacon, color));
            }
        });
    }

    /**
     * Class representing a highlight for an entity.
     */
    private class Highlight {
        private final String name;
        private final boolean beacon;
        private final int color;

        /**
         * Constructor for Highlight.
         * 
         * @param name The name of the entity or identifier.
         * @param beacon Whether the entity is a beacon highlight.
         * @param color The color for the entity highlight as an integer.
         */
        public Highlight(String name, boolean beacon, int color) {
            this.name = name;
            this.beacon = beacon;
            this.color = color;
        }
    }
}
