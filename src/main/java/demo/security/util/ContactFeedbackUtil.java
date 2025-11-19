package demo.security.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Random;

public class ContactFeedbackUtil {
    
    private Connection connection;
    
    public ContactFeedbackUtil() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/feedbackdb", "feedbackuser", "feedbackpass");
    }
    
    // SQL Injection vulnerability
    public void storeFeedback(String name, String email, String message) throws SQLException {
        String query = "INSERT INTO feedback (name, email, message) VALUES ('" 
                + name + "', '" + email + "', '" + message + "')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
    
    // Path Traversal vulnerability
    public String readFeedbackFile(String filename) throws IOException {
        String basePath = "/var/feedback/";
        String fullPath = basePath + filename;
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    // Command Injection vulnerability
    public String exportFeedback(String email, String format) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        String command = "feedback-exporter --email=" + email + " --format=" + format;
        Process process = runtime.exec(command);
        process.waitFor();
        return "Export completed for " + email;
    }
    
    // Weak cryptography - using DES
    public String encryptEmail(String email) throws java.security.GeneralSecurityException {
        // Using weak DES encryption
        String key = "mySecKey";
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
        byte[] desKeyBytes = new byte[8];
        System.arraycopy(keyBytes, 0, desKeyBytes, 0, 8);
        
        SecretKeySpec secretKey = new SecretKeySpec(desKeyBytes, "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] encrypted = cipher.doFinal(email.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    private static final Random random = new Random();
    
    // Weak random number generation
    public String generateFeedbackToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            token.append((char) ('a' + random.nextInt(26)));
        }
        return token.toString();
    }
    
    // Hardcoded credentials
    public Connection getAdminConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/feedbackdb", 
                "admin", 
                "admin123");
    }
    
    // LDAP Injection vulnerability
    public String searchUserByEmail(String email) {
        // This would be used in LDAP search context
        return "(mail=" + email + ")";
    }
    
    // XPath Injection vulnerability
    public String buildXPathQuery(String feedbackId) {
        return "//feedback[@id='" + feedbackId + "']";
    }
    
    // Regular expression DoS vulnerability
    public boolean validateEmail(String email) {
        String regex = "(a+)+b";
        return email.matches(regex);
    }
}

