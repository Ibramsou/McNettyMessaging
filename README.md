# McNettyMessaging
Messaging system using netty. Serviceable on minecraft between Spigot &lt;-> Proxy and any other usages
# Implementation
### Gradle
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.netty:netty-all:4.1.86.Final'
    implementation 'com.google.code.gson:gson:2.8.9'
    implementation 'io.github.ibramsou:mc-netty-messaging-api:2.0.0'
    implementation 'io.github.ibramsou:mc-netty-messaging-core:2.0.0'
}
```
### Maven
```xml
<dependencies>
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.86.Final</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.9</version>
    </dependency>
    <dependency>
        <groupId>io.github.ibramsou</groupId>
        <artifactId>mc-netty-messaging-api</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.ibramsou</groupId>
        <artifactId>mc-netty-messaging-core</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```
# How to use
### Step 1: Create server session
Create a session
```java
Session session = Messaging.getInstance().createSession(SessionType.SERVER);
```
### Step 2: Configure session
```java
SessionConfig config = session.config();
config.set(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS, true);
config.set(MessagingOptions.HOST, "localhost");
config.set(MessagingOptions.PORT, 4448);
config.set(MessagingOptions.CHANNEL, ChannelOption.TCP_NODELAY, true);
```
### Step 3: Open server connection
First, create the session with the listener we created below, then handle connection with the choosen port
```java
session.connect();
```
### Step 4: Connect client session with a similar configuration
```java
Session session = Messaging.getInstance().createSession(SessionType.CLIENT);
session.config().set(MessagingOptions.HOST, "localhost").set(MessagingOptions.PORT, 4448);
session.connect();
```
### Step 5: Add session and packet event listeners
```java
session.messaging().subscribe(SessionConnectEvent.class, event -> System.out.println("Session connected !"));
session.messaging().subscribe(MessagePacket.class, event -> event.getNetwork().sendPacket(new MessagePacket("Hello !")));
```
### Step 6: Create your own custom packet
Add a new implementation
```java
public class TestPacket extends MessagingPacket {

    private final int randomInteger;

    public TestPacket(int randomInteger) {
        this.randomInteger = randomInteger;
    }

    public TestPacket(@Nonnull PacketBuffer buffer) {
        this.randomInteger = buffer.readInt();
    }

    @Override
    public void serialize(@Nonnull PacketBuffer buffer) {
        buffer.writeInt(this.randomInteger);
    }

    public int getRandomInteger() {
        return randomInteger;
    }
}
```
Register the packet in a registered network state
```java
NetworkState.DEFAULT_STATE.register(0x05, TestPacket.class, TestPacket::new);
```
### Step 7: Create and register your own network state
Register the new state
```java
Messaging messaging = Messaging.getInstance();
NetworkState state = messaging.getRegistry().register("Example State");
state.register(0x01, MessagePacket.class, MessagePacket::new);
state.register(0x02, TestPacket.class, TestPacket::new);
```
Set as default
```java
session.config().set(MessagingOptions.DEFAULT_NETWORK_STATE, state);
```
Or change the current network state as you want
```java
messaging.subscribe(TestPacket.class, event -> event.getNetwork().setState(state));
```

# Here is a full example code
```java
package packet;

import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;

import javax.annotation.Nonnull;

public class TestPacket extends MessagingPacket {

    private final int randomInteger;

    public TestPacket(int randomInteger) {
        this.randomInteger = randomInteger;
    }

    public TestPacket(@Nonnull PacketBuffer buffer) {
        this.randomInteger = buffer.readInt();
    }

    @Override
    public void serialize(@Nonnull PacketBuffer buffer) {
        buffer.writeInt(this.randomInteger);
    }

    public int getRandomInteger() {
        return randomInteger;
    }
}
````

```java
package server;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.event.session.SessionConnectEvent;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.packet.impl.MessagePacket;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionConfig;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.netty.channel.ChannelOption;
import packet.TestPacket;

import java.util.concurrent.ThreadLocalRandom;

public class Server {

    public static void main(String[] args) {
        // Register custom packet to default network state
        NetworkState.DEFAULT_STATE.register(0x05, TestPacket.class, TestPacket::new);
        // Create a session server
        Session session = Messaging.getInstance().createSession(SessionType.SERVER);
        // Configure the session
        SessionConfig config = session.config();
        config.set(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS, true);
        config.set(MessagingOptions.HOST, "localhost");
        config.set(MessagingOptions.PORT, 4448);
        config.set(MessagingOptions.CHANNEL, ChannelOption.TCP_NODELAY, true);
        // Register listeners
        session.messaging().subscribe(SessionConnectEvent.class, event -> System.out.println("A client joined the server !"));
        session.messaging().subscribe(MessagePacket.class, event -> {
            if (event.getMessage().startsWith("Hi")) {
                event.getNetwork().sendPacket(new TestPacket(ThreadLocalRandom.current().nextInt(400)));
            }
        });
        // Open server connection
        session.connect();
    }
}
```

```java
package client;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.event.session.SessionConnectEvent;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.packet.impl.MessagePacket;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.netty.channel.ChannelOption;
import packet.TestPacket;

public class Client {

    public static void main(String[] args) {
        Messaging.getInstance().getRegistry().register("Example State");
        // Register custom packet to default network state
        NetworkState.DEFAULT_STATE.register(0x05, TestPacket.class, TestPacket::new);
        // Create a session server
        Session session = Messaging.getInstance().createSession(SessionType.CLIENT);
        // Configure the session
        session.config()
                .set(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS, true)
                .set(MessagingOptions.HOST, "localhost")
                .set(MessagingOptions.PORT, 4448)
                .set(MessagingOptions.CHANNEL, ChannelOption.TCP_NODELAY, true);
        // Register listeners
        session.messaging().subscribe(SessionConnectEvent.class, event -> event.getNetwork().sendPacket(new MessagePacket("Hi !")));
        session.messaging().subscribe(TestPacket.class, event -> System.out.println("Result: " + event.getRandomInteger()));
        // Open server connection
        session.connect();
    }
}
```

### Here is a plugin with an exemple of custom packet state creation: https://github.com/Ibramsou/ProxyMessaging
