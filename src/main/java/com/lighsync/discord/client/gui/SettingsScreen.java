package com.lighsync.discord.client.gui;

import com.lighsync.discord.Discord;
import com.lighsync.discord.client.DiscordClientConfig;
import com.lighsync.discord.handlers.ClientModHandler;
import com.lighsync.discord.network.DiscordAssetMap;
import com.lighsync.discord.network.DiscordRPC;
import com.lighsync.discord.network.DiscordRPCTest;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class SettingsScreen extends Screen {
    private final Screen parent;

    private EditBox appNameBox;

    private CycleButton<DiscordClientConfig.BottomLineMode> bottomModeBtn;
    private EditBox bottomCustomBox;

    private EditBox btn1LabelBox, btn1UrlBox;
    private EditBox btn2LabelBox, btn2UrlBox;

    private IconPickerWidget iconPicker;

    private Button saveBtn;

    private DiscordClientConfig.BottomLineMode lastMode;

    public SettingsScreen(Screen parent) {
        super(Component.translatable("settings.name"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = 28;
        int w = 260;
        int h = 20;

        this.appNameBox = new EditBox(this.font, cx - w / 2, y + 5, w, h, Component.translatable("settings.appname"));
        this.appNameBox.setValue(DiscordClientConfig.APP_NAME.get());
        this.addRenderableWidget(appNameBox);

        y += 28 + 5;

        this.bottomModeBtn = CycleButton.<DiscordClientConfig.BottomLineMode>builder(SettingsScreen::modeText)
                .withValues(DiscordClientConfig.BottomLineMode.values())
                .withInitialValue(DiscordClientConfig.BOTTOM_LINE_MODE.get())
                .create(cx - w / 2, y + 5, 140, h, Component.translatable("settings.bottomline"));
        this.addRenderableWidget(this.bottomModeBtn);

        this.bottomCustomBox = new EditBox(this.font, cx - w / 2 + 150, y + 5, w - 150, h, Component.translatable("settings.custombottomline"));
        this.bottomCustomBox.setValue(DiscordClientConfig.BOTTOM_LINE_CUSTOM.get());
        this.addRenderableWidget(this.bottomCustomBox);

        this.lastMode = this.bottomModeBtn.getValue();
        updateEnabledStates();

        y += 28 + 5;

        this.btn1LabelBox = new EditBox(this.font, cx - w / 2, y + 5, 90, h, Component.translatable("settings.button1.label"));
        this.btn1LabelBox.setValue(DiscordClientConfig.BUTTON_1_LABEL.get());
        this.addRenderableWidget(this.btn1LabelBox);

        this.btn1UrlBox = new EditBox(this.font, cx - w / 2 + 95, y + 5, w - 95, h, Component.translatable("settings.button1.url"));
        this.btn1UrlBox.setValue(DiscordClientConfig.BUTTON_1_URL.get());
        this.addRenderableWidget(this.btn1UrlBox);

        y += 24 + 5;

        this.btn2LabelBox = new EditBox(this.font, cx - w / 2, y + 5, 90, h, Component.translatable("settings.button2.label"));
        this.btn2LabelBox.setValue(DiscordClientConfig.BUTTON_2_LABEL.get());
        this.addRenderableWidget(this.btn2LabelBox);

        this.btn2UrlBox = new EditBox(this.font, cx - w / 2 + 95, y + 5, w - 95, h, Component.translatable("settings.button2.url"));
        this.btn2UrlBox.setValue(DiscordClientConfig.BUTTON_2_URL.get());
        this.addRenderableWidget(this.btn2UrlBox);

        y += 30 + 5;

        List<ResourceLocation> icons = IconPickerWidget.scanIcons(Discord.MOD_ID, "gui/icons");
        this.iconPicker = new IconPickerWidget(cx - w / 2, y + 5, w, 56, icons, ResourceLocation.tryParse(DiscordClientConfig.ICON_ID.get()));
        this.addRenderableWidget(this.iconPicker);

        y += 70 + 5;

        this.saveBtn = this.addRenderableWidget(Button.builder(Component.translatable("settings.button.save"), b -> onSave()).bounds(cx - 100, y, 70, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("settings.button.cancel"), b -> onCancel()).bounds(cx - 25, y, 70, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("settings.button.reset"), b -> onReset()).bounds(cx + 50, y, 70, 20).build());

        updateEnabledStates();
    }

    private String buildBottomLine() {
        var mode = bottomModeBtn.getValue();
        var mc = Minecraft.getInstance();
        return switch (mode) {
            case CUSTOM -> bottomCustomBox.getValue();
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
        return switch (mc.level.dimension().location().toString()) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "The Nether";
            case "minecraft:the_end" -> "The End.";
            default -> "";
        };
    }

    private static Component modeText(DiscordClientConfig.BottomLineMode m) {
        return switch (m) {
            case WORLD_NAME -> Component.translatable("settings.worldname");
            case GAME_VERSION -> Component.translatable("settings.gameversion");
            case PLAYER_NAME -> Component.translatable("settings.playername");
            case CUSTOM -> Component.translatable("settings.custom");
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.appNameBox != null && this.appNameBox.visible && this.appNameBox.active && this.appNameBox.isMouseOver(mouseX, mouseY)) {
            this.setFocused(this.appNameBox);
            return this.appNameBox.mouseClicked(mouseX, mouseY, button);
        }
        if (this.bottomCustomBox != null && this.bottomCustomBox.visible && this.bottomCustomBox.active && this.bottomCustomBox.isMouseOver(mouseX, mouseY)) {
            this.setFocused(this.bottomCustomBox);
            return this.bottomCustomBox.mouseClicked(mouseX, mouseY, button);
        }
        if (this.btn1LabelBox != null && this.btn1LabelBox.visible && this.btn1LabelBox.active && this.btn1LabelBox.isMouseOver(mouseX, mouseY)) {
            this.setFocused(this.btn1LabelBox);
            return this.btn1LabelBox.mouseClicked(mouseX, mouseY, button);
        }
        if (this.btn1UrlBox != null && this.btn1UrlBox.visible && this.btn1UrlBox.active && this.btn1UrlBox.isMouseOver(mouseX, mouseY)) {
            this.setFocused(this.btn1UrlBox);
            return this.btn1UrlBox.mouseClicked(mouseX, mouseY, button);
        }
        if (this.btn2LabelBox != null && this.btn2LabelBox.visible && this.btn2LabelBox.active && this.btn2LabelBox.isMouseOver(mouseX, mouseY)) {
            this.setFocused(this.btn2LabelBox);
            return this.btn2LabelBox.mouseClicked(mouseX, mouseY, button);
        }
        if (this.btn2UrlBox != null && this.btn2UrlBox.visible && this.btn2UrlBox.active && this.btn2UrlBox.isMouseOver(mouseX, mouseY)) {
            this.setFocused(this.btn2UrlBox);
            return this.btn2UrlBox.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getFocused() instanceof EditBox eb && eb.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoints, int modifiers) {
        if (this.getFocused() instanceof EditBox eb && eb.charTyped(codePoints, modifiers)) {
            return true;
        }
        return super.charTyped(codePoints, modifiers);
    }

    @Override
    public void tick() {
        super.tick();

        var now = this.bottomModeBtn.getValue();
        if (now != lastMode) {
            lastMode = now;
            updateEnabledStates();
        }
        updateSaveEnabled();
    }

    private void updateEnabledStates() {
        boolean custom = this.bottomModeBtn.getValue() == DiscordClientConfig.BottomLineMode.CUSTOM;

        this.bottomCustomBox.setEditable(custom);
        this.bottomCustomBox.active = custom;
        this.bottomCustomBox.visible = custom;

        if (!custom) {
            this.bottomCustomBox.setFocused(false);
            if (this.getFocused() == this.bottomCustomBox) {
                this.setFocused(this.bottomModeBtn);
            }
        }
    }

    private void updateSaveEnabled() {
        boolean ok = true;

        ok &= isUrlOk(btn1UrlBox.getValue());
        ok &= isUrlOk(btn2UrlBox.getValue());

        this.saveBtn.active = ok;
    }

    private boolean isUrlOk(String url) {
        if (url == null || url.isBlank()) return true;
        return url.startsWith("http://") || url.startsWith("https://");
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.render(gui, mouseX, mouseY, partialTicks);

        gui.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        gui.drawString(this.font, Component.translatable("settings.txt.appname"), this.width / 2 - 130, 20 + 3, 0xA0A0A0);
        gui.drawString(this.font, Component.translatable("settings.txt.bottomline"), this.width / 2 - 130, 48 + 9, 0xA0A0A0);
        gui.drawString(this.font, Component.translatable("settings.txt.buttons"), this.width / 2 - 130, 76 + 14, 0xA0A0A0);
        gui.drawString(this.font, Component.translatable("settings.txt.icon"), this.width / 2 - 130, 128 + 25, 0xA0A0A0);

        /*if (this.iconPicker.isMouseOver(mouseX, mouseY)) {
            gui.renderTooltip(
                    this.font,
                    List.of(Component.translatable("settings.iconpicker.help")),
                    mouseX, mouseY,
            );
        }*/
    }

    private void onSave() {
        if (!isUrlOk(btn1UrlBox.getValue()) || !isUrlOk(btn2UrlBox.getValue())) return;

        set(DiscordClientConfig.APP_NAME, appNameBox.getValue());

        // set((ForgeConfigSpec.ConfigValue<DiscordClientConfig.BottomLineMode>) DiscordClientConfig.BOTTOM_LINE_MODE, bottomModeBtn.getValue());
        setEnum(DiscordClientConfig.BOTTOM_LINE_MODE, bottomModeBtn.getValue());
        set(DiscordClientConfig.BOTTOM_LINE_CUSTOM, bottomCustomBox.getValue());

        set(DiscordClientConfig.BUTTON_1_LABEL, btn1LabelBox.getValue());
        set(DiscordClientConfig.BUTTON_1_URL, btn1UrlBox.getValue());

        set(DiscordClientConfig.BUTTON_2_LABEL, btn2LabelBox.getValue());
        set(DiscordClientConfig.BUTTON_2_URL, btn2UrlBox.getValue());

        ResourceLocation selected = iconPicker != null ? iconPicker.getSelected() : null;
        String assetKey = DiscordAssetMap.getAssetKey(selected);
        if (selected != null) {
            set(DiscordClientConfig.ICON_ID, selected.toString());
            if (assetKey != null) set(DiscordClientConfig.ICON_ASSET_KEY, assetKey);
        }

        DiscordRPCTest rpcTest = ClientModHandler.getRpcTest();
        if (rpcTest != null && rpcTest.isStarted()) {
            String appName = appNameBox.getValue();
            String bottomLine = buildBottomLine();

            rpcTest.sendOrUpdatePresence(
                    appName,
                    bottomLine,
                    btn1LabelBox.getValue(),
                    btn1UrlBox.getValue(),
                    btn2LabelBox.getValue(),
                    btn2UrlBox.getValue(),
                    DiscordClientConfig.ICON_ASSET_KEY.get()
            );
        }

        Minecraft.getInstance().setScreen(parent);

    }

    private void onCancel() {
        Minecraft.getInstance().setScreen(parent);
    }

    private void onReset() {
        this.appNameBox.setValue("Minecraft " + Discord.GAME_VERSION);
        this.bottomModeBtn.setValue(DiscordClientConfig.BottomLineMode.WORLD_NAME);
        this.bottomCustomBox.setValue("MyServer!");

        this.btn1LabelBox.setValue("Github");
        this.btn1UrlBox.setValue("https://github.com/nogai3");
        this.btn2LabelBox.setValue("Modrinth");
        this.btn2UrlBox.setValue("https://modrinth.com/nogai3");

        this.iconPicker.setSelected(ResourceLocation.tryParse("discord:textures/gui/icons/default.png"));
    }

    private static <T> void set(ForgeConfigSpec.ConfigValue<T> v, T value) {
        v.set(value);
    }

    private static <E extends Enum<E>> void setEnum(ForgeConfigSpec.EnumValue<E> v, E value) {
        v.set(value);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        onCancel();
    }
}