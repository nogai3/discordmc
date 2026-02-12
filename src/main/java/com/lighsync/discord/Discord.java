package com.lighsync.discord;

import com.lighsync.discord.client.DiscordClientConfig;
import com.lighsync.discord.handlers.ClientModHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(Discord.MOD_ID)
public class Discord {
    public static final String MOD_ID = "discord";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Discord(FMLJavaModLoadingContext context) {
        IEventBus
        // MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DiscordClientConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}
}