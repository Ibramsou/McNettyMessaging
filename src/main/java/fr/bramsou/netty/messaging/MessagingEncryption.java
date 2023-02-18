package fr.bramsou.netty.messaging;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class MessagingEncryption {

    private static final SecretKey secretKey = getKeyFromPassword();

    private static SecretKey getKeyFromPassword() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(MessagingOptions.ENCRYPTION_PASSWORD.toCharArray(), MessagingOptions.ENCRYPTION_SALT_KEY.getBytes(), 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Cannot generate key from password '" + MessagingOptions.ENCRYPTION_PASSWORD + "'", e);
        }
    }

    public static String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Cannot encrypt input key '" + input + "'", e);
        }

    }

    public static String decrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(input));
            return new String(plainText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException |
                InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException("Cannot decrypt input key '" + input + "'", e);
        }
    }
}
