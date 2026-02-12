package com.lighsync.discord.client.gui;

import com.lighsync.discord.Discord;
import com.lighsync.discord.client.DiscordClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraftforge.common.ForgeConfigSpec;

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

        this.addRenderableWidget(Button.builder(Component.literal(""), b -> {}).bounds(0,0,0,0).build());
        this.appNameBox = new EditBox(this.font, cx - w/2, y, w, h, Component.translatable("settings.appname"));
        this.appNameBox.setValue(DiscordClientConfig.APP_NAME.get());
        this.addRenderableWidget(appNameBox);

        y += 28;

        this.bottomModeBtn = CycleButton.<DiscordClientConfig.BottomLineMode>builder(SettingsScreen::modeText, DiscordClientConfig.BOTTOM_LINE_MODE.get())
                .withValues(DiscordClientConfig.BottomLineMode.values())
                .create(cx - w/2, y, 140, h, Component.translatable("settings.bottomline"));
        this.addRenderableWidget(this.bottomModeBtn);

        this.bottomCustomBox = new EditBox(this.font, cx - w/2 + 150, y, w - 150, h, Component.translatable("settings.custombottomline"));
        this.bottomCustomBox.setValue(DiscordClientConfig.BOTTOM_LINE_CUSTOM.get());
        this.addRenderableWidget(this.bottomCustomBox);

        y += 28;

        this.btn1LabelBox = new EditBox(this.font, cx -w/2, y, 90, h, Component.translatable("settings.button1.label"));
        this.btn1LabelBox.setValue(DiscordClientConfig.BUTTON_1_LABEL.get());
        this.addRenderableWidget(this.btn1LabelBox);

        this.btn1UrlBox = new EditBox(this.font, cx - w/2 + 95, y, w - 95, h, Component.translatable("settings.button1.url"));
        this.btn1UrlBox.setValue(DiscordClientConfig.BUTTON_1_URL.get());
        this.addRenderableWidget(this.btn1UrlBox);

        y += 24;

        this.btn2LabelBox = new EditBox(this.font, cx - w/2, y, 90, h, Component.translatable("settings.button2.label"));
        this.btn2LabelBox.setValue(DiscordClientConfig.BUTTON_2_LABEL.get());
        this.addRenderableWidget(this.btn2LabelBox);

        this.btn2UrlBox = new EditBox(this.font, cx - w/2 + 95, y, w - 95, h, Component.translatable("settings.button2.url"));
        this.btn2UrlBox.setValue(DiscordClientConfig.BUTTON_2_URL.get());
        this.addRenderableWidget(this.btn2UrlBox);

        y += 30;

        List<Identifier> icons = IconPickerWidget.scanIcons(Discord.MOD_ID, "textures/gui/discord/icons");
        this.iconPicker = new IconPickerWidget(cx - w/2, y, w, 56, icons, Identifier.tryParse(DiscordClientConfig.ICON_ID.get()));
        this.addRenderableWidget(this.iconPicker);

        y += 70;

        this.saveBtn = this.addRenderableWidget(Button.builder(Component.translatable("settings.button.save"), b -> onSave()).bounds(cx - 100, y, 70, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("settings.button.cancel"), b -> onCancel()).bounds(cx - 25, y, 70, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("settings.button.reset"), b -> onReset()).bounds(cx + 50, y, 70, 20).build());

        updateEnabledStates();
    }

    private static Component modeText(DiscordClientConfig.BottomLineMode m) {
        return switch (m) {
            case WORLD_NAME -> Component.translatable("settings.worldname");
            case GAME_VERSION -> Component.translatable("settings.gameversion");
            case CUSTOM -> Component.translatable("settings.custom");
        };
    }

    @Override
    public void tick() {
        super.tick();
        /*this.appNameBox.tick();
        this.bottomCustomBox.tick();
        this.btn1LabelBox.tick();
        this.btn1UrlBox.tick();
        this.btn2LabelBox.tick();
        this.btn2UrlBox.tick();*/

        updateEnabledStates();
        updateSaveEnabled();
    }

    private void updateEnabledStates() {
        boolean custom = this.bottomModeBtn.getValue() == DiscordClientConfig.BottomLineMode.CUSTOM;
        this.bottomCustomBox.setEditable(custom);
        this.bottomCustomBox.setVisible(custom);
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
        this.renderBackground(gui, mouseX, mouseY, partialTicks);

        gui.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);

        gui.drawString(this.font, Component.translatable("settings.txt.appname"), this.width/2 - 130, 20, 0xA0A0A0);
        gui.drawString(this.font, Component.translatable("settings.txt.bottomline"), this.width/2 - 130, 48, 0xA0A0A0);
        gui.drawString(this.font, Component.translatable("settings.txt.buttons"), this.width/2 - 130, 76, 0xA0A0A0);
        gui.drawString(this.font, Component.translatable("settings.txt.icon"), this.width/2 - 130, 128, 0xA0A0A0);

        super.render(gui, mouseX, mouseY, partialTicks);

        /*if (this.iconPicker.isMouseOver(mouseX, mouseY)) {
            gui.renderTooltip(
                    this.font,
                    List.of(Component.translatable("settings.iconpicker.help")),
                    mouseX, mouseY,
            );
        }*/
    }

    private void onSave() {

    }

    private void onCancel() {
        Minecraft.getInstance().setScreen(parent);
    }

    private void onReset() {
        this.appNameBox.setValue("Minecraft {version}");
        this.bottomModeBtn.setValue(DiscordClientConfig.BottomLineMode.WORLD_NAME);
        this.bottomCustomBox.setValue("MyServer!");

        this.btn1LabelBox.setValue("Github");
        this.btn1UrlBox.setValue("https://github.com/nogai3");
        this.btn2LabelBox.setValue("Modrinth");
        this.btn2UrlBox.setValue("https://modrinth.com/nogai3");

        this.iconPicker.setSelected(Identifier.tryParse("discord:gui/discord/icons/default"));
    }

    private static <T> void set(ForgeConfigSpec.ConfigValue<T> v, T value) {
        v.set(value);
    }

    private static void set(ForgeConfigSpec.EnumValue<?> v, Enum<?> value) {
        ((ForgeConfigSpec.EnumValue) v).set(value);
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