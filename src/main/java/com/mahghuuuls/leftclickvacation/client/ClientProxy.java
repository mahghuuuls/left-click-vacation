package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.common.network.MessageAutomationState;
import com.mahghuuuls.leftclickvacation.common.network.MessageServerSupport;
import com.mahghuuuls.leftclickvacation.serveronly.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy {

    private final ClientAutomationController automationController = new ClientAutomationController();
    private final HudNotifier hudNotifier = new HudNotifier();
    private final KeyBindingHandler keyBindingHandler = new KeyBindingHandler(automationController);

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.registerKeyBinding(keyBindingHandler.getToggleKey());
        MinecraftForge.EVENT_BUS.register(keyBindingHandler);
        MinecraftForge.EVENT_BUS.register(automationController);
        MinecraftForge.EVENT_BUS.register(hudNotifier);
        automationController.setHudNotifier(hudNotifier);
    }

    @Override
    public void handleServerSupport(final MessageServerSupport message) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                automationController.setServerSupported(message.isSupported());
            }
        });
    }

    @Override
    public void handleAutomationState(final MessageAutomationState message) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                automationController.applyServerState(message.getState(), message.getReason());
            }
        });
    }
}
