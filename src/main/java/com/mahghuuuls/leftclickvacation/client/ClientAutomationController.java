package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
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
    private HudNotifier hudNotifier;

    public void setHudNotifier(HudNotifier hudNotifier) {
        this.hudNotifier = hudNotifier;
    }

    public void onToggleKeyPressed() {
        if (state == AutomationState.ENABLED) {
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
            showState(AutomationState.DISABLED, localRejection);
            return;
        }

        int selectedHotbarSlot = getSelectedHotbarSlot();
        ClientActivationBinding binding = bindCurrentSelection(selectedHotbarSlot);
        if (binding == null) {
            if (clearActiveBindingOnRejection) {
                activeBinding = null;
            }
            showState(AutomationState.DISABLED, DisableReason.HELD_ITEM_REQUIRED);
            return;
        }

        applyLocalState(AutomationState.ENABLED, DisableReason.NONE, binding);
    }

    private void applyLocalState(AutomationState state, DisableReason reason, ClientActivationBinding binding) {
        this.state = state;
        if (state == AutomationState.ENABLED) {
            activeBinding = binding;
            activeDimension = getCurrentDimension();
        } else {
            activeBinding = null;
            activeDimension = null;
        }
        showState(state, reason);
    }

    private void disableIfEnabled(DisableReason reason) {
        if (isEnabled()) {
            applyLocalState(AutomationState.DISABLED, reason, null);
        }
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
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        disableIfEnabled(DisableReason.WORLD_UNLOADED);
    }
}
