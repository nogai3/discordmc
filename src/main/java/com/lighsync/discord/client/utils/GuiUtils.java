package com.lighsync.discord.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.compress.utils.Lists;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class GuiUtils {
    public static void drawStringShadow(GuiGraphics gui, Font font, String string, int x, int y, int color, int color2) {
        gui.drawString(font, string, x, y, color, false);
        gui.drawString(font, string, (x-1), (y-1), color2, false);
    }

    public static void drawStringShadow(GuiGraphics gui, Font font, Component string, int x, int y, int color, int color2) {
        gui.drawString(font, string, x, y, color, false);
        gui.drawString(font, string, (x-1), (y-1), color2, false);
    }

    public static void drawCenteredStringShadow(GuiGraphics gui, Font font, String string, int x, int y, int color, int color2) {
        gui.drawString(font, string, x - font.width(string) / 2, y, color, false);
        gui.drawString(font, string, (x-1) - font.width(string) / 2, y-1, color2, false);
    }

    public static void drawCenteredStringShadow(GuiGraphics gui, Font font, Component string, int x, int y, int color, int color2) {
        gui.drawString(font, string, x - font.width(string) / 2, y, color, false);
        gui.drawString(font, string, (x-1) - font.width(string) / 2, y - 1, color2, false);
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }

    public static void renderTooltip(GuiGraphics gui, Identifier location, List<MutableComponent> entries, int x, int y, int color, int maxWidth) {
        Matrix3x2fStack poseStack = gui.pose();

        List<FormattedCharSequence> tooltip = Lists.newArrayList();
        int renderWidth = 0;

        for (MutableComponent entry : entries) {
            int entryWidth = (Minecraft.getInstance().font.width(entry) + 4) / 2;

            if (entryWidth > renderWidth) renderWidth = Math.min(entryWidth, maxWidth);
            tooltip.addAll(Minecraft.getInstance().font.split(entry, maxWidth * 2));
        }
        int height = Math.round(tooltip.size() * 5F);

        int renderX = x + 1;
        int renderY = y - (height / 2) - 9;

        poseStack.pushMatrix();
        // drawTexturedTooltipBorder(gui, location, renderWidth, height-6, renderX, renderY+3);

        int yOff = 0;
        poseStack.scale(0.5F, 0.5F);
        for (FormattedCharSequence entry : tooltip) {
            gui.drawString(Minecraft.getInstance().font, entry, (renderX + 10) * 2, (renderY + 9 + yOff) * 2 + 2, color, false);
            yOff += 5;
        }

        poseStack.scale(1F, 1F);
        poseStack.popMatrix();
    }

    public static void drawLineFromSprite(GuiGraphics gui, Identifier location, int x, int y, int textureX, int textureY, int spriteWidth, int spriteHeight, int textureSizeX, int textureSizeY, int length) {
        int leftWidth = 1;
        int rightWidth = 1;
        int middleWidth = spriteWidth - leftWidth - rightWidth;

        int minLineLength = leftWidth + rightWidth;

        if (length < minLineLength) length = minLineLength;

        gui.blit(location, x, y, textureX, textureY, leftWidth, spriteHeight, textureSizeX, textureSizeY);

        int remainingLength = length - leftWidth - rightWidth;
        int middleX = x + leftWidth;
        while (remainingLength > 0) {
            int drawWidth = Math.min(remainingLength, middleWidth);
            gui.blit(location, middleX, y, textureX + leftWidth, textureY, drawWidth, spriteHeight, 512, 512);
            middleX += drawWidth;
            remainingLength -= drawWidth;
        }

        gui.blit(location, middleX, y, textureX + leftWidth + middleWidth, textureY, rightWidth, spriteHeight, 512, 512);
    }
}