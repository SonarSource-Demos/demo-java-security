package demo.security.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Logger;

@WebServlet("/contactFeedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ContactFeedbackServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String feedbackMessage = request.getParameter("feedbackMessage");
        String enableCaptcha = request.getParameter("enableCaptcha");
        String captchaInput = request.getParameter("captchaInput");
        
        // CAPTCHA validation with security issue - weak validation
        if ("true".equals(enableCaptcha)) {
            HttpSession session = request.getSession();
            String captchaAnswer = (String) session.getAttribute("captchaAnswer");
            
            // Intentional vulnerability: case-insensitive comparison
            if (captchaAnswer == null || !captchaAnswer.equalsIgnoreCase(captchaInput)) {
                response.sendRedirect("contact.jsp?error=Invalid CAPTCHA. Please try again.");
                return;
            }
        }
        
        try {
            // SQL Injection vulnerability - direct string concatenation
            Connection connection = DriverManager.getConnection(
                    "myJDBCUrl", "myJDBCUser", "myJDBCPass");
            String query = "INSERT INTO feedback (name, email, subject, message) VALUES ('" 
                    + name + "', '" + email + "', '" + subject + "', '" + feedbackMessage + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
            connection.close();
            
            // Path traversal vulnerability - user input directly used in file path
            String logDir = request.getParameter("logDir");
            if (logDir != null && !logDir.isEmpty()) {
                String logPath = "/var/log/feedback/" + logDir + "/feedback.log";
                File logFile = new File(logPath);
                FileWriter writer = new FileWriter(logFile, true);
                writer.write("Feedback from: " + name + "\n");
                writer.close();
            }
            
            // XSS vulnerability - direct output without escaping
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Name: " + name + "</p>");
            out.println("<p>Email: " + email + "</p>");
            out.println("<p>Subject: " + subject + "</p>");
            out.println("<p>Message: " + feedbackMessage + "</p>");
            out.println("<br><a href='contact.jsp'>Submit another feedback</a>");
            out.println("</body></html>");
            out.close();
            
        } catch (Exception e) {
            logger.severe("Error processing feedback: " + e.getMessage());
            response.sendRedirect("contact.jsp?error=An error occurred while processing your feedback.");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("contact.jsp");
    }
}

