package packet;

import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.github.ibramsou.netty.messaging.api.packet.impl.SkippingPacket;

import javax.annotation.Nonnull;

public class TestPacket extends SkippingPacket {

    private final int randomInteger;

    public TestPacket(int randomInteger) {
        this.randomInteger = randomInteger;
    }

    public TestPacket(@Nonnull PacketBuffer buffer) {
        this.randomInteger = 0;
    }

    @Override
    public void serialize(@Nonnull PacketBuffer buffer) {
        buffer.writeInt(this.randomInteger);
    }

    public int getRandomInteger() {
        return randomInteger;
    }
}
