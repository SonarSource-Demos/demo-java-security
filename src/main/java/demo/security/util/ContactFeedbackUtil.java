package demo.security.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ContactFeedbackUtil {
    
    private static final Logger logger = Logger.getLogger(ContactFeedbackUtil.class.getName());
    private static final String FEEDBACK_DIR = "/tmp/feedback/";
    private Connection connection;
    
    public ContactFeedbackUtil() throws SQLException {
        connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
    }
    
    // Vulnerable: SQL injection through unsanitized parameters
    public void storeFeedback(String name, String email, String subject, 
                              String message, String priority) throws SQLException {
        String query = "INSERT INTO feedback (name, email, subject, message, priority) " +
                      "VALUES ('" + name + "', '" + email + "', '" + subject + "', '" + 
                      message + "', '" + priority + "')";
        
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
        
        logger.info(() -> "Feedback stored from: " + email);
    }
    
    // Vulnerable: SQL injection when searching feedback
    public List<String> searchFeedback(String searchTerm) throws SQLException {
        String query = "SELECT * FROM feedback WHERE subject LIKE '%" + searchTerm + "%' " +
                      "OR message LIKE '%" + searchTerm + "%'";
        
        List<String> results = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                results.add(resultSet.getString("subject"));
            }
        }
        
        return results;
    }
    
    // Vulnerable: Path traversal attack
    public String readFeedbackFile(String feedbackId) throws java.io.IOException {
        String filePath = FEEDBACK_DIR + feedbackId + ".txt";
        
        // No validation of feedbackId - allows path traversal
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        
        logger.info(() -> "Reading feedback file: " + filePath);
        return new String(fileBytes);
    }
    
    // Vulnerable: Path traversal when deleting feedback files
    public void deleteFeedbackFile(String feedbackId) throws java.io.IOException {
        String filePath = FEEDBACK_DIR + feedbackId;
        
        if (Files.exists(Paths.get(filePath))) {
            Files.delete(Paths.get(filePath));
            logger.info(() -> "Deleted feedback file: " + filePath);
        }
    }
    
    // Vulnerable: XML External Entity (XXE) injection
    public String parseFeedbackXml(String xmlContent) throws javax.xml.parsers.ParserConfigurationException, 
            org.xml.sax.SAXException, java.io.IOException {
        javax.xml.parsers.DocumentBuilderFactory factory = 
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
        
        // No XXE protection - vulnerable to XXE attacks
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(
            new java.io.ByteArrayInputStream(xmlContent.getBytes())
        );
        
        return doc.getDocumentElement().getNodeName();
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.severe("Error closing connection: " + e.getMessage());
        }
    }
}

