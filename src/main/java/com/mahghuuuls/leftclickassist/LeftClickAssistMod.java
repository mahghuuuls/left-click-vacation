package com.mahghuuuls.leftclickassist;

import com.mahghuuuls.leftclickassist.common.config.ModConfig;
import com.mahghuuuls.leftclickassist.serveronly.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, acceptableRemoteVersions = "*")
public class LeftClickAssistMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(
            clientSide = "com.mahghuuuls.leftclickassist.client.ClientProxy",
            serverSide = "com.mahghuuuls.leftclickassist.serveronly.ServerProxy")
    public static ServerProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.load();
        proxy.preInit(event);
        LOGGER.info("{} initialized.", Tags.MOD_NAME);
    }
}
