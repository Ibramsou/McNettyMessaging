package fr.bramsou.netty.messaging.packet.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.bramsou.netty.messaging.handler.MessagingPacketListenerHandler;
import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;

import java.util.Objects;

public class JsonMessagePacket implements MessagingPacket<MessagingPacketListenerHandler> {

    private final JsonObject object;

    public JsonMessagePacket(PacketBuffer buffer) {
        this.object = new JsonParser().parse(buffer.readString(Short.MAX_VALUE)).getAsJsonObject();
    }

    public JsonMessagePacket(JsonObject object) {
        this.object = object;
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeString(this.object.toString());
    }

    @Override
    public void read(MessagingPacketListenerHandler handler) {
        handler.handle(this);
    }

    public JsonObject getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonMessagePacket that = (JsonMessagePacket) o;
        return Objects.equals(object, that.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }

    @Override
    public String toString() {
        return "JsonMessagePacket{" +
                "object=" + object +
                '}';
    }
}
