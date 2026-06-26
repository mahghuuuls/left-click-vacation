package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
import com.mahghuuuls.leftclickvacation.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientAutomationController {

    private AutomationState state = AutomationState.DISABLED;
    private ClientActivationBinding activeBinding;
    private Integer activeDimension;
    private int graceTicksRemaining;
    private HudNotifier hudNotifier;

    public void setHudNotifier(HudNotifier hudNotifier) {
        this.hudNotifier = hudNotifier;
    }

    public void onToggleKeyPressed() {
        if (state != AutomationState.DISABLED) {
            if (isActivationItemSelected()) {
                applyLocalState(AutomationState.DISABLED, DisableReason.TOGGLED_OFF, null);
                return;
            }

            requestEnableForCurrentSelection(false);
            return;
        }

        requestEnableForCurrentSelection(true);
    }

    private void requestEnableForCurrentSelection(boolean clearActiveBindingOnRejection) {
        DisableReason localRejection = getLocalEnableRejection();
        if (localRejection != DisableReason.NONE) {
            if (clearActiveBindingOnRejection) {
                activeBinding = null;
            }
            if (state == AutomationState.PAUSED) {
                showState(AutomationState.PAUSED, DisableReason.NONE);
            } else {
                showState(AutomationState.DISABLED, localRejection);
            }
            return;
        }

        int selectedHotbarSlot = getSelectedHotbarSlot();
        ClientActivationBinding binding = bindCurrentSelection(selectedHotbarSlot);
        if (binding == null) {
            if (clearActiveBindingOnRejection) {
                activeBinding = null;
            }
            if (state == AutomationState.PAUSED) {
                showState(AutomationState.PAUSED, DisableReason.NONE);
            } else {
                showState(AutomationState.DISABLED, DisableReason.HELD_ITEM_REQUIRED);
            }
            return;
        }

        applyLocalState(AutomationState.ENABLED, DisableReason.NONE, binding);
    }

    private void applyLocalState(AutomationState state, DisableReason reason, ClientActivationBinding binding) {
        this.state = state;
        if (state == AutomationState.ENABLED) {
            activeBinding = binding;
            activeDimension = getCurrentDimension();
            graceTicksRemaining = 0;
        } else if (state == AutomationState.PAUSED) {
            graceTicksRemaining = configuredGraceTicks();
        } else {
            activeBinding = null;
            activeDimension = null;
            graceTicksRemaining = 0;
        }
        showState(state, reason);
    }

    private void resumeLocalState() {
        state = AutomationState.ENABLED;
        graceTicksRemaining = 0;
        if (hudNotifier != null) {
            hudNotifier.clearPaused();
        }
    }

    private void disableIfEnabled(DisableReason reason) {
        if (state != AutomationState.DISABLED) {
            applyLocalState(AutomationState.DISABLED, reason, null);
        }
    }

    public boolean isEnabled() {
        return state != AutomationState.DISABLED;
    }

    public boolean isActivationItemSelected() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        return isEnabled()
                && player != null
                && activeBinding != null
                && player.inventory.currentItem == activeBinding.hotbarSlot()
                && activeBinding.matchesBoundSlot(player.inventory.getStackInSlot(activeBinding.hotbarSlot()));
    }

    public boolean isActiveAndActivationItemSelected() {
        return state == AutomationState.ENABLED && isActivationItemSelected();
    }

    private void validateActiveSession() {
        if (!isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.world == null) {
            disableIfEnabled(DisableReason.WORLD_UNLOADED);
            return;
        }

        if (!isSupportedGameMode(minecraft)) {
            disableIfEnabled(DisableReason.UNSUPPORTED_GAME_MODE);
            return;
        }

        if (player.isDead || player.getHealth() <= 0.0F) {
            disableIfEnabled(DisableReason.PLAYER_DIED);
            return;
        }

        if (activeDimension != null && player.dimension != activeDimension.intValue()) {
            disableIfEnabled(DisableReason.DIMENSION_CHANGED);
            return;
        }

        if (activeBinding == null || !activeBinding.isActivationItemInPossession(player)) {
            disableIfEnabled(DisableReason.ACTIVATION_ITEM_LOST);
            return;
        }

        if (isActivationItemSelected()) {
            if (state == AutomationState.PAUSED) {
                resumeLocalState();
            }
            return;
        }

        if (state == AutomationState.ENABLED) {
            applyLocalState(AutomationState.PAUSED, DisableReason.NONE, activeBinding);
            return;
        }

        tickGracePeriod();
    }

    private void tickGracePeriod() {
        if (state != AutomationState.PAUSED) {
            return;
        }

        if (graceTicksRemaining > 0) {
            graceTicksRemaining--;
        }
        if (graceTicksRemaining <= 0) {
            disableIfEnabled(DisableReason.GRACE_EXPIRED);
        }
    }

    private DisableReason getLocalEnableRejection() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.world == null) {
            return DisableReason.UNSUPPORTED_GAME_MODE;
        }

        if (!isSupportedGameMode(minecraft)) {
            return DisableReason.UNSUPPORTED_GAME_MODE;
        }

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) {
            return DisableReason.HELD_ITEM_REQUIRED;
        }

        return DisableReason.NONE;
    }

    private boolean isSupportedGameMode(Minecraft minecraft) {
        if (minecraft.playerController == null) {
            return false;
        }
        GameType gameType = minecraft.playerController.getCurrentGameType();
        return gameType == GameType.SURVIVAL || gameType == GameType.ADVENTURE;
    }

    private int getSelectedHotbarSlot() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null) {
            return -1;
        }
        return minecraft.player.inventory.currentItem;
    }

    private Integer getCurrentDimension() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null) {
            return null;
        }
        return Integer.valueOf(minecraft.player.dimension);
    }

    private int configuredGraceTicks() {
        ConfigValues config = ModConfig.values();
        return config.gracePeriodSeconds() * 20;
    }

    private ClientActivationBinding bindCurrentSelection(int selectedHotbarSlot) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null) {
            return null;
        }

        ItemStack stack = minecraft.player.inventory.getStackInSlot(selectedHotbarSlot);
        if (stack.isEmpty()) {
            return null;
        }

        return ClientActivationBinding.bind(selectedHotbarSlot, stack);
    }

    private void showState(AutomationState state, DisableReason reason) {
        if (hudNotifier != null) {
            hudNotifier.show(state, reason);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            validateActiveSession();
        }
    }

    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        state = AutomationState.DISABLED;
        activeBinding = null;
        activeDimension = null;
        graceTicksRemaining = 0;
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        disableIfEnabled(DisableReason.WORLD_UNLOADED);
    }
}
