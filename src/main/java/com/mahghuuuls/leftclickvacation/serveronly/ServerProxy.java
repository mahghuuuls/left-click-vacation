package com.mahghuuuls.leftclickvacation.serveronly;

import com.mahghuuuls.leftclickvacation.common.network.MessageAutomationState;
import com.mahghuuuls.leftclickvacation.common.network.MessageServerSupport;
import com.mahghuuuls.leftclickvacation.common.network.NetworkHandler;
import com.mahghuuuls.leftclickvacation.server.MessageToggleRequestHandler;
import com.mahghuuuls.leftclickvacation.server.ServerAutomationAuthority;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NetworkHandler.registerServerMessages(MessageToggleRequestHandler.class);
        MinecraftForge.EVENT_BUS.register(new ServerAutomationAuthority());
    }

    public void handleServerSupport(MessageServerSupport message) {
    }

    public void handleAutomationState(MessageAutomationState message) {
    }
}
