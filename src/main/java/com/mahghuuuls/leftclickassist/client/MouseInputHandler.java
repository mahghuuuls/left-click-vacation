package com.mahghuuuls.leftclickassist.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class MouseInputHandler {

    private final ClientAutomationController automationController;
    private boolean wasLeftButtonDown;

    public MouseInputHandler(ClientAutomationController automationController) {
        this.automationController = automationController;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        boolean leftButtonDown = Mouse.isButtonDown(0);
        if (minecraft.currentScreen != null) {
            wasLeftButtonDown = leftButtonDown;
            return;
        }

        if (leftButtonDown && !wasLeftButtonDown) {
            automationController.onLeftMousePressed();
        }
        wasLeftButtonDown = leftButtonDown;
    }
}
