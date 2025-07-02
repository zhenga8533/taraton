package net.volcaronitee.nar.feature.combat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.volcaronitee.nar.helper.RelationalValue;
import net.volcaronitee.nar.helper.RelationalValue.Operator;
import net.volcaronitee.nar.util.ConfigUtil;
import net.volcaronitee.nar.util.ListUtil;
import net.volcaronitee.nar.util.LocationUtil;
import net.volcaronitee.nar.util.LocationUtil.World;
import net.volcaronitee.nar.util.OverlayUtil;
import net.volcaronitee.nar.util.OverlayUtil.LineContent;
import net.volcaronitee.nar.util.TextUtil;
import net.volcaronitee.nar.util.TickUtil;

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
                    " to find vanilla entity names. If an entity ID is not found, it will be used to identify custom armor stand names. Remember, you can use 'F3 + I' to copy entity data to clipboard.\n\n\n"
                            + "§lOptions:\n\n"
                            + " §f--beacon §7Highlights entities that are within the beacon range.\n"
                            + " §f--color <hex> §7Sets the color for the entity highlight. Use hex colors like #FF0000.\n"
                            + " §f--height <num> §7Sets the height of the entity highlight.\n"
                            + " §f--width <num> §7Sets the width of the entity highlight.\n"
                            + " §f--range <num> §7Sets the maximum horizontal range of the entity highlight.\n"
                            + " §f--depth <num> §7Sets the maximum vertical depth of the entity highlight.\n"
                            + " §f--identifier <name> §7Sets entity identifier for custom armor stands.\n"
                            + " §f--locations <names> §7Sets the islands locations.\n\n"
                            + "§8You can use relational operators like <, >, <=, >=, =, != for numeric values.\n\n\n"
                            + "§lExample:\n\n"
                            + "Lion --beacon --color #FF0000 --height >2 --width <=1 --depth 16 --offset 32 --identifier Player --locations GARDEN,SPIDERS_DEN")),
            "entity_list.json");

    private static final Map<Entity, Highlight> HIGHLIGHTED_ENTITIES = new HashMap<>();

    private static final Set<Identifier> HIGHLIGHT_ENTITIES = new HashSet<>();
    private static final Map<Identifier, Highlight> ENTITY_HIGHLIGHT = new HashMap<>();

    private static final Set<String> HIGHLIGHT_NAMES = new HashSet<>();
    private static final Map<String, Highlight> NAME_HIGHLIGHT = new HashMap<>();

    private static final EntityType<?> ARMOR_STAND =
            Registries.ENTITY_TYPE.get(Identifier.of("minecraft:armor_stand"));

    private static final Identifier BEACON_BEAM_TEXTURE =
            Identifier.of("minecraft", "textures/entity/beacon_beam.png");

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
                if (!shouldHighlight(client, entity, highlight)) {
                    return;
                }

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
                        Highlight highlight = NAME_HIGHLIGHT.get(key);
                        Box boundingBox = entity.getBoundingBox().expand(0.2, 2, 0.2);
                        List<Entity> nearbyEntities = client.world.getOtherEntities(entity,
                                boundingBox, e -> e.getType() != ARMOR_STAND && !e.isInvisible()
                                        && shouldHighlight(client, e, highlight));
                        if (nearbyEntities.isEmpty()) {
                            return;
                        }

                        // Sort entities by distance to the armor stand
                        Collections.sort(nearbyEntities, Comparator.comparingDouble((Entity e) -> {
                            double dx = e.getX() - entity.getX();
                            double dz = e.getZ() - entity.getZ();
                            return Math.sqrt(dx * dx + dz * dz);
                        }).thenComparingDouble(Entity::getY));

                        // Highlight the closest entity
                        Entity closestEntity = nearbyEntities.get(0);
                        HIGHLIGHTED_ENTITIES.put(closestEntity, highlight);

                        entityCount.merge(highlight.name, 1, Integer::sum);
                        totalCount.incrementAndGet();
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
     * Checks if the entity should be highlighted based on the highlight criteria.
     * 
     * @param client The Minecraft client instance.
     * @param entity The entity to check.
     * @param highlight The highlight criteria to check against.
     * @return True if the entity should be highlighted, false otherwise.
     */
    private boolean shouldHighlight(MinecraftClient client, Entity entity, Highlight highlight) {
        // Range check
        if (highlight.range != null
                && !highlight.range.evaluate((float) entity.distanceTo(client.player))) {
            return false;
        }

        // Height check
        if (highlight.height != null && !highlight.height.evaluate(entity.getHeight())) {
            return false;
        }

        // Width check
        if (highlight.width != null && !highlight.width.evaluate(entity.getWidth())) {
            return false;
        }

        // Depth check (vertical distance)
        if (highlight.depth != null && !highlight.depth
                .evaluate((float) Math.abs(entity.getY() - client.player.getY()))) {
            return false;
        }

        // Location check
        if (!highlight.locations.isEmpty()
                && !highlight.locations.contains(LocationUtil.getWorld())) {
            return false;
        }

        // Identifier check (for custom armor stands)
        if (highlight.identifier != null
                && !Registries.ENTITY_TYPE.getId(entity.getType()).equals(highlight.identifier)) {
            return false;
        }

        return true;
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
     * Parses a string to create a RelationalValue object. Expects formats like "10", ">5", "<=20",
     * "=7", "!=15".
     *
     * @param valueString The string to parse.
     * @return A RelationalValue object, or null if parsing fails.
     */
    private RelationalValue parseRelationalValue(String valueString) {
        if (valueString == null || valueString.isEmpty()) {
            return null;
        }

        // Attempt to parse with an operator
        for (Operator op : Operator.values()) {
            if (valueString.startsWith(op.getSymbol())) {
                String numStr = valueString.substring(op.getSymbol().length()).trim();
                try {
                    float value = Float.parseFloat(numStr);
                    return new RelationalValue(op, value);
                } catch (NumberFormatException e) {
                    // Log error or ignore, return null
                    return null;
                }
            }
        }

        // If no operator found, assume equality
        try {
            float value = Float.parseFloat(valueString.trim());
            return new RelationalValue(Operator.EQUAL, value);
        } catch (NumberFormatException e) {
            // Log error or ignore, return null
            return null;
        }
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

            Identifier entity = null;
            String inputKey = "";
            boolean beacon = false;
            int color = -1;
            RelationalValue height = null;
            RelationalValue width = null;
            RelationalValue range = null;
            RelationalValue depth = null;
            Identifier identifier = null;
            List<World> locations = new ArrayList<>();

            // Parse the input key and options
            String[] args = pair.getKey().trim().split(" --");
            for (String arg : args) {
                if (arg.equals(args[0])) {
                    // The first argument is the entity identifier or name
                    inputKey = arg.trim();
                    entity = Identifier.tryParse(inputKey.toLowerCase());
                    continue;
                } else if (arg.startsWith("beacon ")) {
                    // This argument indicates a beacon highlight
                    beacon = true;
                    continue;
                }

                String[] argArgs = arg.trim().split(" ");
                if (argArgs.length < 2) {
                    continue;
                }

                String parameter = argArgs[1];
                if (arg.startsWith("color ")) {
                    // This argument sets the color for the highlight
                    try {
                        color = Integer.parseInt(parameter.replace("#", ""), 16);
                    } catch (NumberFormatException e) {
                        color = -1;
                    }
                } else if (arg.startsWith("height")) {
                    // This argument sets the height for the highlight
                    height = parseRelationalValue(parameter);
                } else if (arg.startsWith("width")) {
                    // This argument sets the width for the highlight
                    width = parseRelationalValue(parameter);
                } else if (arg.startsWith("range")) {
                    // This argument sets the range for the highlight
                    range = parseRelationalValue(parameter);
                } else if (arg.startsWith("depth")) {
                    // This argument sets the depth for the highlight
                    depth = parseRelationalValue(parameter);
                } else if (arg.startsWith("identifier ")) {
                    // This argument sets the identifier for custom armor stands
                    identifier = Identifier.tryParse(parameter.toLowerCase());
                } else if (arg.startsWith("locations ")) {
                    // This argument sets the locations for the highlight
                    String[] locs = parameter.split(",");
                    for (String loc : locs) {
                        // Check if in World enum
                        try {
                            World world = World.valueOf(loc.trim());
                            locations.add(world);
                        } catch (IllegalArgumentException e) {
                            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                                    TextUtil.getTitle().append(Text.literal(" §cInvalid location: "
                                            + loc.trim() + " §7(EntityHighlight.java)")));
                        }
                    }
                }
            }

            // Handle invalid Identifier format
            if (entity == null) {
                return;
            }

            // Check if the identifier exists in the entity registry
            Highlight highlight = new Highlight(inputKey, beacon, color, height, width, range,
                    depth, identifier, locations);
            if (Registries.ENTITY_TYPE.containsId(entity)) {
                HIGHLIGHT_ENTITIES.add(entity);
                color = (color != -1) ? color : setColor(entity);
                ENTITY_HIGHLIGHT.put(entity, highlight);
            } else {
                HIGHLIGHT_NAMES.add(inputKey);
                color = (color != -1) ? color : setColor(inputKey);
                NAME_HIGHLIGHT.put(inputKey, highlight);
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
        private final RelationalValue height;
        private final RelationalValue width;
        private final RelationalValue range;
        private final RelationalValue depth;
        private final Identifier identifier;
        private final List<World> locations;

        /**
         * Constructor for Highlight.
         * 
         * @param name The name of the entity or identifier.
         * @param beacon Whether the entity is a beacon highlight.
         * @param color The color for the entity highlight as an integer.
         * @param height The height of the entity highlight.
         * @param width The width of the entity highlight.
         * @param range The horizontal range of the entity highlight.
         * @param depth The vertical depth of the entity highlight.
         * @param identifier The identifier for custom armor stands.
         * @param locations The locations for the entity highlight.
         */
        public Highlight(String name, boolean beacon, int color, RelationalValue height,
                RelationalValue width, RelationalValue range, RelationalValue depth,
                Identifier identifier, List<World> locations) {
            this.name = name;
            this.beacon = beacon;
            this.color = color;
            this.height = height;
            this.width = width;
            this.range = range;
            this.depth = depth;
            this.identifier = identifier;
            this.locations = locations;
        }
    }
}
