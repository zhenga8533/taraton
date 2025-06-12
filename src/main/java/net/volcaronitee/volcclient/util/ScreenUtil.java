package net.volcaronitee.volcclient.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.volcaronitee.volcclient.VolcClient;

public class ScreenUtil extends Screen {
    public static final Text TEXT = Text.literal("Volc Client " + VolcClient.MOD_VERSION);

    public ScreenUtil() {
        super(TEXT);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
