package net.volcaronitee.volcclient.feature.chat;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.volcaronitee.volcclient.util.ListUtil;

public class JoinWhitelist {
    private static final ListUtil WHITE_LIST = new ListUtil("White List", "white_list.json");

    public static LiteralArgumentBuilder<FabricClientCommandSource> registerCommand() {
        return literal("whitelist").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) {
                return 0;
            }

            client.send(() -> {
                client.setScreen(WHITE_LIST.createScreen(client.currentScreen));
            });

            return 1;
        });
    }
}
