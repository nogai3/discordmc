/*package com.lighsync.discord.network;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import java.time.OffsetDateTime;

public class DiscordRPCTest {
    private final long clientId;
    private boolean isRunning;
    private IPCClient client;

    public DiscordRPCTest(long clientId) {
        this.clientId = clientId;
    }

    public boolean isStarted() { return isRunning; }

    public void start(
            String appName, String bottomLine,
            String button1Label, String button1Url,
            String button2Label, String button2Url
            ) {

        client = new IPCClient(clientId);

        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setState(appName)
                        .setDetails(bottomLine)
                        .setStartTimestamp(OffsetDateTime.now())
                        .setLargeImage("canary-large", "Discord Canary")
                        .setSmallImage("ptb-small", "Discord PTB")
                        .setParty("party1234", 1, 6)
                        .setMatchSecret("xyzzy")
                        .setJoinSecret("join")
                        .setSpectateSecret("look");
                client.sendRichPresence(builder.build());
            }
        });
        try {
            client.connect(DiscordBuild.ANY);
            isRunning = true;
            System.out.println("Successfully connected to Discord!");
        } catch (NoDiscordClientException e) {
            System.out.println("No discord client found! Stacktrace: " + e);
        }
    }

    public void update(String appName, String bottomLine, String button1Label, String button1Url, String button2Label, String button2Url) {
        if (client == null) return;
        client.close();
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setState(appName)
                        .setDetails(bottomLine)
                        .setParty(button1Label, 1, 6)
                        .setMatchSecret(button2Url)
                        .setJoinSecret(button2Label)
                        .setSpectateSecret(button2Url);
                client.sendRichPresence(builder.build());
            }
        });
        try {
            client.connect(DiscordBuild.ANY);
        } catch (NoDiscordClientException e) {
            System.out.println("No discord client found! Stacktrace: " + e);
        }
    }
}*/
package com.lighsync.discord.network;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.lighsync.discord.Discord;
import com.lighsync.discord.client.DiscordClientConfig;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;

import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordRPCTest {
    private final long clientId;

    private volatile IPCClient client;
    private final AtomicBoolean ready = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler;

    private volatile OffsetDateTime startTime;

    public DiscordRPCTest(long clientId) {
        this.clientId = clientId;
    }

    public boolean isStarted() {
        return client != null && client.getStatus() == PipeStatus.CONNECTED && ready.get();
    }

   public void connect() {
        if (client != null && client.getStatus() == PipeStatus.CONNECTED) return;

        client = new IPCClient(clientId);
        ready.set(false);

        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                ready.set(true);
                if (startTime == null) startTime = OffsetDateTime.now();
            }
            @Override
            public void onDisconnect(IPCClient client, Throwable t) {
                ready.set(false);
            }
        });

        try {
            client.connect(DiscordBuild.ANY);
       } catch (NoDiscordClientException e) {
            client = null;
            ready.set(false);
            System.out.println("Error when Discord connecting! Stacktrace: " + e);
        }
   }

   public void sendOrUpdatePresence(
           String appName, String bottomLine,
           String button1Label, String button1Url,
           String button2Label, String button2Url,
           String largeAssetKey
   ) {
        if (!isStarted()) return;

       RichPresence.Builder b = new RichPresence.Builder()
               .setDetails(appName)
               .setState(bottomLine)
               .setStartTimestamp(startTime != null ? startTime : (startTime = OffsetDateTime.now()));

       // String largeAssetKey = DiscordClientConfig.ICON_ASSET_KEY.get();
       if (largeAssetKey != null && !largeAssetKey.isBlank()) {
           b.setLargeImage(largeAssetKey, buildBottomLine());
       } else {
           b.setLargeImage("default", buildBottomLine());
       }
               // .setParty("party1234", 1, 6)
               // .setMatchSecret("xyzzy")
               // .setJoinSecret("join")
               // .setSpectateSecret("look");

       if (button1Label != null && button1Url != null && !button1Label.isBlank() && !button1Url.isBlank()) b.addButton(button1Label, button1Url);
       if (button2Label != null && button2Url != null && !button2Label.isBlank() && !button2Url.isBlank()) b.addButton(button2Label, button2Url);

       client.sendRichPresence(b.build());
   }

    private static String buildBottomLine() {
        Minecraft mc = Minecraft.getInstance();
        return switch (DiscordClientConfig.BOTTOM_LINE_MODE.get()) {
            case CUSTOM -> DiscordClientConfig.BOTTOM_LINE_CUSTOM.get();
            case WORLD_NAME -> {
                if (mc.level != null && mc.getSingleplayerServer() != null) {
                    yield "Playing in world: " +
                            mc.getSingleplayerServer().getWorldData().getLevelName() +
                            " | Dimension: " + parseDimension(mc);
                }
                yield "Main menu";
            }
            case PLAYER_NAME -> {
                yield "Player name: " + mc.player.getGameProfile().getName();
            }
            case GAME_VERSION -> {
                yield "Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + mc.getLaunchedVersion() + ")";
            }
        };
    }

    private static String parseDimension(Minecraft mc) {
        mc = Minecraft.getInstance();
        return switch (mc.level.dimension().location().toString()) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End.";
            default -> "";
        };
    }

   /*public void testOnce() {
        RichPresence.Builder b = new RichPresence.Builder()
                .setDetails("TEST")
                .setState("ICON CHECK")
                .setStartTimestamp(OffsetDateTime.now())
                .setLargeImage("enderman", "Enderman")
                        .s

        client.sendRichPresence(b.build());
   }*/

   public void disconnect() {
        stopAutoUpdate();
        ready.set(false);
        startTime = null;
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {}
        }
        client = null;
   }

   public void startAutoUpdate(long periodSeconds, Runnable updateLogic) {
        if (scheduler != null) return;

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "discord-rpc-updater");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!isStarted()) {
                    connect();
                    return;
                }
                updateLogic.run();
            } catch (Throwable e) {
                System.err.println(e.toString());
            }
        }, 0, periodSeconds, TimeUnit.SECONDS);
   }

   public  void stopAutoUpdate() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
   }
}