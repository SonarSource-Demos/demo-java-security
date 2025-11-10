package demo.security.servlet;

import demo.security.util.EmailUtil;
import demo.security.util.FeedbackDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Fix: Define constant for repeated HTML strings
    private static final String HTML_END = "</body></html>";
    private static final String HTML_START_ERROR = "<html><body>";
    private static final String HTML_START_SUCCESS = "<html><head><title>Feedback Submitted</title></head><body>";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Extract form parameters (no input validation - vulnerability!)
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String userAgent = request.getHeader("User-Agent");
        String clientIP = request.getRemoteAddr();
        
        response.setContentType("text/html");
        
        // Fix: Handle IOException from getWriter()
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new ServletException("Failed to get response writer", e);
        }
        
        try {
            // Store feedback in database (SQL Injection vulnerability!)
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            long feedbackId = feedbackDAO.storeFeedback(name, email, subject, message, clientIP, userAgent);
            
            // Send email notification (Email Header Injection vulnerability!)
            EmailUtil emailUtil = new EmailUtil();
            boolean emailSent = emailUtil.sendFeedbackNotification(name, email, subject, message);
            
            // Success response (XSS vulnerability - unescaped user input!)
            out.println(HTML_START_SUCCESS);
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Hello <strong>" + name + "</strong>,</p>");
            out.println("<p>Your feedback has been submitted successfully!</p>");
            out.println("<p><strong>Subject:</strong> " + subject + "</p>");
            out.println("<p><strong>Message Preview:</strong></p>");
            out.println("<div style='background:#f9f9f9;padding:10px;border-left:3px solid #ccc;'>");
            out.println(message); // Direct output without HTML escaping - XSS vulnerability!
            out.println("</div>");
            out.println("<p>Feedback ID: <strong>#" + feedbackId + "</strong></p>");
            
            if (emailSent) {
                out.println("<p style='color:green;'>✓ Email notification sent successfully!</p>");
            } else {
                out.println("<p style='color:orange;'>⚠ Feedback saved but email notification failed.</p>");
            }
            
            out.println("<p><a href='contact.jsp'>← Submit another feedback</a></p>");
            out.println("<p><a href='admin/feedback?id=" + feedbackId + "'>View your feedback</a></p>"); // IDOR vulnerability
            out.println(HTML_END);
            
        } catch (SQLException e) {
            // Information disclosure vulnerability - exposing internal details!
            out.println(HTML_START_ERROR);
            out.println("<h2 style='color:red;'>Database Error</h2>");
            out.println("<p>Sorry, we encountered a database error while processing your feedback:</p>");
            out.println("<pre style='background:#ffe6e6;padding:10px;'>");
            out.println("Error: " + e.getMessage()); // Exposing sensitive database info
            out.println("SQL State: " + e.getSQLState());
            out.println("Error Code: " + e.getErrorCode());
            out.println("</pre>");
            out.println("<p>Please try again later or contact support.</p>");
            out.println(HTML_END);
            
        } catch (Exception e) {
            // Generic exception handling with stack trace exposure
            out.println(HTML_START_ERROR);
            out.println("<h2 style='color:red;'>Unexpected Error</h2>");
            out.println("<p>An unexpected error occurred:</p>");
            out.println("<pre style='background:#ffe6e6;padding:10px;'>");
            e.printStackTrace(out); // Full stack trace exposure - vulnerability!
            out.println("</pre>");
            out.println(HTML_END);
        }
    }
    
    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the form page
        // Fix: Handle IOException from sendRedirect()
        try {
            response.sendRedirect("contact.jsp");
        } catch (IOException e) {
            throw new ServletException("Failed to redirect to contact form", e);
        }
    }
}
