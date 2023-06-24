package io.github.ibramsou.netty.messaging.api.packet.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JsonMessagePacket extends MessagingPacket {

    private final JsonObject message;

    @SuppressWarnings("deprecation")
    public JsonMessagePacket(PacketBuffer buffer) {
        this.message = new JsonParser().parse(buffer.readString(Short.MAX_VALUE)).getAsJsonObject();
    }

    public JsonMessagePacket(JsonObject message) {
        this.message = message;
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeString(this.message.toString());
    }

    public JsonObject getMessage() {
        return message;
    }
}
