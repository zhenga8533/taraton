package net.volcaronitee.volcclient.feature.chat;

import net.volcaronitee.volcclient.util.ListUtil;

public class JoinWhitelist {
    public static final ListUtil WHITE_LIST = new ListUtil("White List",
            "A list of players to automatically accept party invites from.", "white_list.json");
}
