package fr.bramsou.messaging.netty.util;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class AddressResolver {

    public static SocketAddress resolveAddress(int port) {
        InetSocketAddress address;
        try {
            final InetAddress resolved = InetAddress.getByName("localhost");
            address = new InetSocketAddress(resolved, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            address = InetSocketAddress.createUnresolved("localhost", port);
        }

        return address;
    }
}
