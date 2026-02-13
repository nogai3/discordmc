package com.lighsync.discord.handlers;

import com.lighsync.discord.Discord;
import com.lighsync.discord.client.DiscordClientConfig;
import com.lighsync.discord.client.gui.SettingsScreen;
import com.lighsync.discord.client.keybinds.Keybinds;
import com.lighsync.discord.network.DiscordRPC;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Discord.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {
    private static int rpcTickTimer = 0;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (Keybinds.INSTANCE.OPEN_SETTINGS.consumeClick()) {
            if (!(mc.screen instanceof SettingsScreen)) {
                mc.setScreen(new SettingsScreen(mc.screen));
            }
        }

        rpcTickTimer++;

        if (rpcTickTimer >= 100) {
            rpcTickTimer = 0;
            updateRPC();
        }
    }

    private static void updateRPC() {
        DiscordRPC rpc = ClientModHandler.getRpc();
        if (rpc == null || !rpc.isStarted()) return;

        Minecraft mc = Minecraft.getInstance();

        String appName = DiscordClientConfig.APP_NAME.get();
        String bottomLine = buildBottomLine(mc);

        rpc.update(
                appName,
                bottomLine,
                DiscordClientConfig.BUTTON_1_LABEL.get(),
                DiscordClientConfig.BUTTON_1_URL.get(),
                DiscordClientConfig.BUTTON_2_LABEL.get(),
                DiscordClientConfig.BUTTON_2_LABEL.get()
        );
    }

    private static String buildBottomLine(Minecraft mc) {
        return switch (DiscordClientConfig.BOTTOM_LINE_MODE.get()) {
            case CUSTOM -> DiscordClientConfig.BOTTOM_LINE_CUSTOM.get();
            case WORLD_NAME -> {
                if (mc.level != null) yield
                        "Playing in world: " +
                                mc.getSingleplayerServer().getWorldData().getLevelName() +
                                " | Dimension: " +
                                switch(mc.level.dimension().location().toString()) {
                                    case "minecraft:overworld" -> "Overworld";
                                    case "minecraft:the_nether" -> "The Nether";
                                    case "minecraft:the_end" -> "The End.";
                                    default -> "";
                                };
                yield "Main menu";
            }
            case GAME_VERSION -> {
                yield "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + mc.getLaunchedVersion() + ")";
            }
        };
    }
}
