package fr.bramsou.messaging.netty.util;

public enum DisconnectReason {
    HOST_DISCONNECTED("Host has disconnected"),
    CONNECTION_TIMED_OUT("Connection timed out"),
    READ_TIMED_OUT("Read timed out"),
    WRITE_TIMED_OUT("Write timed out"),
    EXCEPTION_CAUGHT("An error occurred while disconnecting");

    private final String message;

    DisconnectReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
