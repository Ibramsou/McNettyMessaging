package packet;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.packet.impl.MessagePacket;

public class TestCustomState {

    public static NetworkState getCustomState() {
        NetworkState state = Messaging.getInstance().getRegistry().register("EXAMPLE_STATE");
        state.register(0x01, MessagePacket.class, MessagePacket::new);
        state.register(0x02, TestPacket.class, TestPacket::new);
        return state;
    }
}
