package com.mahghuuuls.leftclickvacation.client;

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

    public boolean matchesBoundSlot(ItemStack currentStack) {
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
