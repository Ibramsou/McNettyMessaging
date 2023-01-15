package fr.bramsou.messaging.netty.packet.impl;

import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

import java.util.Objects;

public class TokenPacket implements NettyPacket {

    private final String token;

    public TokenPacket(String token) {
        this.token = token;
    }

    public TokenPacket(PacketBuffer buffer) {
        this.token = buffer.readString(16);
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeString(this.token);
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPacket that = (TokenPacket) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "TokenPacket{" +
                "token='" + token + '\'' +
                '}';
    }
}
