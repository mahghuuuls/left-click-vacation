package com.mahghuuuls.leftclickvacation.server;

import com.mahghuuuls.leftclickvacation.common.ActivationBinding;
import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.AutomationRules;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
import com.mahghuuuls.leftclickvacation.common.network.MessageAutomationState;
import com.mahghuuuls.leftclickvacation.common.network.MessageServerSupport;
import com.mahghuuuls.leftclickvacation.common.network.MessageToggleRequest;
import com.mahghuuuls.leftclickvacation.common.network.NetworkHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerAutomationAuthority {

    private static final Map<UUID, ServerSession> SESSIONS = new HashMap<UUID, ServerSession>();

    public static void handleToggleRequest(EntityPlayerMP player, MessageToggleRequest request) {
        if (!request.isEnableRequested()) {
            disable(player, DisableReason.TOGGLED_OFF);
            return;
        }

        DisableReason rejection = AutomationRules.getEnableRejection(player, request.getSelectedHotbarSlot());
        if (rejection != DisableReason.NONE) {
            SESSIONS.remove(player.getUniqueID());
            sendState(player, AutomationState.DISABLED, rejection);
            return;
        }

        ItemStack activationStack = player.inventory.getStackInSlot(request.getSelectedHotbarSlot());
        SESSIONS.put(player.getUniqueID(), new ServerSession(ActivationBinding.bind(
                request.getSelectedHotbarSlot(),
                activationStack)));
        sendState(player, AutomationState.ENABLED, DisableReason.NONE);
    }

    private static void disable(EntityPlayerMP player, DisableReason reason) {
        ServerSession session = SESSIONS.remove(player.getUniqueID());
        if (session == null) {
            return;
        }

        session.disable();
        if (player.connection != null) {
            sendState(player, AutomationState.DISABLED, reason);
        }
    }

    private static void sendState(EntityPlayerMP player, AutomationState state, DisableReason reason) {
        NetworkHandler.sendTo(new MessageAutomationState(state, reason), player);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            SESSIONS.remove(event.player.getUniqueID());
            NetworkHandler.sendTo(new MessageServerSupport(true), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SESSIONS.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote
                || !(event.player instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ServerSession session = SESSIONS.get(player.getUniqueID());
        if (session == null || session.state() != AutomationState.ENABLED) {
            return;
        }

        DisableReason invalidation = AutomationRules.getActiveSessionInvalidation(player, session.binding());
        if (invalidation != DisableReason.NONE) {
            disable(player, invalidation);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            disable((EntityPlayerMP) event.getEntityLiving(), DisableReason.PLAYER_DIED);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            disable((EntityPlayerMP) event.player, DisableReason.DIMENSION_CHANGED);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote || SESSIONS.isEmpty()) {
            return;
        }

        for (EntityPlayer player : event.getWorld().playerEntities) {
            if (player instanceof EntityPlayerMP) {
                disable((EntityPlayerMP) player, DisableReason.WORLD_UNLOADED);
            }
        }
    }
}
