package fr.bramsou.messaging.netty.packet.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.bramsou.messaging.netty.handler.MessagingPacketListenerHandler;
import fr.bramsou.messaging.netty.util.MessageType;
import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

public class MessagePacket implements NettyPacket<MessagingPacketListenerHandler> {

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

    @SuppressWarnings("deprecation")
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
