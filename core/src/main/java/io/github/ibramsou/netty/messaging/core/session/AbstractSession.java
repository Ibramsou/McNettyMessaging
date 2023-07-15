package io.github.ibramsou.netty.messaging.core.session;

import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.option.Option;
import io.github.ibramsou.netty.messaging.api.option.OptionList;
import io.github.ibramsou.netty.messaging.api.option.OptionMap;
import io.github.ibramsou.netty.messaging.api.option.OptionValue;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionConfig;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractSession implements Session {

    protected final SessionConfig config = new MessagingSessionConfig();

    @Override
    public final SessionConfig config() {
        return this.config;
    }

    @Override
    public void connect() {
        String host = this.config.get(MessagingOptions.HOST);
        int port = this.config.get(MessagingOptions.PORT);
        System.out.println("Host: " + host);
        System.out.println("PORT: " + port);
        InetSocketAddress address;
        try {
            final InetAddress resolved = InetAddress.getByName(host);
            address = new InetSocketAddress(resolved, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            address = InetSocketAddress.createUnresolved(host, port);
        }

        this.connect(address);
    }

    protected void synchronize(ChannelFuture future) {
        if (this.config.get(MessagingOptions.SYNCHRONIZE)) {
            try {
                future.sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("all")
    protected final ChannelFuture openConnection(AbstractBootstrap<?, ?> bootstrap, Supplier<ChannelFuture> futureConsumer) {
        this.config.get(MessagingOptions.BOOTSTRAP).forEach((channelOption, value) -> bootstrap.option((ChannelOption) channelOption, value));
        final ChannelFuture channelFuture = futureConsumer.get();
        this.config.get(MessagingOptions.LISTENERS).forEach(genericFutureListener -> channelFuture.addListener(genericFutureListener));
        return channelFuture;
    }

    @Override
    public String getHost() {
        return this.config.get(MessagingOptions.HOST);
    }

    @Override
    public int getPort() {
        return this.config.get(MessagingOptions.PORT);
    }

    public abstract void connect(InetSocketAddress address);

    @ParametersAreNonnullByDefault
    static class MessagingSessionConfig implements SessionConfig {
        private final Map<Option<?>, Option<?>> optionMap = new HashMap<>();

        @SuppressWarnings("unchecked")
        private <T extends Option<?>> T getAsType(Option<?> option) {
            Option<?> result = this.optionMap.get(option);
            if (result == null) return null;
            return (T) result;
        }

        @Override
        public <T> MessagingSessionConfig set(OptionValue<T> option, T value) {
            OptionValue<T> copy = this.getAsType(option);
            if (copy == null) {
                copy = option.copy();
                this.optionMap.put(option, copy);
            }
            copy.value(value);
            return this;
        }

        @Override
        public <T> T get(OptionValue<T> option) {
            OptionValue<T> result = this.getAsType(option);
            if (result == null) return option.getValue();
            return result.getValue();
        }

        @Override
        public <T> MessagingSessionConfig add(OptionList<T> option, T value) {
            OptionList<T> copy = this.getAsType(option);
            if (copy == null) {
                copy = option.copy();
                this.optionMap.put(option, copy);
            }

            copy.add(value);
            return this;
        }

        @Override
        public <T> List<T> get(OptionList<T> option) {
            OptionList<T> result = this.getAsType(option);
            if (result == null) return option.getValue();
            return result.getValue();
        }

        @Override
        public <K, V> MessagingSessionConfig set(OptionMap<K, V> option, K key, V value) {
            OptionMap<K, V> copy = this.getAsType(option);
            if (copy == null) {
                copy = (OptionMap<K, V>) option.copy();
                this.optionMap.put(option, copy);
            }

            copy.set(key, value);
            return this;
        }

        @Override
        public <K, V> V remove(OptionMap<K, V> option, K key) {
            OptionMap<K, V> copy = this.getAsType(option);
            if (copy == null) {
                copy = (OptionMap<K, V>) option.copy();
                this.optionMap.put(option, copy);
            }

            return copy.remove(key);
        }

        @Override
        public <K, V> Map<K, V> get(OptionMap<K, V> option) {
            OptionMap<K, V> result = this.getAsType(option);
            if (result == null) return option.getValue();
            return result.getValue();
        }

        @Override
        public MessagingSessionConfig clear(OptionList<?> option) {
            OptionList<?> copy = this.getAsType(option);
            if (copy == null) {
                copy = option.copy();
                this.optionMap.put(option, copy);
            }
            copy.clear();
            return this;
        }

        @Override
        public <K, V> MessagingSessionConfig clear(OptionMap<K, V> option) {
            OptionMap<K, V> copy = this.getAsType(option);
            if (copy == null) {
                copy = (OptionMap<K, V>) option.copy();
                this.optionMap.put(option, copy);
            }
            copy.clear();
            return this;
        }

        @Override
        public MessagingSessionConfig toDefault(Option<?> option) {
            this.optionMap.remove(option);
            return this;
        }
    }
}
