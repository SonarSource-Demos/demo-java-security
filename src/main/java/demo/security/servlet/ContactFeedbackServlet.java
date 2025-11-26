package demo.security.servlet;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String FEEDBACK_DIR = "/tmp/feedback/";

    @Override
    public void init() throws ServletException {
        super.init();
        // Ensure feedback directory exists
        new File(FEEDBACK_DIR).mkdirs();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Retrieve feedback by ID with SQL Injection vulnerability
        String feedbackId = request.getParameter("id");
        if (feedbackId != null) {
            try {
                List<String> feedbacks = getFeedbackById(feedbackId);
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h2>Feedback Results</h2>");
                for (String feedback : feedbacks) {
                    // XSS vulnerability - no escaping
                    out.println("<p>" + feedback + "</p>");
                }
                out.println("</body></html>");
                out.close();
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
        
        // View feedback file with Path Traversal vulnerability
        String filename = request.getParameter("file");
        if (filename != null) {
            // Path traversal vulnerability
            File file = new File(FEEDBACK_DIR + filename);
            if (file.exists()) {
                response.setContentType("text/plain");
                Files.copy(file.toPath(), response.getOutputStream());
            } else {
                response.sendError(404, "File not found");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Optional CAPTCHA validation
        String captchaEnabled = request.getParameter("captcha_enabled");
        if ("true".equals(captchaEnabled)) {
            String captchaInput = request.getParameter("captcha");
            String sessionCaptcha = (String) session.getAttribute("captcha");
            
            // Weak comparison - timing attack vulnerability
            if (!captchaInput.equals(sessionCaptcha)) {
                response.sendError(400, "Invalid CAPTCHA");
                return;
            }
        }
        
        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String priority = request.getParameter("priority");
        
        // Weak random token generation
        Random random = new Random();
        String token = String.valueOf(random.nextInt(1000000));
        
        // Weak MD5 hashing for email
        String emailHash = DigestUtils.md5Hex(email);
        
        // Store feedback in database with SQL Injection
        try {
            saveFeedback(name, email, subject, message, priority, token, emailHash);
        } catch (Exception e) {
            throw new ServletException("Error saving feedback", e);
        }
        
        // Save feedback to file with predictable name
        String filename = "feedback_" + token + ".txt";
        File feedbackFile = new File(FEEDBACK_DIR + filename);
        
        // Write sensitive data to file with weak permissions
        try (PrintWriter fileWriter = new PrintWriter(new FileWriter(feedbackFile))) {
            fileWriter.println("Name: " + name);
            fileWriter.println("Email: " + email);
            fileWriter.println("Subject: " + subject);
            fileWriter.println("Message: " + message);
            fileWriter.println("Priority: " + priority);
            fileWriter.println("Token: " + token);
            fileWriter.println("Email Hash: " + emailHash);
        }
        
        // Log sensitive information
        System.out.println("Feedback submitted by: " + email + " with token: " + token);
        
        // Redirect with token in URL
        response.sendRedirect("/contact-feedback?success=true&token=" + token);
    }

    private List<String> getFeedbackById(String feedbackId) throws Exception {
        // SQL Injection vulnerability
        Connection connection = DriverManager.getConnection(
                "myJDBCUrl", "myJDBCUser", "myJDBCPass");
        
        String query = "SELECT * FROM feedback WHERE id = " + feedbackId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<String> feedbacks = new ArrayList<>();
        while (resultSet.next()) {
            String feedback = "Name: " + resultSet.getString("name") + 
                            ", Subject: " + resultSet.getString("subject") +
                            ", Message: " + resultSet.getString("message");
            feedbacks.add(feedback);
        }
        
        connection.close();
        return feedbacks;
    }

    private void saveFeedback(String name, String email, String subject, 
                             String message, String priority, String token, String emailHash) throws Exception {
        // SQL Injection vulnerability
        Connection connection = DriverManager.getConnection(
                "myJDBCUrl", "myJDBCUser", "myJDBCPass");
        
        String query = "INSERT INTO feedback (name, email, subject, message, priority, token, email_hash) " +
                      "VALUES ('" + name + "', '" + email + "', '" + subject + "', '" + 
                      message + "', '" + priority + "', '" + token + "', '" + emailHash + "')";
        
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        connection.close();
    }
}
