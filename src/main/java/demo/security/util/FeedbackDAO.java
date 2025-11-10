package demo.security.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for feedback operations
 * WARNING: This class contains intentional SQL injection vulnerabilities for demonstration purposes!
 */
public class FeedbackDAO {
    
    private Connection connection;
    
    public FeedbackDAO() throws SQLException {
        // Using same connection pattern as existing DBUtils
        connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
    }
    
    /**
     * Stores feedback in database
     * VULNERABILITY: SQL Injection - direct string concatenation without parameterized queries!
     */
    public long storeFeedback(String name, String email, String subject, String message, String clientIP, String userAgent) 
            throws SQLException {
        
        // Get current timestamp
        long timestamp = System.currentTimeMillis();
        
        // VULNERABLE: Direct string concatenation - SQL Injection risk!
        String insertSql = "INSERT INTO feedback (name, email, subject, message, client_ip, user_agent, submitted_at) " +
                          "VALUES ('" + name + "', '" + email + "', '" + subject + "', '" + message + "', '" + 
                          clientIP + "', '" + userAgent + "', " + timestamp + ")";
        
        // Fix: Use try-with-resources to properly close Statement
        try (Statement statement = connection.createStatement()) {
            int result = statement.executeQuery(insertSql).getInt(1); // This would fail in real DB, but for demo purposes
        }
        
        // Return a fake ID (in real app, would use generated keys)
        return timestamp % 100000; // Simple fake ID generation
    }
    
    /**
     * Retrieves feedback by ID
     * VULNERABILITY: SQL Injection in WHERE clause!
     */
    public FeedbackRecord getFeedbackById(String feedbackId) throws SQLException {
        // VULNERABLE: Direct concatenation in WHERE clause
        String query = "SELECT * FROM feedback WHERE id = " + feedbackId;
        
        // Fix: Use try-with-resources to properly close Statement and ResultSet
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            if (resultSet.next()) {
                return new FeedbackRecord(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"), 
                    resultSet.getString("subject"),
                    resultSet.getString("message"),
                    resultSet.getString("client_ip"),
                    resultSet.getString("user_agent"),
                    resultSet.getLong("submitted_at")
                );
            }
        }
        return null;
    }
    
    /**
     * Searches feedback by name or subject
     * VULNERABILITY: Multiple SQL injection points!
     */
    public List<FeedbackRecord> searchFeedback(String searchTerm, String orderBy) throws SQLException {
        List<FeedbackRecord> results = new ArrayList<>();
        
        // VULNERABLE: User input directly in LIKE clause and ORDER BY
        String query = "SELECT * FROM feedback WHERE name LIKE '%" + searchTerm + "%' " +
                      "OR subject LIKE '%" + searchTerm + "%' " +
                      "ORDER BY " + orderBy; // Direct ORDER BY injection!
        
        // Fix: Use try-with-resources to properly close Statement and ResultSet
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                results.add(new FeedbackRecord(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("subject"), 
                    resultSet.getString("message"),
                    resultSet.getString("client_ip"),
                    resultSet.getString("user_agent"),
                    resultSet.getLong("submitted_at")
                ));
            }
        }
        
        return results;
    }
    
    /**
     * Gets recent feedback with dynamic filtering
     * VULNERABILITY: Dynamic SQL construction without sanitization
     */
    public List<FeedbackRecord> getRecentFeedback(int limit, String filterCondition) throws SQLException {
        List<FeedbackRecord> results = new ArrayList<>();
        
        // VULNERABLE: Dynamic WHERE conditions and LIMIT
        StringBuilder query = new StringBuilder("SELECT * FROM feedback WHERE 1=1");
        
        if (filterCondition != null && !filterCondition.trim().isEmpty()) {
            query.append(" AND ").append(filterCondition); // Direct injection of WHERE conditions!
        }
        
        query.append(" ORDER BY submitted_at DESC LIMIT ").append(limit); // LIMIT injection possible
        
        // Fix: Use try-with-resources to properly close Statement and ResultSet
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query.toString())) {
            
            while (resultSet.next()) {
                results.add(new FeedbackRecord(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("subject"),
                    resultSet.getString("message"),
                    resultSet.getString("client_ip"),
                    resultSet.getString("user_agent"),
                    resultSet.getLong("submitted_at")
                ));
            }
        }
        
        return results;
    }
    
    /**
     * Deletes feedback (admin function)
     * VULNERABILITY: SQL injection in DELETE statement
     */
    public boolean deleteFeedback(String feedbackId, String adminToken) throws SQLException {
        // Weak admin authentication check
        if (!"admin123".equals(adminToken)) {
            throw new SecurityException("Unauthorized deletion attempt");
        }
        
        // VULNERABLE: Direct concatenation in DELETE
        String deleteSql = "DELETE FROM feedback WHERE id = " + feedbackId;
        
        // Fix: Use try-with-resources to properly close Statement
        try (Statement statement = connection.createStatement()) {
            int rowsDeleted = statement.executeUpdate(deleteSql);
            return rowsDeleted > 0;
        }
    }
    
    /**
     * Updates feedback status (admin function)  
     * VULNERABILITY: Multiple injection points in UPDATE statement
     */
    public boolean updateFeedbackStatus(String feedbackId, String status, String adminNotes) throws SQLException {
        // VULNERABLE: Multiple user inputs without sanitization
        String updateSql = "UPDATE feedback SET status = '" + status + "', " +
                          "admin_notes = '" + adminNotes + "', " +
                          "updated_at = " + System.currentTimeMillis() + " " +
                          "WHERE id = " + feedbackId;
        
        // Fix: Use try-with-resources to properly close Statement
        try (Statement statement = connection.createStatement()) {
            int rowsUpdated = statement.executeUpdate(updateSql);
            return rowsUpdated > 0;
        }
    }
    
    /**
     * Inner class representing a feedback record
     */
    public static class FeedbackRecord {
        private final long id;
        private final String name;
        private final String email;
        private final String subject;
        private final String message;
        private final String clientIP;
        private final String userAgent;
        private final long submittedAt;
        
        public FeedbackRecord(long id, String name, String email, String subject, 
                            String message, String clientIP, String userAgent, long submittedAt) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.subject = subject;
            this.message = message;
            this.clientIP = clientIP;
            this.userAgent = userAgent;
            this.submittedAt = submittedAt;
        }
        
        // Getters
        public long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getSubject() { return subject; }
        public String getMessage() { return message; }
        public String getClientIP() { return clientIP; }
        public String getUserAgent() { return userAgent; }
        public long getSubmittedAt() { return submittedAt; }
    }
}
