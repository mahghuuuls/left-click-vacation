package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
import com.mahghuuuls.leftclickvacation.common.network.MessageToggleRequest;
import com.mahghuuuls.leftclickvacation.common.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientAutomationController {

    private boolean serverSupported;
    private AutomationState state = AutomationState.DISABLED;
    private ClientActivationBinding pendingBinding;
    private ClientActivationBinding activeBinding;
    private HudNotifier hudNotifier;

    public void setHudNotifier(HudNotifier hudNotifier) {
        this.hudNotifier = hudNotifier;
    }

    public void setServerSupported(boolean serverSupported) {
        this.serverSupported = serverSupported;
    }

    public void onToggleKeyPressed() {
        if (state == AutomationState.ENABLED) {
            if (isActivationItemSelected()) {
                pendingBinding = null;
                activeBinding = null;
                NetworkHandler.sendToServer(new MessageToggleRequest(false, getSelectedHotbarSlot()));
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
            pendingBinding = null;
            if (clearActiveBindingOnRejection) {
                activeBinding = null;
            }
            showState(AutomationState.DISABLED, localRejection);
            return;
        }

        int selectedHotbarSlot = getSelectedHotbarSlot();
        pendingBinding = bindCurrentSelection(selectedHotbarSlot);
        NetworkHandler.sendToServer(new MessageToggleRequest(true, selectedHotbarSlot));
    }

    public void applyServerState(AutomationState state, DisableReason reason) {
        this.state = state;
        if (state == AutomationState.ENABLED) {
            activeBinding = pendingBinding != null ? pendingBinding : bindCurrentSelection(getSelectedHotbarSlot());
        } else {
            activeBinding = null;
        }
        pendingBinding = null;
        showState(state, reason);
    }

    public boolean isEnabled() {
        return state == AutomationState.ENABLED;
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

    private DisableReason getLocalEnableRejection() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.world == null) {
            return DisableReason.SERVER_DENIED;
        }

        if (!serverSupported) {
            return DisableReason.SERVER_SUPPORT_REQUIRED;
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
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        serverSupported = false;
        state = AutomationState.DISABLED;
        pendingBinding = null;
        activeBinding = null;
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        serverSupported = false;
        state = AutomationState.DISABLED;
        pendingBinding = null;
        activeBinding = null;
    }
}
