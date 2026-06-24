package com.mahghuuuls.leftclickvacation.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ActivationBinding {

    private final int hotbarSlot;
    private final ItemStack activationStack;

    private ActivationBinding(int hotbarSlot, ItemStack activationStack) {
        this.hotbarSlot = hotbarSlot;
        this.activationStack = activationStack;
    }

    public static ActivationBinding bind(int hotbarSlot, ItemStack activationStack) {
        return new ActivationBinding(hotbarSlot, activationStack);
    }

    public int hotbarSlot() {
        return hotbarSlot;
    }

    public boolean isActivationItemInPossession(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (isActivationStack(stack)) {
                return true;
            }
        }

        for (ItemStack stack : player.inventory.offHandInventory) {
            if (isActivationStack(stack)) {
                return true;
            }
        }

        return false;
    }

    public boolean isActivationItemInBoundSlot(EntityPlayer player) {
        return isActivationStack(player.inventory.getStackInSlot(hotbarSlot));
    }

    private boolean isActivationStack(ItemStack currentStack) {
        return !currentStack.isEmpty() && currentStack == activationStack;
    }
}
