package com.lighsync.discord.handlers;

import com.lighsync.discord.Discord;
import com.lighsync.discord.client.gui.SettingsScreen;
import com.lighsync.discord.client.keybinds.Keybinds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Discord.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (Keybinds.INSTANCE.OPEN_SETTINGS.isDown() && mc.player != null) {
            Keybinds.INSTANCE.OPEN_SETTINGS.consumeClick();
            mc.setScreen(new SettingsScreen(null));
        }
    }
}
