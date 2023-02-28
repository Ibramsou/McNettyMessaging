package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingBuilder;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelFuture;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public abstract class MessagingSession {

    private final MessagingSessionListener listener;
    private final MessagingBuilder builder;
    private String host;
    private int port;

    public MessagingSession(MessagingSessionListener listener, MessagingBuilder builder) {
        this.listener = listener;
        this.builder = builder;
    }

    public abstract void disconnect();

    protected final void configureBootstrap(AbstractBootstrap<?, ?> bootstrap) {
        this.builder.getBootstrapOptions().forEach(bootstrap::option);
    }

    protected final void configureConnection(ChannelFuture future) {
        this.builder.getListeners().forEach(future::addListener);
        if (this.builder.isSynchronizeWait()) {
            try {
                future.sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected SocketAddress resolveAddress(String host, int port) {
        InetSocketAddress address;
        try {
            final InetAddress resolved = InetAddress.getByName(host);
            address = new InetSocketAddress(resolved, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            address = InetSocketAddress.createUnresolved(host, port);
        }

        return address;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public final MessagingSessionListener getListener() {
        return listener;
    }

    public MessagingBuilder getBuilder() {
        return builder;
    }
}
