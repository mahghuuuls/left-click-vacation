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
            showFixed("Auto click enabled.", config.fixedHudDurationSeconds());
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
                return "Auto click disabled.";
            case HELD_ITEM_REQUIRED:
                return "Hold an item to enable auto click.";
            case UNSUPPORTED_GAME_MODE:
                return "Auto click only works in Survival or Adventure.";
            case ACTIVATION_ITEM_LOST:
                return "Auto click disabled: item changed.";
            case PLAYER_DIED:
                return "Auto click disabled: you died.";
            case DIMENSION_CHANGED:
                return "Auto click disabled: dimension changed.";
            case WORLD_UNLOADED:
                return "Auto click disabled: world unloaded.";
            case NONE:
            default:
                return "Auto click disabled.";
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
