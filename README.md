# McNettyMessaging
Messaging system using netty. Serviceable on minecraft between Spigot &lt;-> Proxy or any other usages
# Implementation
### Gradle
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.ibramsou:mc-netty-messaging-all:1.0.0'
}
```
### Maven
```xml
<dependencies>
    <dependency>
        <groupId>io.github.ibramsou</groupId>
        <artifactId>mc-netty-messaging-all</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
# How to use
### Step 1: Create packet listener handler
The packet listener handler is needed when incoming packets are received.
```java
public class PacketListener implements MessagingPacketListenerHandler {

    private final MessagingNetwork network;

    public PacketListener(MessagingNetwork network) {
        this.network = network;
    }

    @Override
    public void handle(TokenPacket packet) {
        if (packet.getToken().equals("Password123")) {
            System.out.println(packet.getPort() + " port has connected !");
        } else {
            this.network.disconnect(DisconnectReason.INCORRECT_TOKEN);
        }
    }

    @Override
    public MessagingNetwork getNetwork() {
        return this.network;
    }
}
```
*Note: You can create your packet handler interface in relation with your own custom packets*
### Step 2: Create session listener
A session listener is required to store some important information which is used for the channel initialization
```java

final MessagingSessionListener listener = new MessagingSessionListener() {
    @Override
    public PacketRegistryState getDefaultPacketState(MessagingNetwork network) {
        return MESSAGING_STATE;
    }

    @Override
    public PacketListenerHandler getDefaultPacketListener(MessagingNetwork network) {
        return new PacketListener(network);
    }

    @Override
    public void connected(MessagingNetwork network) {
        System.out.println("Connected :)");
    }

    @Override
    public void disconnected(MessagingNetwork network, DisconnectReason reason, Throwable cause) {
        System.out.println("Disconnected :(");
    }
};
```
*Note: "MESSAGING_STATE" is a default packet state, but you can create your own, or register new packets to the default packet state*
### Step 3: Creating server connection
First, create the session with the listener we created below, then handle connection with the choosen port
```java
new MessagingServerSession(this.listener).bindConnection("localhost", 27777);
```
### Step 4: Connect client
First create the session
```java
final MessagingClientSession session = new MessagingClientSession(this.listener);
```
Configure it as you want
```java
session.setAutoReconnect(true);
session.setReconnectTime(5000);
session.setMaxConnectAttempts(10); // -1 = infinite
```
And then connect to server
```java
session.createConnection("localhost", 27777);
```

### Here is a plugin with an exemple of custom packet state creation: https://github.com/Ibramsou/ProxyMessaging
