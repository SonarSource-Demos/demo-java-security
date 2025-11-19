package demo.security.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ContactFeedbackUtil {
    
    private Connection connection;
    
    public ContactFeedbackUtil() throws SQLException {
        connection = DriverManager.getConnection(
                "myJDBCUrl", "myJDBCUser", "myJDBCPass");
    }
    
    // SQL Injection vulnerability - string concatenation
    public String storeFeedback(String name, String email, String feedback, String category) throws Exception {
        String feedbackId = generateFeedbackId();
        
        // SQL injection through string concatenation
        String query = "INSERT INTO feedback (id, name, email, feedback, category) VALUES ('" 
                + feedbackId + "', '" 
                + name + "', '" 
                + email + "', '" 
                + feedback + "', '" 
                + category + "')";
        
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        
        return feedbackId;
    }
    
    // SQL Injection vulnerability - string concatenation in WHERE clause
    public List<Map<String, String>> getFeedbackByEmail(String email) throws Exception {
        String query = "SELECT id, name, email, feedback, category FROM feedback WHERE email = '" + email + "'";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<Map<String, String>> feedbackList = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, String> feedback = new HashMap<>();
            feedback.put("id", resultSet.getString("id"));
            feedback.put("name", resultSet.getString("name"));
            feedback.put("email", resultSet.getString("email"));
            feedback.put("feedback", resultSet.getString("feedback"));
            feedback.put("category", resultSet.getString("category"));
            feedbackList.add(feedback);
        }
        
        return feedbackList;
    }
    
    // SQL Injection vulnerability - string concatenation
    public List<Map<String, String>> getFeedbackByCategory(String category) throws Exception {
        String query = "SELECT id, name, email, feedback, category FROM feedback WHERE category = '" + category + "'";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<Map<String, String>> feedbackList = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, String> feedback = new HashMap<>();
            feedback.put("id", resultSet.getString("id"));
            feedback.put("name", resultSet.getString("name"));
            feedback.put("email", resultSet.getString("email"));
            feedback.put("feedback", resultSet.getString("feedback"));
            feedback.put("category", resultSet.getString("category"));
            feedbackList.add(feedback);
        }
        
        return feedbackList;
    }
    
    public List<Map<String, String>> getAllFeedback() throws Exception {
        String query = "SELECT id, name, email, feedback, category FROM feedback";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<Map<String, String>> feedbackList = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, String> feedback = new HashMap<>();
            feedback.put("id", resultSet.getString("id"));
            feedback.put("name", resultSet.getString("name"));
            feedback.put("email", resultSet.getString("email"));
            feedback.put("feedback", resultSet.getString("feedback"));
            feedback.put("category", resultSet.getString("category"));
            feedbackList.add(feedback);
        }
        
        return feedbackList;
    }
    
    // Weak random number generation for ID - security vulnerability
    private String generateFeedbackId() {
        Random random = new Random();
        return "FB-" + random.nextInt(1000000);
    }
    
    // Path traversal vulnerability
    public String readFeedbackFile(String fileName) throws Exception {
        String basePath = "/var/feedback/";
        // No validation on fileName - path traversal vulnerability
        String fullPath = basePath + fileName;
        
        java.io.File file = new java.io.File(fullPath);
        java.util.Scanner scanner = new java.util.Scanner(file);
        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        
        return content.toString();
    }
}

