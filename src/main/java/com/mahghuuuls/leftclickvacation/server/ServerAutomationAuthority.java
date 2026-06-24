package com.mahghuuuls.leftclickvacation.server;

import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
import com.mahghuuuls.leftclickvacation.common.network.MessageAutomationState;
import com.mahghuuuls.leftclickvacation.common.network.MessageServerSupport;
import com.mahghuuuls.leftclickvacation.common.network.MessageToggleRequest;
import com.mahghuuuls.leftclickvacation.common.network.NetworkHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ServerAutomationAuthority {

    private static final Map<UUID, AutomationState> STATES = new HashMap<UUID, AutomationState>();

    public static void handleToggleRequest(EntityPlayerMP player, MessageToggleRequest request) {
        if (!request.isEnableRequested()) {
            disable(player, DisableReason.TOGGLED_OFF);
            return;
        }

        DisableReason rejection = getEnableRejection(player, request.getSelectedHotbarSlot());
        if (rejection != DisableReason.NONE) {
            STATES.put(player.getUniqueID(), AutomationState.DISABLED);
            sendState(player, AutomationState.DISABLED, rejection);
            return;
        }

        STATES.put(player.getUniqueID(), AutomationState.ENABLED);
        sendState(player, AutomationState.ENABLED, DisableReason.NONE);
    }

    private static DisableReason getEnableRejection(EntityPlayerMP player, int selectedHotbarSlot) {
        if (selectedHotbarSlot < 0 || selectedHotbarSlot > 8 || player.inventory.currentItem != selectedHotbarSlot) {
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

    private static boolean isSupportedGameMode(EntityPlayerMP player) {
        GameType gameType = player.interactionManager.getGameType();
        return gameType == GameType.SURVIVAL || gameType == GameType.ADVENTURE;
    }

    private static void disable(EntityPlayerMP player, DisableReason reason) {
        STATES.put(player.getUniqueID(), AutomationState.DISABLED);
        sendState(player, AutomationState.DISABLED, reason);
    }

    private static void sendState(EntityPlayerMP player, AutomationState state, DisableReason reason) {
        NetworkHandler.sendTo(new MessageAutomationState(state, reason), player);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            NetworkHandler.sendTo(new MessageServerSupport(true), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        STATES.remove(event.player.getUniqueID());
    }
}
