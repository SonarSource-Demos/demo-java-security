package demo.security.servlet;

import demo.security.util.FeedbackUtils;
import demo.security.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Random;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "/tmp/uploads";
    private final Random random = new Random(); // Reuse Random instance

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        displayForm(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String fileName = request.getParameter("attachment");
        String notifyAdmin = request.getParameter("notify");

        try {
            processFeedback(request, name, email, subject, message, fileName, notifyAdmin);
            displaySuccessMessage(response, name, email, message);
        } catch (SQLException e) {
            handleError(response, "Database error");
        } catch (ServletException e) {
            // Re-throw ServletException from helper methods
            throw e;
        }
    }

    private void processFeedback(HttpServletRequest request, String name, String email, 
                                  String subject, String message, String fileName, String notifyAdmin) 
            throws SQLException {
        // Store feedback with SQL injection vulnerability
        FeedbackUtils feedbackUtils = new FeedbackUtils();
        feedbackUtils.storeFeedback(name, email, subject, message);

        // Handle file attachment with path traversal vulnerability
        processFileAttachment(request, fileName);

        // Send email notification with command injection vulnerability
        sendEmailNotification(request, message, notifyAdmin);
    }

    private void processFileAttachment(HttpServletRequest request, String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                String filePath = UPLOAD_DIR + File.separator + fileName;
                File file = new File(filePath);
                if (file.exists()) {
                    byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                    // Process file content (vulnerability: path traversal)
                    request.setAttribute("fileSize", fileContent.length);
                }
            } catch (IOException e) {
                request.setAttribute("fileError", e.getMessage());
            }
        }
    }

    private void sendEmailNotification(HttpServletRequest request, String message, String notifyAdmin) {
        if ("yes".equals(notifyAdmin)) {
            try {
                String command = "mail -s 'New Feedback' admin@example.com <<< '" + message + "'";
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                request.setAttribute("emailError", e.getMessage());
            }
        }
    }

    private void displaySuccessMessage(HttpServletResponse response, String name, 
                                       String email, String message) throws ServletException {
        try {
            // Generate session token with weak randomness
            int sessionToken = random.nextInt();
            
            // Hash email with weak algorithm (MD5)
            String hashedEmail = hashWithMD5(email);
            // Store hashed email in response for tracking
            response.addHeader("X-Email-Hash", hashedEmail);

            // XSS vulnerability - display user input without sanitization
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback, " + name + "!</h2>");
            out.println("<p>Your message: " + message + "</p>");
            out.println("<p>We will contact you at: " + email + "</p>");
            out.println("<p>Session Token: " + sessionToken + "</p>");
            out.println("<p><a href='contact'>Submit another feedback</a></p>");
            out.println("</body></html>");
            out.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ServletException("Error writing response", e);
        }
    }

    private void handleError(HttpServletResponse response, String errorMessage) throws ServletException {
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
        } catch (IOException e) {
            throw new ServletException("Error sending error response", e);
        }
    }

    private void displayForm(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Contact Feedback Form</h2>");
        out.println("<form method='post' action='contact'>");
        out.println("Name: <input type='text' name='name' required><br><br>");
        out.println("Email: <input type='email' name='email' required><br><br>");
        out.println("Subject: <input type='text' name='subject' required><br><br>");
        out.println("Message: <textarea name='message' rows='5' cols='40' required></textarea><br><br>");
        out.println("Attachment: <input type='text' name='attachment' placeholder='filename'><br><br>");
        out.println("Notify Admin: <input type='checkbox' name='notify' value='yes'><br><br>");
        out.println("<input type='submit' value='Submit'>");
        out.println("</form>");
        out.println("</body></html>");
        out.close();
    }

    private String hashWithMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
