package net.volcaronitee.nar.feature.chat;

import net.minecraft.text.Text;
import net.volcaronitee.nar.config.NarList;

public class AutoKick {
    public static final NarList BLACK_LIST = new NarList("Black List",
            Text.literal("A list of players to block from using various features."),
            "black_list.json");

    public static void register() {

    }
}
