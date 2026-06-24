package com.mahghuuuls.leftclickvacation.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageToggleRequest implements IMessage {

    private boolean enableRequested;
    private int selectedHotbarSlot;

    public MessageToggleRequest() {
        this(false, -1);
    }

    public MessageToggleRequest(boolean enableRequested, int selectedHotbarSlot) {
        this.enableRequested = enableRequested;
        this.selectedHotbarSlot = selectedHotbarSlot;
    }

    public boolean isEnableRequested() {
        return enableRequested;
    }

    public int getSelectedHotbarSlot() {
        return selectedHotbarSlot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        enableRequested = buf.readBoolean();
        selectedHotbarSlot = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(enableRequested);
        buf.writeByte(selectedHotbarSlot);
    }
}
