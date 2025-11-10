package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import demo.security.util.Utils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/contact")
public class ContactFormServlet extends HttpServlet {
    
    private static final String UPLOAD_DIR = "/tmp/uploads/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("search".equals(action)) {
            handleSearch(request, response);
        } else if ("view".equals(action)) {
            handleViewFeedback(request, response);
        } else {
            // Default: show the contact form
            response.sendRedirect("contact.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("submit".equals(action)) {
            handleSubmitFeedback(request, response);
        } else if ("upload".equals(action)) {
            handleFileUpload(request, response);
        } else if ("process_session".equals(action)) {
            handleSessionProcessing(request, response);
        }
    }

    // XSS Vulnerability: Direct output of user input without escaping
    private void handleSubmitFeedback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String attachment = request.getParameter("attachment");
        
        // Input validation vulnerability: No validation of input length or content
        if (name == null) name = "";
        if (email == null) email = "";
        if (subject == null) subject = "";
        if (message == null) message = "";
        if (attachment == null) attachment = "";
        
        try {
            // SQL Injection vulnerability: User input directly concatenated in SQL
            DBUtils db = new DBUtils();
            db.storeFeedback(name, email, subject, message, attachment);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            // XSS vulnerability: User input displayed without HTML escaping
            out.println("<html><head><title>Feedback Submitted</title></head><body>");
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p><strong>Name:</strong> " + name + "</p>");
            out.println("<p><strong>Email:</strong> " + email + "</p>");
            out.println("<p><strong>Subject:</strong> " + subject + "</p>");
            out.println("<p><strong>Message:</strong> " + message + "</p>");
            if (!attachment.isEmpty()) {
                out.println("<p><strong>Attachment:</strong> " + attachment + "</p>");
            }
            out.println("<br><a href='contact.jsp'>Submit another feedback</a>");
            out.println("<br><a href='contact?action=search'>Search feedback</a>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // Information disclosure vulnerability: Exposing stack traces
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error occurred while processing feedback:</h2>");
            out.println("<pre>" + getStackTraceAsString(e) + "</pre>");
            out.println("</body></html>");
        }
    }

    // SQL Injection vulnerability in search functionality
    private void handleSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchTerm = request.getParameter("q");
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            response.sendRedirect("contact.jsp?error=Please enter a search term");
            return;
        }
        
        try {
            DBUtils db = new DBUtils();
            List<String> results = db.searchFeedback(searchTerm);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<html><head><title>Feedback Search Results</title></head><body>");
            out.println("<h2>Search Results for: " + searchTerm + "</h2>");
            
            if (results.isEmpty()) {
                out.println("<p>No feedback found matching your search criteria.</p>");
            } else {
                out.println("<div>");
                for (String result : results) {
                    // XSS vulnerability: Database content displayed without escaping
                    out.println("<div style='border: 1px solid #ccc; margin: 10px; padding: 10px;'>");
                    out.println(result);
                    out.println("</div>");
                }
                out.println("</div>");
            }
            
            out.println("<br><a href='contact.jsp'>Back to Contact Form</a>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // Information disclosure vulnerability
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Database Error:</h2>");
            out.println("<p>Connection String: mYJDBCUrl</p>");
            out.println("<p>Error Details:</p>");
            out.println("<pre>" + getStackTraceAsString(e) + "</pre>");
            out.println("</body></html>");
        }
    }

    // SQL Injection vulnerability in view functionality
    private void handleViewFeedback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String feedbackId = request.getParameter("id");
        
        if (feedbackId == null) {
            response.sendRedirect("contact.jsp?error=Missing feedback ID");
            return;
        }
        
        try {
            DBUtils db = new DBUtils();
            String feedback = db.getFeedbackById(feedbackId);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<html><head><title>View Feedback</title></head><body>");
            
            if (feedback != null) {
                out.println("<h2>Feedback Details:</h2>");
                // XSS vulnerability: Database content with HTML displayed without proper escaping
                out.println("<div>" + feedback + "</div>");
            } else {
                out.println("<p>Feedback not found.</p>");
            }
            
            out.println("<br><a href='contact.jsp'>Back to Contact Form</a>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // Information disclosure vulnerability
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error retrieving feedback:</h2>");
            out.println("<pre>" + getStackTraceAsString(e) + "</pre>");
            out.println("</body></html>");
        }
    }

    // Path Traversal vulnerability in file upload
    private void handleFileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("filename");
        String content = request.getParameter("content");
        
        if (fileName == null || content == null) {
            response.sendRedirect("contact.jsp?error=Missing file parameters");
            return;
        }
        
        try {
            // Path traversal vulnerability: No validation of file path
            String filePath = UPLOAD_DIR + fileName;
            File file = new File(filePath);
            
            // Create directories if they don't exist (potential directory traversal)
            file.getParentFile().mkdirs();
            
            // Write file content
            FileUtils.writeStringToFile(file, content, "UTF-8");
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>File uploaded successfully!</h2>");
            out.println("<p>File saved to: " + filePath + "</p>");
            out.println("<p>Absolute path: " + file.getAbsolutePath() + "</p>");
            out.println("<br><a href='contact.jsp'>Back to Contact Form</a>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // Information disclosure vulnerability
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>File upload error:</h2>");
            out.println("<p>System info: " + System.getProperty("user.dir") + "</p>");
            out.println("<pre>" + getStackTraceAsString(e) + "</pre>");
            out.println("</body></html>");
        }
    }

    // Deserialization vulnerability similar to UserServlet
    private void handleSessionProcessing(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionData = request.getParameter("session_data");
        
        if (sessionData == null) {
            response.getWriter().println("Missing session data");
            return;
        }
        
        try {
            // Deserialization vulnerability: Deserializing untrusted data
            byte[] decoded = Base64.decodeBase64(sessionData);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded));
            SessionHeader sessionHeader = (SessionHeader) in.readObject();
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Session processed successfully!</h2>");
            out.println("<p>Username: " + sessionHeader.getUsername() + "</p>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // Information disclosure vulnerability
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Session processing error:</h2>");
            out.println("<pre>" + getStackTraceAsString(e) + "</pre>");
            out.println("</body></html>");
        }
    }

    // Helper method to convert stack trace to string (information disclosure)
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
