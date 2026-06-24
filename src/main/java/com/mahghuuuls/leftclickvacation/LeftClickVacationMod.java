package com.mahghuuuls.leftclickvacation;

import com.mahghuuuls.leftclickvacation.common.config.ModConfig;
import com.mahghuuuls.leftclickvacation.common.network.NetworkHandler;
import com.mahghuuuls.leftclickvacation.serveronly.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LeftClickVacationMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(
            clientSide = "com.mahghuuuls.leftclickvacation.client.ClientProxy",
            serverSide = "com.mahghuuuls.leftclickvacation.serveronly.ServerProxy")
    public static ServerProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.load();
        NetworkHandler.init();
        proxy.preInit(event);
        LOGGER.info("{} initialized.", Tags.MOD_NAME);
    }
}
