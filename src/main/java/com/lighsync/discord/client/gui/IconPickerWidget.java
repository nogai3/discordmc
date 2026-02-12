package com.lighsync.discord.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IconPickerWidget extends AbstractWidget {
    private final List<Identifier> icons;
    private Identifier selected;

    private final int cell = 18;
    private final int pad = 4;

    public IconPickerWidget(int x, int y, int width, int height, List<Identifier> icons, Identifier selected) {
        super(x, y, width, height, Component.empty());
        this.icons = icons;
        this.selected = selected;
    }

    public Identifier getSelected() {
        return selected;
    }

    public void setSelected(Identifier id) {
        this.selected = id;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        gui.fill(getX(), getY(), getX() + width, getY() + height, 0x55000000);
        gui.renderOutline(getX(), getY(), width, height, 0xFF404040);

        int cols = Math.max(1, (width - pad * 2) / cell);
        int startX = getX() + pad;
        int startY = getY() + pad;

        for (int i = 0; i < icons.size(); i++) {
            int cx = i % cols;
            int cy = i / cols;
            int ix = startX + cx * cell;
            int iy = startY + cy * cell;

            if (iy + cell > getY() + height) break;

            Identifier id = icons.get(i);

            if (id != null && id.equals(selected)) {
                gui.fill(ix - 1, iy - 1, ix + 17, iy + 17, 0x66FFFFFF);
                gui.renderOutline(ix - 1, iy - 1, 18, 18, 0xFFFFFFFF);
            }

            gui.blit(id, ix, iy, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean flag) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (!this.active || !this.visible) return false;
        if (!this.isMouseOver(mouseX, mouseY)) return false;

        int cols = Math.max(1, (width - pad * 2) / cell);
        int startX = getX() + pad;
        int startY = getY() + pad;

        int rx = (int) mouseX - startX;
        int ry = (int) mouseY - startY;
        if (rx < 0 || ry < 0) return false;

        int cx = rx / cell;
        int cy = ry / cell;
        int index = cy * cols + cx;


        if (index >= 0 && index < icons.size()) {
            this.selected = icons.get(index);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {}

    public static List<Identifier> scanIcons (String modId, String texturesPath) {
        var rm = Minecraft.getInstance().getResourceManager();

        var found = rm.listResources(texturesPath, p -> p.getPath().endsWith(".png"));

        List<Identifier> out = new ArrayList<>();
        for (Identifier res : found.keySet()) {
            String path = res.getPath();

            if (path.startsWith("textures/")) path = path.substring("textures/".length());
            if (path.endsWith(".png")) path = path.substring(0, path.length() - 4);

            out.add(Identifier.fromNamespaceAndPath(res.getNamespace(), path));
        }
        out.sort(Comparator.comparing(Identifier::toString));
        return out;
    }
}
