package com.mahghuuuls.leftclickvacation.common.network;

import com.mahghuuuls.leftclickvacation.LeftClickVacationMod;
import com.mahghuuuls.leftclickvacation.common.AutomationState;
import com.mahghuuuls.leftclickvacation.common.DisableReason;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageAutomationState implements IMessage {

    private AutomationState state;
    private DisableReason reason;

    public MessageAutomationState() {
        this(AutomationState.DISABLED, DisableReason.NONE);
    }

    public MessageAutomationState(AutomationState state, DisableReason reason) {
        this.state = state;
        this.reason = reason;
    }

    public AutomationState getState() {
        return state;
    }

    public DisableReason getReason() {
        return reason;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        state = readEnum(buf, AutomationState.values(), AutomationState.DISABLED);
        reason = readEnum(buf, DisableReason.values(), DisableReason.NONE);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(state.ordinal());
        buf.writeByte(reason.ordinal());
    }

    private static <T> T readEnum(ByteBuf buf, T[] values, T fallback) {
        int ordinal = buf.readUnsignedByte();
        if (ordinal < 0 || ordinal >= values.length) {
            return fallback;
        }
        return values[ordinal];
    }

    public static class Handler implements IMessageHandler<MessageAutomationState, IMessage> {

        @Override
        public IMessage onMessage(final MessageAutomationState message, MessageContext ctx) {
            LeftClickVacationMod.proxy.handleAutomationState(message);
            return null;
        }
    }
}
