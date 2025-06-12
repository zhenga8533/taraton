package net.volcaronitee.volcclient.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.volcaronitee.volcclient.util.ConfigUtil;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigUtil::createScreen;
    }

    @SerialEntry
    public boolean test = true;
}
