package io.github.ibramsou.netty.messaging.core.cipher;

import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.cipher.Encryption;
import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.session.Session;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MessagingEncryption implements Encryption {

    private final Map<Session, SecretKey> sessionKeyMap = new HashMap<>();

    @Override
    public String encrypt(Network network, String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(network.getSession()));
            byte[] cipherText = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Cannot encrypt input key '" + input + "'", e);
        }
    }

    @Override
    public String decrypt(Network network, String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(network.getSession()));
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(input));
            return new String(plainText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Cannot decrypt input key '" + input + "'", e);
        }
    }

    private SecretKey getSecretKey(Session session) {
        SecretKey key = this.sessionKeyMap.get(session);
        if (key == null) {
            key = this.getKeyFromPassword(session);
            this.sessionKeyMap.put(session, key);
        }

        return key;
    }

    private SecretKey getKeyFromPassword(Session session) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(
                    session.config().get(MessagingOptions.ENCRYPTION_PASSWORD).toCharArray(),
                    session.config().get(MessagingOptions.ENCRYPTION_SALT_KEY).getBytes(), 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Cannot generate key from password '" + MessagingOptions.ENCRYPTION_PASSWORD + "'", e);
        }
    }
}
