/*
 *       Copyright (C) 2018-present Hyperium <https://hyperium.cc/>
 *
 *       This program is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published
 *       by the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cc.hyperium.gui;

import cc.hyperium.Hyperium;
import cc.hyperium.Metadata;
import cc.hyperium.config.Settings;
import cc.hyperium.gui.playerrenderer.GuiPlayerRenderer;
import cc.hyperium.purchases.PurchaseApi;
import cc.hyperium.styles.GuiStyle;
import cc.hyperium.utils.ChatColor;
import cc.hyperium.utils.HyperiumFontRenderer;
import cc.hyperium.utils.JsonHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GuiHyperiumScreen extends GuiScreen {

    public static ResourceLocation background = new ResourceLocation("textures/material/backgrounds/1.png");
    public static boolean customBackground;
    public static File customImage = new File(Minecraft.getMinecraft().mcDataDir, "customImage.png");
    public static ResourceLocation bgDynamicTexture;
    public static BufferedImage bgBr;
    public static HyperiumFontRenderer fr = new HyperiumFontRenderer("Arial", Font.PLAIN, 20);
    public static HyperiumFontRenderer sfr = new HyperiumFontRenderer("Arial", Font.PLAIN, 12);
    private static float swing;
    public GuiButton hypixelButton;

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_FASTEST);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
    }

    public static ResourceLocation getBackground() {
        return background;
    }

    public static void setBackground(ResourceLocation givenBackground) {
        background = givenBackground;
    }

    public static void setCustomBackground(boolean givenBoolean) {
        customBackground = givenBoolean;
    }

    public void initGui() {
        customBackground = Settings.BACKGROUND.equalsIgnoreCase("CUSTOM");
        if (customImage.exists() && customBackground) {
            try {
                bgBr = ImageIO.read(new FileInputStream(customImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bgBr != null)
                bgDynamicTexture = mc.getRenderManager().renderEngine.getDynamicTextureLocation(customImage.getName(), new DynamicTexture(bgBr));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        switch (getStyle()) {
            case DEFAULT:
                drawDefaultStyleScreen(mouseX, mouseY);
                break;
            case HYPERIUM:
                drawHyperiumStyleScreen(mouseX, mouseY, partialTicks);
                break;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public GuiStyle getStyle() {
        return GuiStyle.valueOf(Minecraft.getMinecraft().theWorld == null ? Settings.MENU_STYLE : Settings.PAUSE_STYLE);
    }

    public void renderHyperiumBackground(ScaledResolution resolution) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        if (customImage.exists() && bgDynamicTexture != null && customBackground) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(bgDynamicTexture);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(0.0D, resolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
            worldrenderer.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
            worldrenderer.pos(resolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
            worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(background);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, resolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(resolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawHyperiumStyleScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        swing++;

        if (mc.theWorld == null) {
            if (!Settings.BACKGROUND.equals("DEFAULT")) {
                GlStateManager.disableAlpha();
                ScaledResolution sr = new ScaledResolution(mc);
                this.renderHyperiumBackground(sr);
                GlStateManager.enableAlpha();
            }
        }

        /* Render shadowed bar at top of screen */
        if (mc.theWorld == null) {
            this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
            this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        } else {
            this.drawDefaultBackground();
        }

        drawRect(0, 4, width, 55, 0x33000000);
        drawRect(0, 5, width, 54, 0x33000000);

        /* Render Client Logo */
        GlStateManager.color(1, 1, 1, 1);
        ResourceLocation logo = new ResourceLocation("textures/hyperium-logo.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(logo);
        drawScaledCustomSizeModalRect(10, 5, 0, 0, 2160, 500, 200, 47, 2160, 500);

        /* Render profile container */
        drawRect(width - 155, 10, width - 10, 49, 0x33000000);
        drawRect(width - 156, 9, width - 9, 50, 0x33000000);


        /* Fetch player credit count */
        if (!Hyperium.INSTANCE.isDevEnv()) {
            if (PurchaseApi.getInstance() != null && PurchaseApi.getInstance().getSelf() != null) {
                JsonHolder response = PurchaseApi.getInstance().getSelf().getResponse();
                int credits = response.optInt("remaining_credits");

                /* Render player credits count and username */
                fr.drawString(Minecraft.getMinecraft().getSession().getUsername(), width - 153, 13, 0xFFFFFF);
                fr.drawString(I18n.format("menu.profile.credits", credits), width - 153, 25, 0xFFFF00);
            }
        }


        float val = (float) (Math.sin(swing / 40) * 30);

        ScissorState.scissor(width - 153, 0, 145, 49, true);

        if (Minecraft.getMinecraft().thePlayer != null) {
            GuiPlayerRenderer.renderPlayerWithRotation(width - 118, -4, val);
        }

        ScissorState.endScissor();

        /* Render Hyperium version number */
        fr.drawStringScaled("Hyperium v" + (Hyperium.INSTANCE.isLatestVersion ? ChatColor.GREEN : ChatColor.RED) + Metadata.getVersion(), width - 152, 39, 0xFFFFFF, .75);

        /* Display copyright disclaimers at bottom of screen */
        sfr.drawString(I18n.format("menu.left").toUpperCase(), 1, height - 7, 0x55FFFFFF);
        String s = I18n.format("menu.right").toUpperCase();
        sfr.drawString(s, width - sfr.getWidth(s) - 1, height - 7, 0x55FFFFFF);

        GlStateManager.popMatrix();

    }

    public void drawDefaultStyleScreen(int mouseX, int mouseY) {
        if (!Settings.BACKGROUND.equals("DEFAULT")) {
            GlStateManager.disableAlpha();
            this.renderHyperiumBackground(new ScaledResolution(mc));
        }

        ParticleOverlay.getOverlay().render(mouseX, mouseY, 0, 0, 0, 0);

        GlStateManager.enableAlpha();
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(4F, 4F, 1F);
        this.drawCenteredString(fontRendererObj, Metadata.getModid(), width / 8, 40 / 4, 0xFFFFFF);
        GlStateManager.popMatrix();
        String s = String.format("%s %s", Metadata.getModid(), Metadata.getVersion());
        this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);
        String s1 = I18n.format("menu.right");
        this.drawString(this.fontRendererObj, s1, this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, -1);
        String s3 = "Made by Sk1er, Kevin,";
        this.drawString(this.fontRendererObj, s3, this.width - this.fontRendererObj.getStringWidth(s3) - 2, this.height - 30, -1);

        String s4 = "Cubxity, CoalOres and boomboompower";
        this.drawString(this.fontRendererObj, s4, this.width - this.fontRendererObj.getStringWidth(s4) - 2, this.height - 20, -1);
        GuiButton hypixelButton = this.hypixelButton;
        if (hypixelButton != null)
            hypixelButton.displayString = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? I18n.format("button.ingame.fixhypixelsession") : I18n.format("button.ingame.joinhypixel");
    }

    public int getIntendedWidth(int value) {
        float intendedWidth = 1920F;
        return (int) ((Minecraft.getMinecraft().displayWidth / intendedWidth) * value);
    }

    public int getIntendedHeight(int value) {
        float intendedHeight = 1080F;
        return (int) ((Minecraft.getMinecraft().displayHeight / intendedHeight) * value);
    }

    @Override
    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    @Override
    public void drawWorldBackground(int tint) {
        if (this.mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }
}
