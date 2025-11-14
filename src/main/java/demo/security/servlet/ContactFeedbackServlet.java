package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * ContactFeedbackServlet handles user feedback submissions.
 * This servlet demonstrates various security vulnerabilities for educational purposes:
 * - XSS (Cross-Site Scripting)
 * - SQL Injection
 * - Path Traversal
 * - Command Injection
 * - Insecure File Upload
 * - Information Disclosure
 */
@WebServlet("/contact/feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ContactFeedbackServlet.class.getName());
    private static final String FEEDBACK_DIR = "/tmp/feedback/";
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Create feedback directory if it doesn't exist
        File dir = new File(FEEDBACK_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vulnerable to XSS - directly echoing user input
        String feedbackId = request.getParameter("id");
        String action = request.getParameter("action");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        if ("view".equals(action) && feedbackId != null) {
            // Path traversal vulnerability - no validation of feedbackId
            String filePath = FEEDBACK_DIR + feedbackId + ".txt";
            
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                // XSS vulnerability - directly outputting user content
                out.println("<h2>Feedback Details</h2>");
                out.println("<div>" + content + "</div>");
            } catch (IOException e) {
                // Information disclosure - exposing full file path
                out.println("<p>Error reading feedback file: " + filePath + "</p>");
                out.println("<p>Error message: " + e.getMessage() + "</p>");
            }
        } else if ("search".equals(action)) {
            // SQL injection vulnerability
            String searchTerm = request.getParameter("term");
            out.println("<h2>Search Results for: " + searchTerm + "</h2>");
            // Note: This would call a vulnerable DBUtils method in production
            // For demo purposes, we're keeping it simple
        } else if ("export".equals(action)) {
            // Command injection vulnerability
            String format = request.getParameter("format");
            String filename = request.getParameter("filename");
            
            try {
                // Command injection - user input used in system command
                Runtime runtime = Runtime.getRuntime();
                String command = "cat " + FEEDBACK_DIR + filename + " | convert - " + format;
                Process process = runtime.exec(command);
                
                InputStream inputStream = process.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + "." + format);
                
                OutputStream outStream = response.getOutputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.close();
                return;
            } catch (IOException e) {
                out.println("<p>Export error: " + e.getMessage() + "</p>");
            }
        }
        
        // XSS vulnerability - echoing user input
        if (feedbackId != null) {
            out.println("<p>Feedback ID: " + feedbackId + "</p>");
        }
        
        out.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String category = request.getParameter("category");
        String priority = request.getParameter("priority");
        
        // Weak validation - only checking for null
        if (name == null || email == null || message == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
            return;
        }
        
        // SQL Injection vulnerability - building query with concatenation
        String feedbackId = UUID.randomUUID().toString();
        
        // Note: In production, this would call DBUtils with vulnerable concatenated SQL:
        // String query = "INSERT INTO feedback (id, name, email, subject, message, category, priority) VALUES ('" 
        //     + feedbackId + "','" + name + "','" + email + "','" + subject + "','" 
        //     + message + "','" + category + "','" + priority + "')";
        
        // Insecure file creation - path traversal vulnerability
        String filename = request.getParameter("filename");
        if (filename == null) {
            filename = feedbackId;
        }
        
        // No sanitization of filename - allows path traversal
        File feedbackFile = new File(FEEDBACK_DIR + filename + ".txt");
        
        try (FileWriter writer = new FileWriter(feedbackFile)) {
            writer.write("Name: " + name + "\n");
            writer.write("Email: " + email + "\n");
            writer.write("Subject: " + subject + "\n");
            writer.write("Category: " + category + "\n");
            writer.write("Priority: " + priority + "\n");
            writer.write("Message: " + message + "\n");
        }
        
        // Insecure cookie creation - no HttpOnly, Secure flags
        WebUtils webUtils = new WebUtils();
        webUtils.addCookie(response, "last_feedback_id", feedbackId);
        webUtils.addCookie(response, "user_email", email); // Storing sensitive data in cookie
        
        // Session fixation vulnerability
        HttpSession session = request.getSession(true);
        session.setAttribute("user_name", name);
        session.setAttribute("user_email", email);
        
        // XSS vulnerability - reflecting user input
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Thank you for your feedback, " + name + "!</h2>");
        out.println("<p>We have received your message:</p>");
        out.println("<div style='border:1px solid #ccc; padding:10px;'>");
        out.println("<strong>Subject:</strong> " + subject + "<br>");
        out.println("<strong>Message:</strong> " + message);
        out.println("</div>");
        out.println("<p>Your feedback ID: <strong>" + feedbackId + "</strong></p>");
        out.println("<p><a href='/contact/feedback?action=view&id=" + feedbackId + "'>View your feedback</a></p>");
        out.println("</body></html>");
        out.close();
        
        // Logging sensitive information
        logger.info("Feedback submitted by: " + email + " with message: " + message);
    }
    
    /**
     * Process file attachment (vulnerable to unrestricted file upload)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String filename = request.getHeader("X-Filename");
        
        // No validation of file type or extension
        if (filename == null || filename.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Filename required");
            return;
        }
        
        // Path traversal vulnerability
        File uploadFile = new File(FEEDBACK_DIR + filename);
        
        // No size limit check
        try (FileOutputStream fos = new FileOutputStream(uploadFile);
             InputStream is = request.getInputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.println("File uploaded successfully: " + filename);
        out.close();
    }
    
    /**
     * Delete feedback (vulnerable to IDOR - Insecure Direct Object Reference)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String feedbackId = request.getParameter("id");
        
        // No authorization check - anyone can delete any feedback
        if (feedbackId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Feedback ID required");
            return;
        }
        
        // Path traversal vulnerability
        File feedbackFile = new File(FEEDBACK_DIR + feedbackId + ".txt");
        
        if (feedbackFile.exists()) {
            boolean deleted = feedbackFile.delete();
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.println("Feedback deleted: " + deleted);
            out.close();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feedback not found");
        }
    }
}

