package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    
    private static final String DIV_END_TAG = "</div>";
    private static final String HTML_DIV_START = "<div>";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Display existing feedback messages (with XSS vulnerability)
        String feedbackId = request.getParameter("feedbackId");
        
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            if (feedbackId != null) {
                try {
                    DBUtils db = new DBUtils();
                    List<String> feedback = db.findFeedback(feedbackId);
                    out.print("<h2>Feedback Results</h2>");
                    feedback.forEach(result -> 
                        out.print(HTML_DIV_START + "Feedback: " + result + DIV_END_TAG)  // XSS vulnerability
                    );
                } catch (Exception e) {
                    out.print(HTML_DIV_START + "Error: " + e.getMessage() + DIV_END_TAG);  // Information disclosure
                }
            } else {
                out.print("<h2>Contact Feedback System</h2>");
                out.print("<p>No feedback ID provided</p>");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String priority = request.getParameter("priority");
        String attachmentPath = request.getParameter("attachmentPath");
        String userPrefs = request.getParameter("userPrefs");
        String xmlFeedback = request.getParameter("xmlFeedback");
        
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            try {
                String feedbackId = saveFeedbackToDatabase(name, email, message, priority);
                
                processAttachment(attachmentPath, out);
                processUserPreferences(userPrefs, out);
                processXmlFeedback(xmlFeedback, out);
                sendEmailNotification(email, out);
                performLdapLookup(request, out);
                
                displaySuccessResponse(out, name, email, message, feedbackId);
                
            } catch (Exception e) {
                out.print(HTML_DIV_START + "Error processing feedback: " + e.getMessage() + DIV_END_TAG);  // Information disclosure
                throw new ContactProcessingException("Failed to process contact feedback", e);
            }
        }
    }
    
    private String saveFeedbackToDatabase(String name, String email, String message, String priority) throws SQLException, java.security.NoSuchAlgorithmException {
        // Generate insecure feedback ID using MD5 (weak cryptography - intentional vulnerability)
        MessageDigest md = MessageDigest.getInstance("MD5");
        String feedbackId = new BigInteger(1, md.digest((name + email).getBytes())).toString(16);
        
        // SQL Injection vulnerability - direct string concatenation (intentional)
        String query = "INSERT INTO feedback (id, name, email, message, priority) VALUES ('" 
            + feedbackId + "', '" + name + "', '" + email + "', '" + message + "', '" + priority + "')";
            
        try (Connection connection = DriverManager.getConnection("mYJDBCUrl", "myJDBCUser", "myJDBCPass");
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
        
        return feedbackId;
    }
    
    private void processAttachment(String attachmentPath, PrintWriter out) {
        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            try {
                // Path Traversal vulnerability - unsanitized file access (intentional)
                Files.readAllBytes(Paths.get(attachmentPath));
                out.print(HTML_DIV_START + "Attachment processed: " + attachmentPath + DIV_END_TAG);
            } catch (Exception e) {
                out.print(HTML_DIV_START + "Failed to process attachment" + DIV_END_TAG);
            }
        }
    }
    
    private void processUserPreferences(String userPrefs, PrintWriter out) {
        if (userPrefs != null) {
            try {
                byte[] decoded = Base64.decodeBase64(userPrefs);
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded))) {
                    in.readObject();  // Unsafe deserialization (intentional vulnerability)
                }
                out.print(HTML_DIV_START + "User preferences loaded" + DIV_END_TAG);
            } catch (Exception e) {
                out.print(HTML_DIV_START + "Failed to load preferences" + DIV_END_TAG);
            }
        }
    }
    
    private void processXmlFeedback(String xmlFeedback, PrintWriter out) {
        if (xmlFeedback != null && !xmlFeedback.isEmpty()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                // No secure processing features set - vulnerable to XXE (intentional)
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.parse(new ByteArrayInputStream(xmlFeedback.getBytes()));
                out.print(HTML_DIV_START + "XML feedback processed" + DIV_END_TAG);
            } catch (Exception e) {
                out.print(HTML_DIV_START + "Failed to process XML feedback" + DIV_END_TAG);
            }
        }
    }
    
    private void sendEmailNotification(String email, PrintWriter out) {
        if (email != null && !email.isEmpty()) {
            try {
                // Command Injection vulnerability (intentional)
                String command = "echo 'Feedback received from " + email + "' | mail -s 'New Feedback' admin@company.com";
                Runtime.getRuntime().exec(command);
                out.print(HTML_DIV_START + "Email notification sent" + DIV_END_TAG);
            } catch (Exception e) {
                out.print(HTML_DIV_START + "Failed to send notification" + DIV_END_TAG);
            }
        }
    }
    
    private void performLdapLookup(HttpServletRequest request, PrintWriter out) {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader != null) {
            String username = sessionHeader.getUsername();
            try {
                // LDAP injection vulnerability - unsanitized input (intentional)
                String ldapQuery = "(&(objectClass=person)(uid=" + username + "))";
                out.print(HTML_DIV_START + "LDAP query executed: " + ldapQuery + DIV_END_TAG);  // Information disclosure
            } catch (Exception e) {
                out.print(HTML_DIV_START + "LDAP lookup failed" + DIV_END_TAG);
            }
        }
    }
    
    private void displaySuccessResponse(PrintWriter out, String name, String email, String message, String feedbackId) {
        out.print("<h2>Feedback Submitted Successfully!</h2>");
        out.print(HTML_DIV_START + "Name: " + name + DIV_END_TAG);  // XSS vulnerability (intentional)
        out.print(HTML_DIV_START + "Email: " + email + DIV_END_TAG);  // XSS vulnerability (intentional)  
        out.print(HTML_DIV_START + "Message: " + message + DIV_END_TAG);  // XSS vulnerability (intentional)
        out.print(HTML_DIV_START + "Feedback ID: " + feedbackId + DIV_END_TAG);
    }
    
    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth != null) {
            try {
                byte[] decoded = Base64.decodeBase64(sessionAuth);
                try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded))) {
                    return (SessionHeader) in.readObject();  // Insecure deserialization (intentional)
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    // Custom exception to replace generic Exception
    private static class ContactProcessingException extends RuntimeException {
        public ContactProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}