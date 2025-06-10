package net.volcaronitee.volcclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.volcaronitee.volcclient.command.VolcClientCommand;

public class VolcClient implements ClientModInitializer {
	public static final String MOD_ID = "volc-client";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");
		VolcClientCommand.register();
	}
}
