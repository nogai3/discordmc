package com.lighsync.discord.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lighsync.discord.Discord;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DiscordAssetMap {
    private static final Gson GSON = new Gson();
    private static Map<String, String> map = Map.of();

    public static void load() {
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            ResourceLocation jsonRL = ResourceLocation.fromNamespaceAndPath(Discord.MOD_ID, "textures/gui/icons/icons_list.json");

            Optional<Resource> optional = rm.getResource(jsonRL);
            if (optional.isEmpty()) {
                Discord.getLogger().error("[DiscordAssetMap]: icons_list.json not found!");
                map = Map.of();
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(optional.get().open(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> loaded = GSON.fromJson(reader, type);

                map = loaded != null ? new HashMap<>(loaded) : Map.of();

                Discord.getLogger().info("[DiscordAssetMap]: Loaded " + map.size() + " icon mappings...");
            }
        } catch (Exception e) {
            map = Map.of();
            Discord.getLogger().error("[DiscordAssetMap]: Failed to load icons_list.json");
            e.printStackTrace();
        }
    }

    public static String getAssetKey(ResourceLocation rl) {
        if (rl == null) return null;
        return map.get(rl.toString());
    }
}