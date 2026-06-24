package com.mahghuuuls.leftclickvacation.common.network;

import com.mahghuuuls.leftclickvacation.Tags;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkHandler {

    private static SimpleNetworkWrapper channel;

    private NetworkHandler() {
    }

    public static void init() {
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);
        channel.registerMessage(MessageServerSupport.Handler.class, MessageServerSupport.class, 0, Side.CLIENT);
        channel.registerMessage(MessageAutomationState.Handler.class, MessageAutomationState.class, 2, Side.CLIENT);
    }

    public static void registerServerMessages(
            Class<? extends IMessageHandler<MessageToggleRequest, IMessage>> toggleRequestHandlerClass) {
        channel.registerMessage(toggleRequestHandlerClass, MessageToggleRequest.class, 1, Side.SERVER);
    }

    public static void sendToServer(IMessage message) {
        channel.sendToServer(message);
    }

    public static void sendTo(MessageServerSupport message, EntityPlayerMP player) {
        channel.sendTo(message, player);
    }

    public static void sendTo(MessageAutomationState message, EntityPlayerMP player) {
        channel.sendTo(message, player);
    }
}
