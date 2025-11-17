package demo.security.servlet;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    private static final String CONTENT_TYPE_HTML = "text/html";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String feedbackId = request.getParameter("id");
        String format = request.getParameter("format");
        
        try {
            // SQL Injection vulnerability - concatenating user input directly
            Connection connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
            String query = "SELECT * FROM feedback WHERE id = '" + feedbackId + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter out = response.getWriter();
            
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String message = resultSet.getString("message");
                
                // XSS vulnerability - outputting user data without escaping
                out.print("<h2>Feedback from: " + name + "</h2>");
                out.print("<p>Email: " + email + "</p>");
                out.print("<div>" + message + "</div>");
            }
            
            // Path traversal vulnerability
            if (format != null && format.equals("file")) {
                String filename = request.getParameter("filename");
                File file = new File("/var/feedback/" + filename);
                String content = new String(Files.readAllBytes(file.toPath()));
                out.print("<pre>" + content + "</pre>");
            }
            
            out.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String attachmentPath = request.getParameter("attachment");
        String notifyCmd = request.getParameter("notify");
        
        try {
            // Weak hashing algorithm - using MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(email.getBytes());
            String emailHash = Base64.encodeBase64String(hash);
            
            // SQL Injection in INSERT statement
            Connection connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
            String insertQuery = "INSERT INTO feedback (name, email, email_hash, message) VALUES ('" 
                + name + "', '" + email + "', '" + emailHash + "', '" + message + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(insertQuery);
            
            // Command injection vulnerability
            if (notifyCmd != null && !notifyCmd.isEmpty()) {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("sh -c " + notifyCmd);
                try {
                    process.waitFor();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ServletException("Process interrupted", ie);
                }
            }
            
            // Path traversal in file operations
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                File attachment = new File("/var/uploads/" + attachmentPath);
                if (attachment.exists()) {
                    Files.readAllBytes(attachment.toPath());
                    // Process attachment
                }
            }
            
            // Insecure random number generation - using non-secure random
            int confirmationCode = new Random(System.currentTimeMillis()).nextInt(999999);
            
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter out = response.getWriter();
            
            // XSS vulnerability in response
            out.print("<h1>Thank you for your feedback, " + name + "!</h1>");
            out.print("<p>We will contact you at: " + email + "</p>");
            out.print("<p>Your confirmation code is: " + confirmationCode + "</p>");
            
            // Hardcoded credentials
            if (email.equals("admin@example.com")) {
                String adminPassword = "admin123";
                out.print("<p>Admin access granted with password: " + adminPassword + "</p>");
            }
            
            out.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException("Error saving feedback", e);
        }
    }
    
    // Additional method with LDAP injection
    protected void searchUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        
        try {
            // LDAP Injection vulnerability - building query with user input
            String ldapFilter = username; // Used in LDAP query
            
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter out = response.getWriter();
            out.print("<p>Searching for user with filter: " + ldapFilter + "</p>");
            out.close();
        } catch (Exception e) {
            throw new ServletException("Error searching user", e);
        }
    }
}

