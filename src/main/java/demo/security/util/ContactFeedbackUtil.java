package demo.security.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ContactFeedbackUtil provides utility methods for handling contact feedback.
 * This class demonstrates various security vulnerabilities for educational purposes.
 */
public class ContactFeedbackUtil {
    
    /**
     * Search feedback by keyword - VULNERABLE to SQL Injection
     */
    public static List<String> searchFeedback(Connection connection, String keyword) throws Exception {
        // SQL Injection vulnerability - concatenating user input directly
        String query = "SELECT id, subject, message FROM feedback WHERE " +
                      "subject LIKE '%" + keyword + "%' OR message LIKE '%" + keyword + "%'";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<String> results = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String subject = resultSet.getString("subject");
            String message = resultSet.getString("message");
            results.add(id + ": " + subject + " - " + message);
        }
        
        return results;
    }
    
    /**
     * Get feedback by category - VULNERABLE to SQL Injection
     */
    public static List<String> getFeedbackByCategory(Connection connection, String category) throws Exception {
        // SQL Injection vulnerability
        String query = "SELECT * FROM feedback WHERE category = '" + category + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<String> feedbacks = new ArrayList<>();
        while (resultSet.next()) {
            feedbacks.add(resultSet.getString("message"));
        }
        
        return feedbacks;
    }
    
    /**
     * Read feedback file - VULNERABLE to Path Traversal
     */
    public static String readFeedbackFile(String feedbackId) throws IOException {
        // Path traversal vulnerability - no validation of feedbackId
        String filePath = "/tmp/feedback/" + feedbackId + ".txt";
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    /**
     * Export feedback to file - VULNERABLE to Command Injection
     */
    public static void exportFeedback(String feedbackId, String outputFormat) throws IOException {
        // Command injection vulnerability
        String command = "cat /tmp/feedback/" + feedbackId + ".txt | convert - " + outputFormat;
        Runtime.getRuntime().exec(command);
    }
    
    /**
     * Validate email - WEAK validation
     */
    public static boolean isValidEmail(String email) {
        // Weak regex that can be bypassed
        return email != null && email.contains("@");
    }
    
    /**
     * Sanitize filename - INSUFFICIENT sanitization
     */
    public static String sanitizeFilename(String filename) {
        // Insufficient sanitization - only removes some dangerous characters
        // Still vulnerable to path traversal with encoded characters
        return filename.replace("/", "").replace("\\", "");
    }
    
    /**
     * Hash sensitive data - USING WEAK ALGORITHM
     */
    public static String hashData(String data) {
        try {
            // Using weak MD5 algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
    
    /**
     * Generate temporary file - INSECURE temp file creation
     */
    public static File createTempFeedbackFile(String prefix) throws IOException {
        // Insecure temp file creation - predictable filename
        File tempDir = new File("/tmp/feedback_temp");
        if (!tempDir.exists()) {
            tempDir.mkdir(); // Insecure directory creation with default permissions
        }
        
        String filename = prefix + System.currentTimeMillis() + ".tmp";
        return new File(tempDir, filename);
    }
    
    /**
     * Deserialize feedback object - VULNERABLE to insecure deserialization
     */
    public static Object deserializeFeedback(String serializedData) throws IOException, ClassNotFoundException {
        // Insecure deserialization vulnerability
        byte[] data = java.util.Base64.getDecoder().decode(serializedData);
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
    
    /**
     * Log feedback - LOGS SENSITIVE INFORMATION
     */
    public static void logFeedback(String email, String message, String ipAddress) {
        // Logging sensitive information without masking
        System.out.println("Feedback received from: " + email);
        System.out.println("IP Address: " + ipAddress);
        System.out.println("Message: " + message);
    }
    
    /**
     * Check if user is admin - HARDCODED CREDENTIALS
     */
    public static boolean isAdmin(String username, String password) {
        // Hardcoded credentials
        return "admin".equals(username) && "admin123".equals(password);
    }
    
    /**
     * Build redirect URL - VULNERABLE to Open Redirect
     */
    public static String buildRedirectUrl(String baseUrl, String returnUrl) {
        // Open redirect vulnerability - no validation of returnUrl
        return baseUrl + "?redirect=" + returnUrl;
    }
    
    /**
     * Process feedback with regex - VULNERABLE to ReDoS
     */
    public static boolean validateFeedbackMessage(String message) {
        // ReDoS vulnerability - catastrophic backtracking
        String regex = "(a+)+b";
        return Pattern.matches(regex, message);
    }
    
    /**
     * Get feedback statistics - VULNERABLE to timing attack
     */
    public static boolean verifyFeedbackToken(String providedToken, String expectedToken) {
        // Timing attack vulnerability - using string comparison
        return providedToken.equals(expectedToken);
    }
    
    /**
     * Encrypt sensitive data - WEAK ENCRYPTION
     */
    public static String encryptSensitiveData(String data) {
        // Weak encryption - just XOR with a fixed key
        StringBuilder encrypted = new StringBuilder();
        int key = 42; // Hardcoded weak key
        
        for (char c : data.toCharArray()) {
            encrypted.append((char) (c ^ key));
        }
        
        return encrypted.toString();
    }
}

