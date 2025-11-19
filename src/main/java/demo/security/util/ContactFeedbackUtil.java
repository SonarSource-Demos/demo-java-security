package demo.security.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContactFeedbackUtil {

    private Connection connection;

    public ContactFeedbackUtil() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/feedback", "feedbackUser", "feedbackPass123");
    }

    // SQL Injection vulnerability - concatenating user input into SQL query
    public List<String> getFeedbackByEmail(String email) throws SQLException {
        String query = "SELECT * FROM feedback WHERE email = '" + email + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> feedbacks = new ArrayList<>();
        while (resultSet.next()) {
            feedbacks.add(resultSet.getString("message"));
        }
        return feedbacks;
    }

    // SQL Injection vulnerability - concatenating user input in query
    public void saveFeedback(String name, String email, String message, String rating) throws SQLException {
        String insertQuery = "INSERT INTO feedback (name, email, message, rating) VALUES ('" 
            + name + "', '" + email + "', '" + message + "', '" + rating + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(insertQuery);
    }

    // Path Traversal vulnerability - user input used directly in file path
    public String readFeedbackFile(String filename) throws IOException {
        File file = new File("/var/feedback/" + filename);
        return new String(Files.readAllBytes(file.toPath()));
    }

    // Path Traversal vulnerability - writing to user-controlled path
    public void saveFeedbackToFile(String filename, String content) throws IOException {
        FileWriter writer = new FileWriter("/var/feedback/" + filename);
        writer.write(content);
        writer.close();
    }

    // Weak cryptographic algorithm (DES)
    public String encryptFeedback(String feedback, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(feedback.getBytes(StandardCharsets.UTF_8));
        return new String(encrypted);
    }

    // Weak hash algorithm (MD5)
    public String hashEmail(String email) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(email.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Insecure Random for token generation
    public String generateFeedbackToken() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }

    // Deserialization vulnerability
    public Object deserializeFeedback(byte[] data) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }

    // Command Injection vulnerability - executing system command with user input
    public String exportFeedbackToFile(String format) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("export_feedback --format=" + format);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }

    // LDAP Injection vulnerability
    public String searchUserByEmail(String email) {
        String filter = "(mail=" + email + ")";
        return "ldap://localhost:389/" + filter;
    }

    // XPath Injection vulnerability
    public String searchFeedbackByRating(String rating) {
        return "//feedback[rating='" + rating + "']";
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
