package io.github.ibramsou.netty.messaging.api;

import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.option.OptionList;
import io.github.ibramsou.netty.messaging.api.option.OptionMap;
import io.github.ibramsou.netty.messaging.api.option.OptionValue;
import io.github.ibramsou.netty.messaging.api.pipeline.PipelineFactory;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface MessagingOptions {

     // Global messaging settings

    /**
     * Optimize write var int methods
     */
    OptionValue<Boolean> OPTIMIZE_WRITE_VAR_INTS = new OptionValue<>(true);
    /**
     * Optimize get var ints size method
     */
    OptionValue<Boolean> OPTIMIZE_VAR_INTS_SIZER = new OptionValue<>(true);
    /**
     * Enable error throws when an unregistered incoming packet is received
     */
    OptionValue<Boolean> THROW_UNKNOWN_PACKET_ERRORS = new OptionValue<>(false);
    /**
     * Define an encryption password for token packets
     */
    OptionValue<String> ENCRYPTION_PASSWORD = new OptionValue<>("i1lxhOcjUXzO");
    /**
     * Define an encryption salt key for token packets
     */
    OptionValue<String> ENCRYPTION_SALT_KEY = new OptionValue<>("QCLjfUcuejiux97X3UzBKoFBSwuvqPheeFyFJINs9bdbfEi7SE");
    /**
     * Define compression threshold size for compressed packets
     * Compression can be enabled with CompressionPacket
     */
    OptionValue<Integer> COMPRESSION_THRESHOLD = new OptionValue<>(256);

    // Global network settings
    /**
     * Custom loop group configuration
     */
    OptionValue<MessagingLoopGroup> LOOP_GROUP = new OptionValue<>(MessagingLoopGroup.DEFAULT_LOOP_GROUP);
    /**
     * Default network state registry used when a new client connects
     */
    OptionValue<NetworkState> DEFAULT_NETWORK_STATE = new OptionValue<>(NetworkState.DEFAULT_STATE);

    /**
     * Add custom pipeline handlers for encoding and decoding
     */
    OptionMap<String, PipelineFactory> BEFORE_PIPELINE_HANDLERS = new OptionMap<String, PipelineFactory>()
            .set("timeout", network -> new ReadTimeoutHandler(30))
            .set("sizer", Messaging.getInstance().getPipelineFactories().getSizerFactory())
            .set("codec", Messaging.getInstance().getPipelineFactories().getCodecFactory());
    OptionMap<String, PipelineFactory> AFTER_PIPELINE_HANDLERS = new OptionMap<>();
    /**
     * Configure default netty bootstrap options
     */
    OptionMap<ChannelOption<?>, Object> BOOTSTRAP = new OptionMap<ChannelOption<?>, Object>()
            .set(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);
    /**
     * Configure default netty channel options
     */
    OptionMap<ChannelOption<?>, Object> CHANNEL = new OptionMap<ChannelOption<?>, Object>()
            .set(ChannelOption.TCP_NODELAY, true)
            .set(ChannelOption.IP_TOS, 0x18);
    /**
     * Configure future listeners after the bootstrap initialisation
     */
    OptionList<GenericFutureListener<? extends Future<? super Void>>> LISTENERS = new OptionList<>();
    /**
     * Use a blocking method waiting for the end of the connection after the bootstrap creation
     */
    OptionValue<Boolean> SYNCHRONIZE = new OptionValue<>(false);

    // Global sessions settings

    /**
     * Address host
     */
    OptionValue<String> HOST = new OptionValue<>("localhost");
    /**
     * Address port
     */
    OptionValue<Integer> PORT = new OptionValue<>(54321);

    // Client settings

    /**
     * Add a re-connect try when connection failed
     */
    OptionValue<Boolean> AUTO_RECONNECT = new OptionValue<>(true);
    /**
     * Max connections attempts before giving auto reconnects (-1 = infinite)
     */
    OptionValue<Integer> CONNECTION_MAX_ATTEMPTS = new OptionValue<>(-1);
    /**
     * Time before another reconnect try
     */
    OptionValue<Integer> AUTO_RECONNECT_TIME = new OptionValue<>(5_000);

    // Server settings

    /**
     * Authorize address that is not localhost to connect the server
     */
    OptionValue<Boolean> AUTHORIZE_INCOMING_ADDRESSES = new OptionValue<>(true);
}
