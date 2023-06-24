package packet;

import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;

import javax.annotation.Nonnull;

public class TestPacket extends MessagingPacket {

    private final int randomInteger;

    public TestPacket(int randomInteger) {
        this.randomInteger = randomInteger;
    }

    public TestPacket(@Nonnull PacketBuffer buffer) {
        this.randomInteger = buffer.readInt();
    }

    @Override
    public void serialize(@Nonnull PacketBuffer buffer) {
        buffer.writeInt(this.randomInteger);
    }

    public int getRandomInteger() {
        return randomInteger;
    }
}
