package fr.bramsou.netty.messaging.packet.impl;

import fr.bramsou.netty.messaging.handler.MessagingPacketListenerHandler;
import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;

import java.util.Objects;

public class MessagePacket implements MessagingPacket<MessagingPacketListenerHandler> {

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

    @Override
    public void read(MessagingPacketListenerHandler handler) {
        handler.handle(this);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagePacket that = (MessagePacket) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "MessagePacket{" +
                "message='" + message + '\'' +
                '}';
    }
}
