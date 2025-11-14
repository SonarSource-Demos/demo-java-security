package demo.security.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class FeedbackUtils {
    
    private static final String BASE_PATH = "/tmp/feedback/";
    
    // Path Traversal vulnerability - user input directly used in file path
    public static String readAttachment(String filename) throws IOException {
        File file = new File(BASE_PATH + filename);
        byte[] content = Files.readAllBytes(file.toPath());
        return new String(content);
    }
    
    // Path Traversal vulnerability - store attachment
    public static void storeAttachment(String filename) throws IOException {
        File file = new File(BASE_PATH + filename);
        Files.write(file.toPath(), "attachment content".getBytes());
    }
    
    // Command Injection vulnerability - user input in system command
    public static void processCategory(String category) throws IOException {
        String command = "echo Processing category: " + category;
        Runtime.getRuntime().exec(command);
    }
    
    // Weak cryptography vulnerability - using DES and weak key
    public static byte[] encryptFeedback(String message) throws Exception {
        String key = "weakkey1";
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(message.getBytes());
    }
    
    // Weak key generation - small RSA key size
    public static KeyPair generateFeedbackKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(512);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    // Command Injection - execute feedback report command
    public static String generateReport(String reportType) throws IOException {
        Process process = Runtime.getRuntime().exec("report_gen.sh " + reportType);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }
}

