package demo.security.servlet;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Random;
import java.util.regex.Pattern;

@WebServlet("/contact")
public class ContactFeedbackServlet extends HttpServlet {
    
    // Insecure randomness - using java.util.Random instead of SecureRandom
    private final Random random = new Random();
    
    // Hard-coded credentials - security vulnerability
    private static final String DB_PASSWORD = "password";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            if ("submit".equals(action)) {
                handleFeedbackSubmission(request, response);
            } else if ("search".equals(action)) {
                handleFeedbackSearch(request, response);
            } else if ("export".equals(action)) {
                handleExportFeedback(request, response);
            } else if ("upload".equals(action)) {
                handleFileUpload(request, response);
            } else if ("process".equals(action)) {
                handleProcessXml(request, response);
            } else if ("auth".equals(action)) {
                handleAdminAuth(request, response);
            }
        } catch (IOException e) {
            throw new ServletException("Error processing request", e);
        }
    }

    // SQL Injection vulnerability - concatenating user input directly into SQL query
    private void handleFeedbackSubmission(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String feedback = request.getParameter("feedback");
        String category = request.getParameter("category");
        
        try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/feedback", "root", DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            
            // SQL Injection vulnerability
            String query = "INSERT INTO feedback (name, email, feedback, category) VALUES ('" 
                    + name + "', '" + email + "', '" + feedback + "', '" + category + "')";
            statement.executeUpdate(query);
            
            // XSS vulnerability - reflecting user input without sanitization
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Thank you " + name + " for your feedback!</h2>");
            out.println("<p>Your feedback: " + feedback + "</p>");
            out.println("</body></html>");
            out.close();
            
        } catch (Exception e) {
            throw new IOException("Database error", e);
        }
    }

    // SQL Injection in search functionality
    private void handleFeedbackSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchTerm = request.getParameter("search");
        
        try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/feedback", "root", DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            
            // SQL Injection vulnerability
            String query = "SELECT * FROM feedback WHERE category = '" + searchTerm + "' OR name LIKE '%" + searchTerm + "%'";
            statement.executeQuery(query);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h2>Search results for: " + searchTerm + "</h2>");
            out.close();
            
        } catch (Exception e) {
            throw new IOException("Search error", e);
        }
    }

    // Path Traversal vulnerability - user input used directly in file path
    private void handleExportFeedback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = request.getParameter("filename");
        String format = request.getParameter("format");
        
        // Path Traversal vulnerability
        String exportPath = "/var/exports/" + filename + "." + format;
        
        // Command Injection vulnerability
        try {
            String[] command = {"export-tool", "--file=" + filename, "--format=" + format};
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            throw new IOException("Export error", e);
        }
        
        // Also write to user-controlled path
        try {
            Files.write(Paths.get(exportPath), "Export data".getBytes());
        } catch (Exception e) {
            throw new IOException("Write error", e);
        }
        
        response.getWriter().println("Export completed to: " + exportPath);
    }

    // Insecure file upload - no validation
    private void handleFileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Part filePart = null;
        try {
            filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            
            // Path Traversal vulnerability - no validation of filename
            String uploadPath = "/var/uploads/" + fileName;
            
            InputStream fileContent = filePart.getInputStream();
            Files.copy(fileContent, Paths.get(uploadPath));
            
            response.getWriter().println("File uploaded: " + fileName);
            
        } catch (Exception e) {
            throw new IOException("Upload error", e);
        }
    }

    // XXE (XML External Entity) vulnerability
    private void handleProcessXml(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String xmlData = request.getParameter("xmldata");
        
        try {
            // XXE vulnerability - DocumentBuilderFactory not configured securely
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            ByteArrayInputStream input = new ByteArrayInputStream(xmlData.getBytes());
            builder.parse(input);
            
            response.getWriter().println("XML processed successfully");
            
        } catch (Exception e) {
            throw new IOException("XML processing error", e);
        }
    }

    // Insecure randomness for session tokens
    private String generateSessionToken() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.encodeBase64String(bytes);
    }

    // Weak cryptographic hash
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            return Base64.encodeBase64String(hash);
        } catch (Exception e) {
            return null;
        }
    }

    // Insecure authentication with hardcoded credentials and additional vulnerabilities
    private void handleAdminAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        // Hard-coded credentials vulnerability
        if ("admin".equals(username) && DB_PASSWORD.equals(password)) {
            // Weak cryptographic hash - MD5
            String hashedPassword = hashPassword(password);
            
            // Generate insecure session token
            String sessionToken = generateSessionToken();
            
            // Regular expression DoS vulnerability
            if (validateEmail(email)) {
                response.getWriter().println("Admin authenticated with hashed password: " + hashedPassword + " and token: " + sessionToken);
            }
            
            // LDAP Injection vulnerability
            searchUserInLdap(username);
        }
    }

    // LDAP Injection vulnerability
    private void searchUserInLdap(String username) {
        try {
            // LDAP Injection - user input directly in filter
            String filter = "(uid=" + username + ")";
            // Simulate LDAP search - in real code this would be used with DirContext.search()
            // Using filter for LDAP query would be vulnerable to LDAP injection
            if (!filter.isEmpty()) {
                // LDAP search would be performed here
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    // Regular expression DoS vulnerability
    private boolean validateEmail(String email) {
        String regex = "(a+)+@example.com";
        return Pattern.matches(regex, email);
    }
}

