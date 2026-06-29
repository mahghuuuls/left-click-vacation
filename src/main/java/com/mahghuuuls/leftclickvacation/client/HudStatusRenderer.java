package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import com.mahghuuuls.leftclickvacation.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HudStatusRenderer {

    private static final int ICON_SIZE = 16;
    private static final int TEXT_Y = 18;
    private static final int BASE_HEIGHT = 28;
    private static final int ON_COLOR = 0x55FF55;
    private static final int OFF_COLOR = 0xFF5555;

    private final ClientAutomationController automationController;

    public HudStatusRenderer(ClientAutomationController automationController) {
        this.automationController = automationController;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || !automationController.isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.world == null) {
            return;
        }

        ItemStack stack = automationController.getArmedItemStack();
        if (stack.isEmpty()) {
            return;
        }

        ConfigValues config = ModConfig.values();
        String statusText = automationController.isAutoClickActive() ? "ON" : "OFF";
        int color = automationController.isAutoClickActive() ? ON_COLOR : OFF_COLOR;
        int textWidth = minecraft.fontRenderer.getStringWidth(statusText);
        int baseWidth = Math.max(ICON_SIZE, textWidth);
        float scale = config.hudScalePercent() / 100.0F;

        ScaledResolution resolution = event.getResolution();
        int x = clampToScreen(config.hudX(), resolution.getScaledWidth(), scaledSize(baseWidth, scale));
        int y = clampToScreen(config.hudY(), resolution.getScaledHeight(), scaledSize(BASE_HEIGHT, scale));

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        renderItem(minecraft, stack, (baseWidth - ICON_SIZE) / 2, 0);
        minecraft.fontRenderer.drawStringWithShadow(statusText, (baseWidth - textWidth) / 2, TEXT_Y, color);
        GlStateManager.popMatrix();
    }

    private void renderItem(Minecraft minecraft, ItemStack stack, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    private int scaledSize(int value, float scale) {
        return Math.max(1, Math.round(value * scale));
    }

    private int clampToScreen(int value, int screenSize, int componentSize) {
        int max = Math.max(0, screenSize - componentSize);
        if (value < 0) {
            return 0;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
