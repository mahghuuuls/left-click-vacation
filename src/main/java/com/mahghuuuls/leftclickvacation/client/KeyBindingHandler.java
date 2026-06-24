package com.mahghuuuls.leftclickvacation.client;

import com.mahghuuuls.leftclickvacation.Tags;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class KeyBindingHandler {

    private static final String CATEGORY = "key.categories." + Tags.MOD_ID;

    private final ClientAutomationController automationController;
    private final KeyBinding toggleKey = new KeyBinding(
            "key." + Tags.MOD_ID + ".toggle",
            Keyboard.KEY_NONE,
            CATEGORY);

    public KeyBindingHandler(ClientAutomationController automationController) {
        this.automationController = automationController;
    }

    public KeyBinding getToggleKey() {
        return toggleKey;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        while (toggleKey.isPressed()) {
            automationController.onToggleKeyPressed();
        }
    }
}
