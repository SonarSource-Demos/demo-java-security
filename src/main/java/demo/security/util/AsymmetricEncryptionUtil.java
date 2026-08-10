package demo.security.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;

public class AsymmetricEncryptionUtil {
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private KeyPair keyPair;

    public void generateKeys(int keySize) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstanceStrong();
        keyGen.initialize(keySize, random);
        this.keyPair = keyGen.generateKeyPair();
    }

    public String encrypt(String plaintext) throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not generated. Call generateKeys() first.");
        }

        Cipher cipher = newEncryptionCipher(keyPair.getPublic());
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static Cipher newEncryptionCipher(PublicKey publicKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher;
    }

    public String decrypt(String ciphertext) throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not generated. Call generateKeys() first.");
        }

        PrivateKey privateKey = keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            AsymmetricEncryptionUtil util = new AsymmetricEncryptionUtil();
            util.generateKeys(2048);
            String encrypted = util.encrypt("Hello, World!");
            System.out.println("Encrypted: " + encrypted);
            String decrypted = util.decrypt(encrypted);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}