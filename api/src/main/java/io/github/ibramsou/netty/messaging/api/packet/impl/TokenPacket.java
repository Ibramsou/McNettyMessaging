package io.github.ibramsou.netty.messaging.api.packet.impl;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TokenPacket extends MessagingPacket {

    private final String token;
    private final int port;

    public TokenPacket(String token, int port) {
        this.token = token;
        this.port = port;
    }

    public TokenPacket(PacketBuffer buffer) {
        this.token = Messaging.getInstance().getEncryption().decrypt(buffer.network(), buffer.readString(Short.MAX_VALUE));
        this.port = buffer.readVarInt();
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeString(Messaging.getInstance().getEncryption().encrypt(buffer.network(), this.token));
        buffer.writeVarInt(this.port);
    }
}
