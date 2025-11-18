package demo.security.util;

import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

public class ContactFeedbackUtil {
    
    private Connection connection;
    
    public ContactFeedbackUtil() throws SQLException {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:h2:mem:testdb", "sa", "");
        } catch (SQLException e) {
            // Allow creation without database for testing utility methods
            connection = null;
        }
    }
    
    // SQL Injection vulnerability - concatenating user input
    public void saveFeedback(String name, String email, String message) throws SQLException {
        String query = "INSERT INTO feedback (name, email, message, submitted_at) VALUES ('" 
                + name + "', '" + email + "', '" + message + "', CURRENT_TIMESTAMP)";
        Statement statement = connection.createStatement();
        statement.executeQuery(query);
    }
    
    // Path Traversal vulnerability - no validation on file path
    public void saveAttachment(String fileName, byte[] content) throws IOException {
        String uploadDir = "/tmp/uploads/";
        File file = new File(uploadDir + fileName); // Path traversal vulnerability
        FileUtils.writeByteArrayToFile(file, content);
    }
    
    // Weak cryptography - using DES
    public String encryptSensitiveData(String data) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES"); // Weak algorithm
        keyGen.init(56); // Weak key size
        SecretKey key = keyGen.generateKey();
        
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); // ECB mode is insecure
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    // Weak hashing - using MD5
    public String hashEmail(String email) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5"); // Weak hash algorithm
        byte[] hash = md.digest(email.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    // Predictable random - using java.util.Random instead of SecureRandom
    public String generateToken() {
        Random random = new Random(); // Predictable random
        return String.valueOf(random.nextInt(999999));
    }
    
    // XSS vulnerability - no output encoding
    public List<Map<String, String>> getFeedbackList() throws SQLException {
        String query = "SELECT name, email, message FROM feedback ORDER BY submitted_at DESC LIMIT 10";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        
        List<Map<String, String>> feedbackList = new ArrayList<>();
        while (rs.next()) {
            Map<String, String> feedback = new HashMap<>();
            feedback.put("name", rs.getString("name"));
            feedback.put("email", rs.getString("email"));
            feedback.put("message", rs.getString("message"));
            feedbackList.add(feedback);
        }
        return feedbackList;
    }
    
    // Command Injection vulnerability
    public String executeSystemCommand(String userInput) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("echo " + userInput); // Command injection
        return "Command executed";
    }
    
    // Hard-coded credentials
    public boolean authenticateAdmin(String username, String password) {
        String adminUser = "admin"; // Hard-coded
        String adminPass = "admin123"; // Hard-coded password
        return username.equals(adminUser) && password.equals(adminPass);
    }
    
    // Information disclosure - logging sensitive data
    public void logFeedback(String email, String message) {
        System.out.println("Feedback from: " + email); // Sensitive data in logs
        System.out.println("Message: " + message);
    }
    
    // Resource leak - connection not closed
    public List<String> searchFeedback(String searchTerm) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        // Connection not closed - resource leak
        String query = "SELECT message FROM feedback WHERE message LIKE '%" + searchTerm + "%'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        List<String> results = new ArrayList<>();
        while (rs.next()) {
            results.add(rs.getString("message"));
        }
        return results;
    }
}

