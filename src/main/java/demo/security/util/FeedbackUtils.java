package demo.security.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FeedbackUtils {
    
    private Connection connection;
    
    public FeedbackUtils() throws SQLException {
        connection = DriverManager.getConnection(
            "jdbc:mydb://localhost:3306/feedback", 
            "feedbackUser", 
            "feedbackPass"
        );
    }
    
    public void storeFeedback(String name, String email, String message, String rating) throws SQLException {
        // SQL Injection vulnerability - concatenating user input
        String query = "INSERT INTO feedback (name, email, message, rating) VALUES ('" 
            + name + "', '" 
            + email + "', '" 
            + message + "', " 
            + rating + ")";
        
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
    
    public List<String> searchFeedback(String searchTerm) throws SQLException {
        // SQL Injection vulnerability - concatenating user input
        String query = "SELECT name, email, message FROM feedback WHERE message LIKE '%" 
            + searchTerm + "%'";
        
        List<String> results = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String email = resultSet.getString(2);
                String message = resultSet.getString(3);
                results.add(name + " (" + email + "): " + message);
            }
        }
        return results;
    }
    
    public String readAttachment(String filePath) throws IOException {
        // Path Traversal vulnerability - no validation on file path
        File file = new File(filePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return new String(fileContent, StandardCharsets.UTF_8);
    }
    
    public void executeValidation(String script) throws ScriptException {
        // Code Injection vulnerability - executing user-provided script
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(script);
    }
    
    public String hashEmail(String email) throws NoSuchAlgorithmException {
        // Weak cryptography - using MD5
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(email.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public SecretKey generateEncryptionKey() throws NoSuchAlgorithmException {
        // Weak cryptography - small key size
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(64);
        return keyGen.generateKey();
    }
    
    public void encryptFeedback(String feedback) throws NoSuchAlgorithmException {
        // Weak cryptography - using DES
        SecretKey key = generateEncryptionKey();
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipher.doFinal(feedback.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new NoSuchAlgorithmException("Encryption failed", e);
        }
    }
    
    public void deleteFeedbackFile(String fileName) throws IOException {
        // Path Traversal vulnerability - no validation
        File file = new File("/var/feedback/" + fileName);
        Files.delete(file.toPath());
    }
    
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
