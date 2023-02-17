package fr.bramsou.messaging.netty;

import fr.bramsou.messaging.netty.pipeline.PipelineCodec;
import fr.bramsou.messaging.netty.pipeline.PipelineSizer;
import fr.bramsou.messaging.netty.session.NettySession;
import io.netty.channel.*;

public class NettyInitializer extends ChannelInitializer<Channel> {

    private final NettySession session;

    public NettyInitializer(NettySession session) {
        this.session = session;
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
        final NettyNetwork network = new NettyNetwork(this.session);

        pipeline.addLast("sizer", new PipelineSizer());
        pipeline.addLast("codec", new PipelineCodec(network));
        pipeline.addLast("manager", network);
    }
}
