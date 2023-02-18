package fr.bramsou.netty.messaging;

import fr.bramsou.netty.messaging.pipeline.PipelineCodec;
import fr.bramsou.netty.messaging.pipeline.PipelineSizer;
import fr.bramsou.netty.messaging.session.MessagingSession;
import io.netty.channel.*;

public class MessagingInitializer extends ChannelInitializer<Channel> {

    private final MessagingSession session;

    public MessagingInitializer(MessagingSession session) {
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
        final MessagingNetwork network = new MessagingNetwork(this.session);

        pipeline.addLast("sizer", new PipelineSizer());
        pipeline.addLast("codec", new PipelineCodec(network));
        pipeline.addLast("manager", network);
    }
}
