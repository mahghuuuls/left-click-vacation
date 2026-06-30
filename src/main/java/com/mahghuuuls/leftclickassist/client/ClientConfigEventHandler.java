package com.mahghuuuls.leftclickassist.client;

import com.mahghuuuls.leftclickassist.Tags;
import com.mahghuuuls.leftclickassist.common.config.ModConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientConfigEventHandler {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Tags.MOD_ID.equals(event.getModID())) {
            ModConfig.load();
        }
    }
}
