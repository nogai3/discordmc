package com.lighsync.discord.client;

import com.lighsync.discord.Discord;
import net.minecraftforge.common.ForgeConfigSpec;

public class DiscordClientConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> APP_NAME;
    public static final ForgeConfigSpec.EnumValue<BottomLineMode> BOTTOM_LINE_MODE;
    public static final ForgeConfigSpec.ConfigValue<String> BOTTOM_LINE_CUSTOM;

    public static final ForgeConfigSpec.ConfigValue<String> BUTTON_1_LABEL;
    public static final ForgeConfigSpec.ConfigValue<String> BUTTON_1_URL;

    public static final ForgeConfigSpec.ConfigValue<String> BUTTON_2_LABEL;
    public static final ForgeConfigSpec.ConfigValue<String> BUTTON_2_URL;

    public static final ForgeConfigSpec.ConfigValue<String> ICON_ID;

    static {
        var b = new ForgeConfigSpec.Builder();

        b.push(Discord.MOD_ID);

        APP_NAME = b.comment("RPC application name")
                .define("appName", "Minecraft {version}");

        BOTTOM_LINE_MODE = b.comment("Bottom line mode")
                .defineEnum("bottomLineMode", BottomLineMode.WORLD_NAME);

        BOTTOM_LINE_CUSTOM = b.comment("Customn bottom line if mode=CUSTOM")
                .define("bottomLineCustom", "MyServer!");

        BUTTON_1_LABEL =    b.define("button1Label", "Github");
        BUTTON_1_URL =      b.define("button1Url", "https://github.com/nogai3");

        BUTTON_2_LABEL =    b.define("button2Label", "Modrinth");
        BUTTON_2_URL =      b.define("button2Url", "htts://modrinth.com/nogai3");

        ICON_ID = b.define("iconId","discord:gui/discord/icons/default");

        b.pop();

        SPEC = b.build();
    }

    public enum BottomLineMode {
        WORLD_NAME,
        GAME_VERSION,
        CUSTOM
    }
}