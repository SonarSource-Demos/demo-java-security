package demo.security.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FeedbackUtils {
    
    private static final Logger LOGGER = Logger.getLogger(FeedbackUtils.class.getName());
    
    // Hardcoded credentials - Security Hotspot
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feedback";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password123";
    
    // SQL Injection vulnerability
    public String getFeedbackByEmail(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM feedback WHERE email = '" + email + "'";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                StringBuilder result = new StringBuilder();
                while (resultSet.next()) {
                    result.append(resultSet.getString("message")).append("\n");
                }
                return result.toString();
            }
        }
    }
    
    // Path traversal vulnerability
    public String readFeedbackFile(String filename) throws IOException {
        // No validation on filename - allows path traversal
        File file = new File("/var/feedback/" + filename);
        return new String(Files.readAllBytes(file.toPath()));
    }
    
    // Command injection vulnerability
    public void exportFeedback(String format, String outputFile) throws IOException {
        String command = "feedback-export --format=" + format + " --output=" + outputFile;
        Runtime.getRuntime().exec(command);
    }
    
    // Weak cryptography - using DES  
    public String encryptFeedback(String data) throws GeneralSecurityException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey secretKey = keyGen.generateKey();
        
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    // Weak hashing - using MD5
    public String hashEmail(String email) throws java.security.NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(email.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
    
    // Insecure random number generation
    public int generateConfirmationCode() {
        Random random = new Random();
        return random.nextInt(999999);
    }
    
    // SSRF vulnerability - Server-Side Request Forgery
    public String fetchExternalFeedback(String url) throws IOException {
        URL feedbackUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) feedbackUrl.openConnection();
        connection.setRequestMethod("GET");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        
        return content.toString();
    }
    
    // XML External Entity (XXE) vulnerability potential
    public void processFeedbackXML(String xmlData) throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, IOException {
        // Vulnerable XML processing without disabling external entities
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(new java.io.ByteArrayInputStream(xmlData.getBytes()));
    }
    
    // Zip Slip vulnerability
    public void extractFeedbackArchive(String zipFilePath, String destDir) throws IOException {
        File destDirectory = new File(destDir);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            
            while (zipEntry != null) {
                // No validation on zipEntry.getName() - allows zip slip
                File newFile = new File(destDirectory, zipEntry.getName());
                
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        }
    }
    
    // Insecure deserialization
    public Object deserializeFeedback(String serializedData) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(serializedData);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }
    
    // Reflected XSS helper (generates vulnerable HTML)
    public String generateFeedbackHtml(String userName, String userMessage) {
        // No HTML escaping - leads to XSS
        return "<div class='feedback'>" +
               "<h3>From: " + userName + "</h3>" +
               "<p>" + userMessage + "</p>" +
               "</div>";
    }
    
    // Sensitive data exposure in logs
    public void logFeedback(String email, String message, String creditCard) {
        LOGGER.info("Feedback received from: " + email);
        LOGGER.info("Message: " + message);
        LOGGER.warning("Credit Card: " + creditCard); // Sensitive data in logs
    }
    
    // Missing authentication check
    public void deleteFeedback(int feedbackId) throws SQLException {
        // No authentication or authorization check
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "DELETE FROM feedback WHERE id = " + feedbackId;
            statement.executeUpdate(query);
        }
    }
}

