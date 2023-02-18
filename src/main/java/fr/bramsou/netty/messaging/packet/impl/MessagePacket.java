package fr.bramsou.netty.messaging.packet.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.bramsou.netty.messaging.handler.MessagingPacketListenerHandler;
import fr.bramsou.netty.messaging.util.MessageType;
import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;

public class MessagePacket implements MessagingPacket<MessagingPacketListenerHandler> {

    private final MessageType type;
    private final Object message;

    public MessagePacket(String message) {
        this.type = MessageType.STRING;
        this.message = message;
    }

    public MessagePacket(JsonObject object) {
        this.type = MessageType.JSON;
        this.message = object;
    }

    public MessagePacket(PacketBuffer buffer) {
        this.type = buffer.readVarInt() == 0 ? MessageType.STRING : MessageType.JSON;
        String message = buffer.readString(Short.MAX_VALUE);
        if (this.type == MessageType.STRING) {
            this.message = message;
        } else {
            this.message = new JsonParser().parse(message).getAsJsonObject();
        }
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeVarInt(this.type.ordinal());
        buffer.writeString(this.message.toString());
    }

    @Override
    public void read(MessagingPacketListenerHandler handler) {
        if (this.message instanceof JsonObject) {
            handler.handleJson((JsonObject) this.message);
        } else {
            handler.handleMessage((String) this.message);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getMessage() {
        return (T) message;
    }
}
