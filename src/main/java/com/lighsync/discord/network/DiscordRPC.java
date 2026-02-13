package com.lighsync.discord.network;

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;

import java.time.Instant;

public class DiscordRPC {
    // компуктер, пожалуйста, запусти всё, когда глеб придет чтобы всё сексуально работало ^~^ T_T :3 ;D <3 <3
    private final long clientId;
    private RichPresence presence;

    public DiscordRPC(long clientId) {
        this.clientId = clientId;
    }

    public boolean isStarted() {
        return presence != null;
    }

    public void start(String appName, String bottomLine,
                      String button1Label, String button1Url,
                      String button2Label, String button2Url) {

        if (!DiscordIPC.start(clientId, () ->
                System.out.println("Logged in Discord as: " + DiscordIPC.getUser().username))) {
            System.out.println("Failed to start DiscordIPC");
            return;
        }

        presence = new RichPresence();
        presence.setDetails(appName);
        presence.setState(bottomLine);
        presence.setStart(Instant.now().getEpochSecond());

        /*try {
            if (button1Label != null && !button1Label.isBlank() && button1Url != null && !button1Url.isBlank())
                presence.setButton1(button1Label, button1Url);
            if (button2Label != null && !button2Label.isBlank() && button2Url != null && !button2Url.isBlank())
                presence.setButton2(button2Label, button2Url);
        } catch (Throwable ignored) {}
        */
        DiscordIPC.setActivity(presence);
    }

    public void update(String appName, String bottomLine, String button1Label, String button2Label, String button1Url, String button2Url) {
        if (presence == null) return;
        presence.setDetails(appName);
        presence.setState(bottomLine);
        try {
            if (button1Label != null && !button1Label.isBlank() && button1Url != null && !button1Url.isBlank())
                System.out.println(String.format("[DEBUG]: [%s] [%s]", button1Label, button1Url));
            if (button2Label != null && !button2Label.isBlank() && button2Url != null && !button2Url.isBlank())
                System.out.println(String.format("[DEBUG]: [%s] [%s]", button2Label, button2Url));
        } catch (Throwable ignored) {}
        DiscordIPC.setActivity(presence);
    }

    public void stop() {
        DiscordIPC.stop();
    }
}