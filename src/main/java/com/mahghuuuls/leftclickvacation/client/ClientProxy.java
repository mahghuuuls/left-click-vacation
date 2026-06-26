package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.serveronly.ServerProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy {

    private final ClientAutomationController automationController = new ClientAutomationController();
    private final ClientBlockBreakDriver blockBreakDriver = new ClientBlockBreakDriver(automationController);
    private final ClientConfigEventHandler configEventHandler = new ClientConfigEventHandler();
    private final HudNotifier hudNotifier = new HudNotifier();
    private final KeyBindingHandler keyBindingHandler = new KeyBindingHandler(automationController);

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.registerKeyBinding(keyBindingHandler.getToggleKey());
        MinecraftForge.EVENT_BUS.register(keyBindingHandler);
        MinecraftForge.EVENT_BUS.register(automationController);
        MinecraftForge.EVENT_BUS.register(blockBreakDriver);
        MinecraftForge.EVENT_BUS.register(configEventHandler);
        MinecraftForge.EVENT_BUS.register(hudNotifier);
        automationController.setHudNotifier(hudNotifier);
    }
}
