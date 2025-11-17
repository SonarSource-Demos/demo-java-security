package demo.security.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContactFeedbackUtil {

    private Connection connection;
    
    // Hardcoded credentials - Security vulnerability
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feedback_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "P@ssw0rd123!";
    
    public ContactFeedbackUtil() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * SQL Injection vulnerability - concatenating user input directly into SQL query
     */
    public List<Feedback> searchFeedback(String searchTerm) throws Exception {
        // SQL Injection vulnerability
        String query = "SELECT id, name, email, message FROM feedback WHERE message LIKE '%" + searchTerm + "%'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<Feedback> feedbacks = new ArrayList<>();
        while (resultSet.next()) {
            Feedback feedback = new Feedback();
            feedback.setId(resultSet.getInt("id"));
            feedback.setName(resultSet.getString("name"));
            feedback.setEmail(resultSet.getString("email"));
            feedback.setMessage(resultSet.getString("message"));
            feedbacks.add(feedback);
        }
        return feedbacks;
    }

    /**
     * SQL Injection vulnerability in insert operation
     */
    public void saveFeedback(String name, String email, String message, String category) throws Exception {
        // SQL Injection vulnerability
        String query = "INSERT INTO feedback (name, email, message, category, created_at) VALUES ('" 
            + name + "', '" + email + "', '" + message + "', '" + category + "', NOW())";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    /**
     * Path Traversal vulnerability - allows reading arbitrary files
     */
    public String readFeedbackTemplate(String templateName) throws IOException {
        // Path traversal vulnerability
        String filePath = "/var/www/templates/" + templateName;
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Command Injection vulnerability - executing system commands with user input
     */
    public String exportFeedbackToFile(String feedbackId, String format) throws IOException {
        // Command injection vulnerability
        String command = "feedback-exporter --id=" + feedbackId + " --format=" + format;
        Process process = Runtime.getRuntime().exec(command);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    /**
     * Weak cryptography - using MD5 for password hashing
     */
    public String hashPassword(String password) {
        try {
            // Weak cryptography - MD5 is not secure for password hashing
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insecure random number generation for CAPTCHA
     */
    public String generateCaptchaCode() {
        // Insecure random - using java.util.Random instead of SecureRandom
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }

    /**
     * File upload vulnerability - no validation of file type or content
     */
    public String uploadAttachment(HttpServletRequest request) throws Exception {
        // File upload vulnerability - no validation
        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        List<FileItem> items = upload.parseRequest(request);
        
        for (FileItem item : items) {
            if (!item.isFormField()) {
                // Path traversal vulnerability in filename
                String fileName = item.getName();
                String uploadPath = "/var/www/uploads/" + fileName;
                File uploadedFile = new File(uploadPath);
                item.write(uploadedFile);
                return uploadPath;
            }
        }
        return null;
    }

    /**
     * Information disclosure - exposing stack traces
     */
    public String deleteFeedback(String feedbackId) {
        try {
            // SQL Injection vulnerability
            String query = "DELETE FROM feedback WHERE id = " + feedbackId;
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return "Feedback deleted successfully";
        } catch (Exception e) {
            // Information disclosure - exposing full stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "Error: " + sw.toString();
        }
    }

    /**
     * LDAP Injection vulnerability
     */
    public boolean authenticateUser(String username, String password) throws Exception {
        // LDAP injection vulnerability
        String filter = "(uid=" + username + ")";
        // Simulated LDAP query - in real implementation would connect to LDAP
        return username.equals("admin") && password.equals(DB_PASSWORD);
    }

    /**
     * XPath Injection vulnerability
     */
    public String getFeedbackStats(String year) throws Exception {
        // XPath injection vulnerability
        String xpathQuery = "//feedback[@year='" + year + "']/count()";
        // Simulated XPath query
        return "Statistics for year: " + year;
    }

    /**
     * Insecure deserialization vulnerability
     */
    public Object deserializeFeedback(byte[] data) throws Exception {
        // Insecure deserialization
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }

    /**
     * Regular Expression Denial of Service (ReDoS)
     */
    public boolean validateEmail(String email) {
        // ReDoS vulnerability - catastrophic backtracking
        String regex = "^([a-zA-Z0-9]+)*@([a-zA-Z0-9]+)*\\.([a-zA-Z0-9]+)*$";
        return email.matches(regex);
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Inner class for Feedback data
    public static class Feedback {
        private int id;
        private String name;
        private String email;
        private String message;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

