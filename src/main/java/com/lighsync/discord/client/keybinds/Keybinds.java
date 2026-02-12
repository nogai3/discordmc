package com.lighsync.discord.client.keybinds;

import com.lighsync.discord.Discord;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class Keybinds {
    public static final Keybinds INSTANCE = new Keybinds();

    private Keybinds() {}

    private static final String CATEGORY = "key.categories" + Discord.MOD_ID;

    public final KeyMapping OPEN_SETTINGS = new KeyMapping(
            "key." + Discord.MOD_ID + ".open_settings",
            InputConstants.Type.SCANCODE,
            InputConstants.KEY_0,
            KeyMapping.Category.GAMEPLAY
    );


}