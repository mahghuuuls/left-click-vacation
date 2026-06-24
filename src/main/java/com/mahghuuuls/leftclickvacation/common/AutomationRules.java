package com.mahghuuuls.leftclickvacation.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;

public final class AutomationRules {

    private AutomationRules() {
    }

    public static DisableReason getEnableRejection(EntityPlayerMP player, int selectedHotbarSlot) {
        if (!isValidHotbarSlot(selectedHotbarSlot) || player.inventory.currentItem != selectedHotbarSlot) {
            return DisableReason.SERVER_DENIED;
        }

        if (!isSupportedGameMode(player)) {
            return DisableReason.UNSUPPORTED_GAME_MODE;
        }

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) {
            return DisableReason.HELD_ITEM_REQUIRED;
        }

        return DisableReason.NONE;
    }

    public static DisableReason getActiveSessionInvalidation(EntityPlayerMP player, ActivationBinding binding) {
        if (!isSupportedGameMode(player)) {
            return DisableReason.UNSUPPORTED_GAME_MODE;
        }

        if (!binding.isActivationItemInPossession(player)) {
            return DisableReason.ACTIVATION_ITEM_LOST;
        }

        return DisableReason.NONE;
    }

    private static boolean isValidHotbarSlot(int slot) {
        return slot >= 0 && slot <= 8;
    }

    private static boolean isSupportedGameMode(EntityPlayerMP player) {
        GameType gameType = player.interactionManager.getGameType();
        return gameType == GameType.SURVIVAL || gameType == GameType.ADVENTURE;
    }
}
