package demo.security.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ContactFeedbackUtil {
    
    // Constants for string literals
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_FEEDBACK = "feedback";
    private static final String FIELD_CATEGORY = "category";
    
    private Connection connection;
    private final Random random;
    private final String feedbackBasePath;
    
    public ContactFeedbackUtil() throws SQLException {
        connection = DriverManager.getConnection(
                "myJDBCUrl", "myJDBCUser", "myJDBCPass");
        this.random = new Random();
        this.feedbackBasePath = System.getProperty("feedback.base.path", "/var/feedback/");
    }
    
    // SQL Injection vulnerability - string concatenation
    public String storeFeedback(String name, String email, String feedback, String category) throws ContactFeedbackException {
        String feedbackId = generateFeedbackId();
        
        // SQL injection through string concatenation
        String query = "INSERT INTO feedback (id, name, email, feedback, category) VALUES ('" 
                + feedbackId + "', '" 
                + name + "', '" 
                + email + "', '" 
                + feedback + "', '" 
                + category + "')";
        
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new ContactFeedbackException("Failed to store feedback", e);
        }
        
        return feedbackId;
    }
    
    // SQL Injection vulnerability - string concatenation in WHERE clause
    public List<Map<String, String>> getFeedbackByEmail(String email) throws ContactFeedbackException {
        String query = "SELECT id, name, email, feedback, category FROM feedback WHERE email = '" + email + "'";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            List<Map<String, String>> feedbackList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> feedback = new HashMap<>();
                feedback.put(FIELD_ID, resultSet.getString(FIELD_ID));
                feedback.put(FIELD_NAME, resultSet.getString(FIELD_NAME));
                feedback.put(FIELD_EMAIL, resultSet.getString(FIELD_EMAIL));
                feedback.put(FIELD_FEEDBACK, resultSet.getString(FIELD_FEEDBACK));
                feedback.put(FIELD_CATEGORY, resultSet.getString(FIELD_CATEGORY));
                feedbackList.add(feedback);
            }
            
            return feedbackList;
        } catch (SQLException e) {
            throw new ContactFeedbackException("Failed to get feedback by email", e);
        }
    }
    
    // SQL Injection vulnerability - string concatenation
    public List<Map<String, String>> getFeedbackByCategory(String category) throws ContactFeedbackException {
        String query = "SELECT id, name, email, feedback, category FROM feedback WHERE category = '" + category + "'";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            List<Map<String, String>> feedbackList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> feedback = new HashMap<>();
                feedback.put(FIELD_ID, resultSet.getString(FIELD_ID));
                feedback.put(FIELD_NAME, resultSet.getString(FIELD_NAME));
                feedback.put(FIELD_EMAIL, resultSet.getString(FIELD_EMAIL));
                feedback.put(FIELD_FEEDBACK, resultSet.getString(FIELD_FEEDBACK));
                feedback.put(FIELD_CATEGORY, resultSet.getString(FIELD_CATEGORY));
                feedbackList.add(feedback);
            }
            
            return feedbackList;
        } catch (SQLException e) {
            throw new ContactFeedbackException("Failed to get feedback by category", e);
        }
    }
    
    public List<Map<String, String>> getAllFeedback() throws ContactFeedbackException {
        String query = "SELECT id, name, email, feedback, category FROM feedback";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            List<Map<String, String>> feedbackList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> feedback = new HashMap<>();
                feedback.put(FIELD_ID, resultSet.getString(FIELD_ID));
                feedback.put(FIELD_NAME, resultSet.getString(FIELD_NAME));
                feedback.put(FIELD_EMAIL, resultSet.getString(FIELD_EMAIL));
                feedback.put(FIELD_FEEDBACK, resultSet.getString(FIELD_FEEDBACK));
                feedback.put(FIELD_CATEGORY, resultSet.getString(FIELD_CATEGORY));
                feedbackList.add(feedback);
            }
            
            return feedbackList;
        } catch (SQLException e) {
            throw new ContactFeedbackException("Failed to get all feedback", e);
        }
    }
    
    // Weak random number generation for ID - security vulnerability
    private String generateFeedbackId() {
        return "FB-" + random.nextInt(1000000);
    }
    
    // Path traversal vulnerability
    public String readFeedbackFile(String fileName) throws ContactFeedbackException {
        // No validation on fileName - path traversal vulnerability
        String fullPath = feedbackBasePath + fileName;
        
        java.io.File file = new java.io.File(fullPath);
        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            return content.toString();
        } catch (java.io.FileNotFoundException e) {
            throw new ContactFeedbackException("Failed to read feedback file", e);
        }
    }
}
