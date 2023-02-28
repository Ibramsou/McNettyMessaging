package fr.bramsou.netty.messaging;

import fr.bramsou.netty.messaging.pipeline.PipelineFactory;
import fr.bramsou.netty.messaging.pipeline.PipelineCodec;
import fr.bramsou.netty.messaging.pipeline.PipelineSizer;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.*;

public class MessagingBuilder {

    public static final MessagingBuilder DEFAULT_BUILDER = new MessagingBuilder()
            .bootstrapOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)
            .channelOption(ChannelOption.IP_TOS, 0x18)
            .channelOption(ChannelOption.TCP_NODELAY, true)
            .pipelineHandler("size", PipelineSizer::new)
            .pipelineHandler("codec", PipelineCodec::new)
            .synchronize(false);

    private MessagingLoopGroup loopGroup;
    private final LinkedList<PipelineEntry> handlers = new LinkedList<>();
    private final Map<ChannelOption<Object>, Object> bootstrapOptions = new LinkedHashMap<>();
    private final Map<ChannelOption<Object>, Object> channelOptions = new LinkedHashMap<>();
    private final Collection<GenericFutureListener<? extends Future<? super Void>>> listeners = new LinkedHashSet<>();

    private boolean synchronizeWait;
    private boolean authorizeIncomingAddress = true;

    public MessagingBuilder loopGroup(MessagingLoopGroup group) {
        this.loopGroup = group;
        return this;
    }

    public MessagingBuilder pipelineHandler(String id, PipelineFactory<?> handler) {
        return this.pipelineHandler(id, handler, false);
    }

    public MessagingBuilder pipelineHandler(String id, PipelineFactory<?> handler, boolean afterNetwork) {
        final PipelineEntry entry = new PipelineEntry(id, handler, afterNetwork);
        this.handlers.add(entry);
        return this;
    }

    public MessagingBuilder firstPipelineHandler(String id, PipelineFactory<?> handler) {
        return this.firstPipelineHandler(id, handler, false);
    }

    public MessagingBuilder firstPipelineHandler(String id, PipelineFactory<?> handler, boolean after) {
        final PipelineEntry entry = new PipelineEntry(id, handler, after);
        this.handlers.addFirst(entry);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> MessagingBuilder bootstrapOption(ChannelOption<T> option, T value) {
        this.bootstrapOptions.put((ChannelOption<Object>) option, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> MessagingBuilder channelOption(ChannelOption<T> option, T value) {
        this.channelOptions.put((ChannelOption<Object>) option, value);
        return this;
    }

    public MessagingBuilder synchronize(boolean synchronize) {
        this.synchronizeWait = synchronize;
        return this;
    }

    public MessagingBuilder listener(GenericFutureListener<? extends Future<? super Void>> listener) {
        this.listeners.add(listener);
        return this;
    }

    public MessagingBuilder authorizeIncomingAddress(boolean authorizeIncomingAddress) {
        this.authorizeIncomingAddress = authorizeIncomingAddress;
        return this;
    }

    public boolean isSynchronizeWait() {
        return synchronizeWait;
    }

    public Collection<PipelineEntry> getPipelineHandlers() {
        return this.handlers;
    }

    public Map<ChannelOption<Object>, Object> getBootstrapOptions() {
        return bootstrapOptions;
    }

    public Map<ChannelOption<Object>, Object> getChannelOptions() {
        return channelOptions;
    }

    public Collection<GenericFutureListener<? extends Future<? super Void>>> getListeners() {
        return listeners;
    }

    public boolean isAuthorizeIncomingAddress() {
        return authorizeIncomingAddress;
    }

    public MessagingLoopGroup getLoopGroup() {
        if (loopGroup == null) return MessagingLoopGroup.DEFAULT_LOOP_GROUP;
        return loopGroup;
    }

    protected static final class PipelineEntry {
        final String id;
        final PipelineFactory<?> constructor;
        final boolean afterNetwork;

        public PipelineEntry(String id, PipelineFactory<?> constructor, boolean afterNetwork) {
            this.id = id;
            this.constructor = constructor;
            this.afterNetwork = afterNetwork;
        }

    }
}
