package com.lighsync.discord.handlers;

import com.lighsync.discord.Discord;
import com.lighsync.discord.client.DiscordClientConfig;
import com.lighsync.discord.client.keybinds.Keybinds;
import com.lighsync.discord.network.DiscordRPC;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Discord.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {
    private static DiscordRPC rpc;

    public static DiscordRPC getRpc() { return rpc; }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            rpc = new DiscordRPC(1471601684454309920L);
            rpc.start(
                    DiscordClientConfig.APP_NAME.get(),
                    DiscordClientConfig.BOTTOM_LINE_CUSTOM.get(),
                    DiscordClientConfig.BUTTON_1_LABEL.get(),
                    DiscordClientConfig.BUTTON_1_URL.get(),
                    DiscordClientConfig.BUTTON_2_LABEL.get(),
                    DiscordClientConfig.BUTTON_2_URL.get()
            );
        });
    }
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybinds.INSTANCE.OPEN_SETTINGS);
    }
}
