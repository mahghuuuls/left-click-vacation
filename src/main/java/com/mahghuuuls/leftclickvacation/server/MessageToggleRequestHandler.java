package com.mahghuuuls.leftclickvacation.server;

import com.mahghuuuls.leftclickvacation.common.network.MessageToggleRequest;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageToggleRequestHandler implements IMessageHandler<MessageToggleRequest, IMessage> {

    @Override
    public IMessage onMessage(final MessageToggleRequest message, MessageContext ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        IThreadListener mainThread = player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ServerAutomationAuthority.handleToggleRequest(player, message);
            }
        });
        return null;
    }
}
