package fr.bramsou.messaging.netty.packet.impl;

import fr.bramsou.messaging.netty.NettyEncryption;
import fr.bramsou.messaging.netty.handler.MessagingPacketListenerHandler;
import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

import java.util.Objects;

public class TokenPacket implements NettyPacket<MessagingPacketListenerHandler> {

    private final String token;
    private final int port;

    public TokenPacket(String token, int port) {
        this.token = token;
        this.port = port;
    }

    public TokenPacket(PacketBuffer buffer) {
        this.token = NettyEncryption.decrypt(buffer.readString(Short.MAX_VALUE));
        this.port = buffer.readVarInt();
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeString(NettyEncryption.encrypt(this.token));
        buffer.writeVarInt(this.port);
    }

    @Override
    public void read(MessagingPacketListenerHandler handler) {
        handler.handle(this);
    }

    public String getToken() {
        return token;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPacket that = (TokenPacket) o;
        return port == that.port && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, port);
    }

    @Override
    public String toString() {
        return "TokenPacket{" +
                "token='" + token + '\'' +
                ", port=" + port +
                '}';
    }
}
