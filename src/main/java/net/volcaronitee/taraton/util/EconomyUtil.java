package net.volcaronitee.taraton.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.taraton.config.TaratonJson;

public class EconomyUtil {
    private static final String AUCTION_URL = "https://volcaronitee.pythonanywhere.com/auction";
    private static final String BAZAAR_URL = "https://volcaronitee.pythonanywhere.com/bazaar";

    // Use volatile to ensure thread-safe updates
    private static volatile JsonObject auctionData = new JsonObject();
    private static volatile JsonObject bazaarData = new JsonObject();

    public static void init() {
        // Fetch data immediately on startup
        updateAuction(MinecraftClient.getInstance());
        updateBazaar(MinecraftClient.getInstance());

        // Register periodic updates every 30 minutes (36000 ticks)
        TickUtil.register(EconomyUtil::updateAuction, 36000);
        TickUtil.register(EconomyUtil::updateBazaar, 36000);
    }

    /**
     * Updates the auction data by fetching it from the specified URL.
     * 
     * @param client The Minecraft client instance, used for context.
     */
    public static void updateAuction(MinecraftClient client) {
        RequestUtil.get(AUCTION_URL).thenAccept(response -> {
            if (response != null) {
                try {
                    JsonObject parsedResponse =
                            TaratonJson.GSON.fromJson(response, JsonObject.class);
                    if (parsedResponse != null && parsedResponse.has("items")) {
                        auctionData = parsedResponse.getAsJsonObject("items");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse auction data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Updates the bazaar data by fetching it from the specified URL.
     * 
     * @param client The Minecraft client instance, used for context.
     */
    public static void updateBazaar(MinecraftClient client) {
        RequestUtil.get(BAZAAR_URL).thenAccept(response -> {
            if (response != null) {
                try {
                    JsonObject parsedResponse =
                            TaratonJson.GSON.fromJson(response, JsonObject.class);
                    if (parsedResponse != null && parsedResponse.has("items")) {
                        bazaarData = parsedResponse.getAsJsonObject("items");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse bazaar data: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Retrieves the auction price for a given item ID.
     * 
     * @param itemId The ID of the item to retrieve the auction price for.
     * @return The auction price of the item, or 0 if not found.
     */
    public static long getAuctionLbin(String itemId) {
        if (auctionData.has(itemId)) {
            JsonObject itemInfo = auctionData.getAsJsonObject(itemId);
            if (itemInfo.has("lbin")) {
                return itemInfo.get("lbin").getAsLong();
            }
        }
        return 0L;
    }

    /**
     * Retrieves the auction price for a given item ID, including the buy now price.
     * 
     * @param itemId The ID of the item to retrieve the auction price for.
     * @return The auction price of the item, or 0 if not found.
     */
    public static double getBazaarBuyPrice(String itemId) {
        if (bazaarData.has(itemId)) {
            JsonElement itemInfo = bazaarData.get(itemId);
            if (itemInfo.isJsonArray() && itemInfo.getAsJsonArray().size() >= 1) {
                return itemInfo.getAsJsonArray().get(0).getAsDouble();
            }
        }
        return 0.0;
    }

    /**
     * Retrieves the sell price for a given item ID from the bazaar data.
     * 
     * @param itemId The ID of the item to retrieve the sell price for.
     * @return The sell price of the item, or 0 if not found.
     */
    public static double getBazaarSellPrice(String itemId) {
        if (bazaarData.has(itemId)) {
            JsonElement itemInfo = bazaarData.get(itemId);
            if (itemInfo.isJsonArray() && itemInfo.getAsJsonArray().size() >= 2) {
                return itemInfo.getAsJsonArray().get(1).getAsDouble();
            }
        }
        return 0.0;
    }
}
