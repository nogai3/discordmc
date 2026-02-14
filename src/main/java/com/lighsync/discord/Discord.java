package com.lighsync.discord;

import com.lighsync.discord.client.DiscordClientConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Discord.MOD_ID)
public class Discord {
    public static final String MOD_ID = "discord";
    public static final String GAME_VERSION_FROM_SHARED_CONSTANTS = SharedConstants.getCurrentVersion().toString();
    public static final String GAME_VERSION = "1.21.1";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Discord(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.CLIENT, DiscordClientConfig.SPEC);
    }
    private void commonSetup(final FMLCommonSetupEvent event) { LOGGER.info("test"); }

    public static Logger getLogger() { return LOGGER; }
}