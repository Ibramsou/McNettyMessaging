package fr.bramsou.messaging.netty;

import fr.bramsou.messaging.netty.pipeline.PipelineMessaging;
import fr.bramsou.messaging.netty.pipeline.PipelineSizer;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyInitializer extends ChannelInitializer<Channel> {

    private final NettyNetwork network;

    public NettyInitializer(NettyNetwork network) {
        this.network = network;
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.config().setOption(ChannelOption.IP_TOS, 0x18);

        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        } catch (ChannelException e) {
            throw new IllegalArgumentException("Exception while trying to set TCP_NODELAY", e);
        }

        final ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("timeout", new ReadTimeoutHandler(30));
        pipeline.addLast("sizer", new PipelineSizer());
        pipeline.addLast("codec", new PipelineMessaging());
        pipeline.addLast("manager", this.network);
    }
}
