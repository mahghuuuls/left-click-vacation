package com.mahghuuuls.leftclickvacation.common.network;

import com.mahghuuuls.leftclickvacation.LeftClickVacationMod;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSupport implements IMessage {

    private boolean supported;

    public MessageServerSupport() {
        this(false);
    }

    public MessageServerSupport(boolean supported) {
        this.supported = supported;
    }

    public boolean isSupported() {
        return supported;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        supported = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(supported);
    }

    public static class Handler implements IMessageHandler<MessageServerSupport, IMessage> {

        @Override
        public IMessage onMessage(final MessageServerSupport message, MessageContext ctx) {
            LeftClickVacationMod.proxy.handleServerSupport(message);
            return null;
        }
    }
}
