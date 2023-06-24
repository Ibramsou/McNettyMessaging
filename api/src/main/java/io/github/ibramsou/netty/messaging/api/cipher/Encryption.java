package io.github.ibramsou.netty.messaging.api.cipher;

import io.github.ibramsou.netty.messaging.api.network.Network;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Encryption {

    /**
     * Encrypt a message with AES
     * @param network the connected network
     * @param message the message to encrypt
     * @return the encrypted message
     */
    String encrypt(Network network, String message);

    /**
     * Decrypt a message with AES
     * @param network the connected network
     * @param message the message to decrypt
     * @return the decrypted message
     */
    String decrypt(Network network, String message);
}
