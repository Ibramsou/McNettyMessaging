package io.github.ibramsou.netty.messaging.api.packet.impl;

import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MessagePacket extends MessagingPacket {

    private final String message;

    public MessagePacket(String message) {
        this.message = message;
    }

    public MessagePacket(PacketBuffer buffer) {
        this.message = buffer.readString(Short.MAX_VALUE);
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeString(this.message);
    }

    public String getMessage() {
        return message;
    }
}
