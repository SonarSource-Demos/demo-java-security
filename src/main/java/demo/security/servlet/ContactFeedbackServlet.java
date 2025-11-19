package demo.security.servlet;

import demo.security.util.ContactFeedbackUtil;
import demo.security.util.ContactFeedbackException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    // Constants for string literals
    private static final String FIELD_NAME = "name";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_FEEDBACK = "feedback";
    private static final String FIELD_CATEGORY = "category";
    private static final String FIELD_ID = "id";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter(FIELD_NAME);
        String email = request.getParameter(FIELD_EMAIL);
        String feedback = request.getParameter(FIELD_FEEDBACK);
        String category = request.getParameter(FIELD_CATEGORY);
        
        try {
            ContactFeedbackUtil util;
            try {
                util = new ContactFeedbackUtil();
            } catch (java.sql.SQLException e) {
                throw new ContactFeedbackException("Database connection failed", e);
            }
            
            // Store feedback with SQL injection vulnerability
            String feedbackId = util.storeFeedback(name, email, feedback, category);
            
            // Retrieve and display feedback
            List<Map<String, String>> feedbackList = util.getFeedbackByEmail(email);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Your feedback ID: " + feedbackId + "</p>");
            out.println("<h3>Your previous feedback:</h3>");
            
            for (Map<String, String> fb : feedbackList) {
                // XSS vulnerability - directly outputting user input
                out.println("<div>");
                out.println("<p><b>Name:</b> " + fb.get(FIELD_NAME) + "</p>");
                out.println("<p><b>Email:</b> " + fb.get(FIELD_EMAIL) + "</p>");
                out.println("<p><b>Category:</b> " + fb.get(FIELD_CATEGORY) + "</p>");
                out.println("<p><b>Feedback:</b> " + fb.get(FIELD_FEEDBACK) + "</p>");
                out.println("</div><hr>");
            }
            
            out.println("</body></html>");
            out.close();
            
        } catch (ContactFeedbackException e) {
            throw new ServletException("Error processing feedback", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchEmail = request.getParameter(FIELD_EMAIL);
        String searchCategory = request.getParameter(FIELD_CATEGORY);
        
        try {
            ContactFeedbackUtil util;
            try {
                util = new ContactFeedbackUtil();
            } catch (java.sql.SQLException e) {
                throw new ContactFeedbackException("Database connection failed", e);
            }
            List<Map<String, String>> feedbackList;
            
            if (searchEmail != null && !searchEmail.isEmpty()) {
                // SQL injection vulnerability in search
                feedbackList = util.getFeedbackByEmail(searchEmail);
            } else if (searchCategory != null && !searchCategory.isEmpty()) {
                feedbackList = util.getFeedbackByCategory(searchCategory);
            } else {
                feedbackList = util.getAllFeedback();
            }
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<html><body>");
            out.println("<h2>Feedback Results</h2>");
            
            for (Map<String, String> fb : feedbackList) {
                // XSS vulnerability
                out.println("<div>");
                out.println("<p><b>ID:</b> " + fb.get(FIELD_ID) + "</p>");
                out.println("<p><b>Name:</b> " + fb.get(FIELD_NAME) + "</p>");
                out.println("<p><b>Email:</b> " + fb.get(FIELD_EMAIL) + "</p>");
                out.println("<p><b>Category:</b> " + fb.get(FIELD_CATEGORY) + "</p>");
                out.println("<p><b>Feedback:</b> " + fb.get(FIELD_FEEDBACK) + "</p>");
                out.println("</div><hr>");
            }
            
            out.println("</body></html>");
            out.close();
            
        } catch (ContactFeedbackException e) {
            throw new ServletException("Error retrieving feedback", e);
        }
    }
}
