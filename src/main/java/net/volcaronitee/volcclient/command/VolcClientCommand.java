package net.volcaronitee.volcclient.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.VolcClientScreen;
import net.volcaronitee.volcclient.config.VolcClientConfig;

public class VolcClientCommand {
    private static final String[] ALIASES = {"vc", "volc", "volcclient"};

    public static void register() {
        ClientCommandRegistrationCallback.EVENT
                .register((CommandDispatcher<FabricClientCommandSource> dispatcher,
                        CommandRegistryAccess access) -> {
                    for (String alias : ALIASES) {
                        dispatcher.register(literal(alias).executes(context -> help(context))

                                // Help command
                                .then(literal("help").executes(VolcClientCommand::help))

                                // Settings command
                                .then(literal("settings").executes(VolcClientCommand::settings))

                        // Command End
                        );
                    }
                });
    }

    private static int help(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        source.sendFeedback(Text.literal("Volc Client WIP"));
        return 1;
    }

    private static int settings(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return 0;
        }

        VolcClientScreen screen = new VolcClientScreen();
        client.setScreen(VolcClientConfig.createScreen(screen));
        return 1;
    }

}
