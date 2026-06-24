package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
import com.mahghuuuls.leftclickvacation.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HudNotifier {

    private String message;
    private int ticksRemaining;

    public void show(AutomationState state, DisableReason reason) {
        ConfigValues config = ModConfig.values();
        if (state == AutomationState.ENABLED) {
            if (!config.showEnabledMessage()) {
                return;
            }
            showFixed("Left Click Vacation enabled.", config.fixedHudDurationSeconds());
            return;
        }

        if (!config.showDisabledMessage()) {
            return;
        }
        showFixed(disabledMessage(reason), config.fixedHudDurationSeconds());
    }

    private void showFixed(String message, int seconds) {
        this.message = message;
        this.ticksRemaining = seconds * 20;
    }

    private String disabledMessage(DisableReason reason) {
        switch (reason) {
            case TOGGLED_OFF:
                return "Left Click Vacation disabled.";
            case HELD_ITEM_REQUIRED:
                return "Left Click Vacation requires an item in your main hand.";
            case UNSUPPORTED_GAME_MODE:
                return "Left Click Vacation only works in Survival or Adventure.";
            case SERVER_SUPPORT_REQUIRED:
                return "Left Click Vacation requires server support.";
            case SERVER_DENIED:
                return "Left Click Vacation was denied by the server.";
            case NONE:
            default:
                return "Left Click Vacation disabled.";
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ticksRemaining <= 0) {
            return;
        }
        ticksRemaining--;
        if (ticksRemaining <= 0) {
            message = null;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || message == null || ticksRemaining <= 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.world == null) {
            return;
        }

        ScaledResolution resolution = event.getResolution();
        int width = minecraft.fontRenderer.getStringWidth(message);
        int x = (resolution.getScaledWidth() - width) / 2;
        int y = resolution.getScaledHeight() - 68;
        minecraft.fontRenderer.drawStringWithShadow(message, x, y, 0xFFFFFF);
    }
}
