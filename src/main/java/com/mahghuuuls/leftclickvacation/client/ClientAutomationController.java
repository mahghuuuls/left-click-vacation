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
    private boolean modEnabled;
    private ClientActivationBinding activeBinding;
    private Integer activeDimension;
    private HudNotifier hudNotifier;

    public void setHudNotifier(HudNotifier hudNotifier) {
        this.hudNotifier = hudNotifier;
    }

    public void onToggleKeyPressed() {
        if (modEnabled) {
            disableMod(DisableReason.TOGGLED_OFF);
            return;
        }

        enableMod();
    }

    public void onLeftMousePressed() {
        if (!modEnabled || isTargetingEntity()) {
            return;
        }

        if (!isActivationItemSelected()) {
            return;
        }

        if (state != AutomationState.DISABLED) {
            applyLocalState(AutomationState.DISABLED, DisableReason.TOGGLED_OFF, null);
            return;
        }

        applyLocalState(AutomationState.ENABLED, DisableReason.NONE, activeBinding);
    }

    private void enableMod() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.world == null) {
            return;
        }

        DisableReason localRejection = getLocalEnableRejection();
        if (localRejection != DisableReason.NONE) {
            activeBinding = null;
            showState(AutomationState.DISABLED, localRejection);
            return;
        }

        ClientActivationBinding binding = bindCurrentSelection(getSelectedHotbarSlot());
        if (binding == null) {
            activeBinding = null;
            showState(AutomationState.DISABLED, DisableReason.HELD_ITEM_REQUIRED);
            return;
        }

        modEnabled = true;
        activeBinding = binding;
        activeDimension = getCurrentDimension();
        if (hudNotifier != null) {
            hudNotifier.showModEnabled();
        }
    }

    private void disableMod(DisableReason reason) {
        modEnabled = false;
        if (state != AutomationState.DISABLED) {
            applyLocalState(AutomationState.DISABLED, reason, null);
            return;
        }

        activeBinding = null;
        activeDimension = null;
        showState(AutomationState.DISABLED, reason);
    }

    private void applyLocalState(AutomationState state, DisableReason reason, ClientActivationBinding binding) {
        this.state = state;
        if (state == AutomationState.ENABLED) {
            activeBinding = binding;
            activeDimension = getCurrentDimension();
        } else {
            if (!modEnabled) {
                activeBinding = null;
                activeDimension = null;
            }
        }
        showState(state, reason);
    }

    private void disableIfEnabled(DisableReason reason) {
        if (state != AutomationState.DISABLED) {
            applyLocalState(AutomationState.DISABLED, reason, null);
        }
    }

    public boolean isEnabled() {
        return modEnabled;
    }

    public boolean isAutoClickActive() {
        return state == AutomationState.ENABLED;
    }

    public ItemStack getArmedItemStack() {
        if (!modEnabled || activeBinding == null) {
            return ItemStack.EMPTY;
        }
        return activeBinding.activationStack();
    }

    public boolean isActivationItemSelected() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        return modEnabled
                && player != null
                && activeBinding != null
                && player.inventory.currentItem == activeBinding.hotbarSlot()
                && activeBinding.matchesBoundSlot(player.inventory.getStackInSlot(activeBinding.hotbarSlot()));
    }

    public boolean isActiveAndActivationItemSelected() {
        return state == AutomationState.ENABLED && isActivationItemSelected();
    }

    private void validateActiveSession() {
        if (!modEnabled && state == AutomationState.DISABLED) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.world == null) {
            disableMod(DisableReason.WORLD_UNLOADED);
            return;
        }

        if (player.isDead || player.getHealth() <= 0.0F) {
            disableMod(DisableReason.PLAYER_DIED);
            return;
        }

        if (activeDimension != null && player.dimension != activeDimension.intValue()) {
            disableMod(DisableReason.DIMENSION_CHANGED);
            return;
        }

        if (!isSupportedGameMode(minecraft)) {
            disableMod(DisableReason.UNSUPPORTED_GAME_MODE);
            return;
        }

        if (state == AutomationState.DISABLED) {
            if (activeBinding == null || !activeBinding.isActivationItemInPossession(player)) {
                disableMod(DisableReason.ACTIVATION_ITEM_LOST);
            }
            return;
        }

        if (activeBinding == null || !activeBinding.isActivationItemInPossession(player)) {
            disableIfEnabled(DisableReason.ACTIVATION_ITEM_LOST);
            return;
        }

        if (isActivationItemSelected()) {
            return;
        }

        if (state == AutomationState.ENABLED) {
            applyLocalState(AutomationState.DISABLED, DisableReason.ACTIVATION_ITEM_LOST, null);
            return;
        }

        applyLocalState(AutomationState.DISABLED, DisableReason.ACTIVATION_ITEM_LOST, null);
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

    private boolean isTargetingEntity() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft.objectMouseOver != null
                && minecraft.objectMouseOver.typeOfHit == net.minecraft.util.math.RayTraceResult.Type.ENTITY;
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
        modEnabled = false;
        state = AutomationState.DISABLED;
        activeBinding = null;
        activeDimension = null;
    }

    @SubscribeEvent
    public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (modEnabled || state != AutomationState.DISABLED) {
            disableMod(DisableReason.WORLD_UNLOADED);
        }
    }
}
