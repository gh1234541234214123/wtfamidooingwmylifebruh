package com.superpowers.mod;

import com.superpowers.mod.command.SuperpowersCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperpowersMod implements ModInitializer {

    public static final String MOD_ID = "superpowers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Superpowers mod loaded!");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SuperpowersCommand.register(dispatcher);
        });
    }
}
