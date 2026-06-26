package com.mahghuuuls.leftclickvacation.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ClientActivationBinding {

    private final int hotbarSlot;
    private final ItemStack activationSnapshot;

    private ClientActivationBinding(int hotbarSlot, ItemStack activationSnapshot) {
        this.hotbarSlot = hotbarSlot;
        this.activationSnapshot = activationSnapshot;
    }

    public static ClientActivationBinding bind(int hotbarSlot, ItemStack activationStack) {
        return new ClientActivationBinding(hotbarSlot, activationStack.copy());
    }

    public int hotbarSlot() {
        return hotbarSlot;
    }

    public boolean isActivationItemInPossession(EntityPlayer player) {
        if (matchesActivationItem(player.inventory.getItemStack())) {
            return true;
        }

        for (ItemStack stack : player.inventory.mainInventory) {
            if (matchesActivationItem(stack)) {
                return true;
            }
        }

        for (ItemStack stack : player.inventory.offHandInventory) {
            if (matchesActivationItem(stack)) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesBoundSlot(ItemStack currentStack) {
        return matchesActivationItem(currentStack);
    }

    private boolean matchesActivationItem(ItemStack currentStack) {
        if (currentStack.isEmpty()) {
            return false;
        }

        if (currentStack.getItem() != activationSnapshot.getItem()) {
            return false;
        }

        if (!currentStack.isItemStackDamageable()
                && currentStack.getMetadata() != activationSnapshot.getMetadata()) {
            return false;
        }

        return ItemStack.areItemStackTagsEqual(currentStack, activationSnapshot);
    }
}
