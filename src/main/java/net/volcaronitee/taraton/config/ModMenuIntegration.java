package net.volcaronitee.taraton.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return TaratonConfig::createScreen;
    }

    @SerialEntry
    public boolean test = true;
}
