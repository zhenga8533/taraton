package net.volcaronitee.volcclient.util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import java.nio.file.Path;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ListUtil {
    private String title;
    private Path configPath;
    private ConfigClassHandler<ListUtil> handler;

    public ListUtil(String title, String configPath) {
        this.title = title;
        this.configPath = Path.of(configPath);
        this.handler = ConfigClassHandler.createBuilder(ListUtil.class)
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                        .setPath(this.configPath).appendGsonBuilder(gsonBuilder -> gsonBuilder
                                .setPrettyPrinting().disableHtmlEscaping().serializeNulls())
                        .build())
                .build();
    }

    public LiteralArgumentBuilder<FabricClientCommandSource> command(String name) {
        return literal(name).executes(context -> {
            ScreenUtil screen = new ScreenUtil();
            context.getSource().getClient().setScreen(screen);
            return 1;
        });
    }

    public ConfigCategory create(ConfigUtil defaults, ConfigUtil config) {
        return ConfigCategory.createBuilder().name(Text.literal(this.title)).build();
    }

    public Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(handler, (defaults, config, builder) -> {
            builder.title(Text.literal(this.title)).build();

            return builder;
        }).generateScreen(parent);
    }
}
