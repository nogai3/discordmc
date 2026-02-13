package com.lighsync.discord.handlers;

import com.lighsync.discord.Discord;
import com.lighsync.discord.client.DiscordClientConfig;
import com.lighsync.discord.client.keybinds.Keybinds;
import com.lighsync.discord.network.DiscordRPC;
import com.lighsync.discord.network.DiscordRPCTest;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Discord.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {
    private static DiscordRPC rpc;
    private static DiscordRPCTest rpcTest;
    private static Minecraft mc;

    public static DiscordRPC getRpc() { return rpc; }
    public static DiscordRPCTest getRpcTest() { return rpcTest; }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            /*rpc = new DiscordRPC(1471601684454309920L);
            rpc.start(
                    DiscordClientConfig.APP_NAME.get(),
                    DiscordClientConfig.BOTTOM_LINE_CUSTOM.get(),
                    DiscordClientConfig.BUTTON_1_LABEL.get(),
                    DiscordClientConfig.BUTTON_1_URL.get(),
                    DiscordClientConfig.BUTTON_2_LABEL.get(),
                    DiscordClientConfig.BUTTON_2_URL.get()
            );*/
            rpcTest = new DiscordRPCTest(1471601684454309920L);
            rpcTest.startAutoUpdate(5, () -> {
                rpcTest.sendOrUpdatePresence(
                        DiscordClientConfig.APP_NAME.get(),
                        buildBottomLine(),
                        DiscordClientConfig.BUTTON_1_LABEL.get(),
                        DiscordClientConfig.BUTTON_1_URL.get(),
                        DiscordClientConfig.BUTTON_2_LABEL.get(),
                        DiscordClientConfig.BUTTON_2_URL.get()
                );
            });
        });
    }
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybinds.INSTANCE.OPEN_SETTINGS);
    }

    private static String buildBottomLine() {
        return switch (DiscordClientConfig.BOTTOM_LINE_MODE.get()) {
            case CUSTOM -> DiscordClientConfig.BOTTOM_LINE_CUSTOM.get();
            case WORLD_NAME -> {
                mc = Minecraft.getInstance();
                if (mc.level != null && mc.getSingleplayerServer() != null) {
                    yield "Playing in world: " +
                            mc.getSingleplayerServer().getWorldData().getLevelName() +
                            " | Dimension: " + parseDimension(mc);
                }
                yield "Main menu";
            }
            case GAME_VERSION -> {
                yield "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + mc.getLaunchedVersion() + ")";
            }
        };
    }

    private static String parseDimension(Minecraft mc) {
        return switch (mc.level.dimension().location().toString()) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End.";
            default -> "";
        };
    }
}