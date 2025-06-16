package net.volcaronitee.volcclient.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

/**
 * Utility class for handling client commands.
 */
public class CommandUtil {
    private static final String[] ALIASES = {"vc", "volc", "volcclient"};

    /*
     * Initializes the client command registration for Volc Client.
     */
    public static void init() {
        ClientCommandRegistrationCallback.EVENT
                .register((CommandDispatcher<FabricClientCommandSource> dispatcher,
                        CommandRegistryAccess access) -> {
                    for (String alias : ALIASES) {
                        dispatcher.register(literal(alias).executes(context -> help(context))

                                // Help command
                                .then(literal("help").executes(CommandUtil::help))

                                // Settings command
                                .then(literal("settings").executes(CommandUtil::settings))

                                // Debug command
                                .then(literal("debug").executes(CommandUtil::debug))

                        // Command End
                        );
                    }
                });
    }

    /**
     * Displays help information for the Volc Client commands.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int help(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        source.sendFeedback(Text.literal("Volc Client WIP"));
        return 1;
    }

    /**
     * Opens the settings screen for Volc Client.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int settings(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return 0;
        }

        ScreenUtil screen = new ScreenUtil();
        client.setScreen(ConfigUtil.createScreen(screen));
        return 1;
    }

    /**
     * Displays debug information for the Volc Client.
     * 
     * @param context The command context containing the source and arguments.
     * @return 1 if the command was executed successfully, 0 otherwise.
     */
    private static int debug(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() == null || source.getWorld() == null) {
            return 0;
        }

        StringBuilder debugMessage = new StringBuilder("Volc Client Debug:\n");
        debugMessage.append(PlayerUtil.debugPlayer()).append("\n");
        debugMessage.append(LocationUtil.debugLocation()).append("\n");
        debugMessage.append(PartyUtil.debugParty());
        source.sendFeedback(Text.literal(debugMessage.toString()));
        return 1;
    }
}
