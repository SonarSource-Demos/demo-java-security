package demo.security.servlet;

import demo.security.util.ContactFeedbackUtil;
import demo.security.util.ContactFeedbackException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
    private static final String HTML_START = "<html><body>";
    private static final String HTML_END = "</body></html>";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter(FIELD_NAME);
        String email = request.getParameter(FIELD_EMAIL);
        String feedback = request.getParameter(FIELD_FEEDBACK);
        String category = request.getParameter(FIELD_CATEGORY);
        
        ContactFeedbackUtil util = createUtil();
        
        // Store feedback with SQL injection vulnerability
        String feedbackId = storeFeedback(util, name, email, feedback, category);
        
        // Retrieve and display feedback
        List<Map<String, String>> feedbackList = getFeedbackByEmail(util, email);
        
        renderFeedbackSubmissionResponse(response, feedbackId, feedbackList);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchEmail = request.getParameter(FIELD_EMAIL);
        String searchCategory = request.getParameter(FIELD_CATEGORY);
        
        ContactFeedbackUtil util = createUtil();
        List<Map<String, String>> feedbackList = getFeedbackList(util, searchEmail, searchCategory);
        
        renderFeedbackSearchResponse(response, feedbackList);
    }
    
    private ContactFeedbackUtil createUtil() throws ServletException {
        try {
            return new ContactFeedbackUtil();
        } catch (SQLException e) {
            throw new ServletException("Database connection failed", e);
        }
    }
    
    private String storeFeedback(ContactFeedbackUtil util, String name, String email, String feedback, String category) throws ServletException {
        try {
            return util.storeFeedback(name, email, feedback, category);
        } catch (ContactFeedbackException e) {
            throw new ServletException("Failed to store feedback", e);
        }
    }
    
    private List<Map<String, String>> getFeedbackByEmail(ContactFeedbackUtil util, String email) throws ServletException {
        try {
            return util.getFeedbackByEmail(email);
        } catch (ContactFeedbackException e) {
            throw new ServletException("Failed to retrieve feedback", e);
        }
    }
    
    private List<Map<String, String>> getFeedbackList(ContactFeedbackUtil util, String searchEmail, String searchCategory) throws ServletException {
        try {
            if (searchEmail != null && !searchEmail.isEmpty()) {
                // SQL injection vulnerability in search
                return util.getFeedbackByEmail(searchEmail);
            } else if (searchCategory != null && !searchCategory.isEmpty()) {
                return util.getFeedbackByCategory(searchCategory);
            } else {
                return util.getAllFeedback();
            }
        } catch (ContactFeedbackException e) {
            throw new ServletException("Failed to search feedback", e);
        }
    }
    
    private void renderFeedbackSubmissionResponse(HttpServletResponse response, String feedbackId, List<Map<String, String>> feedbackList) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println(HTML_START);
        out.println("<h2>Thank you for your feedback!</h2>");
        out.println("<p>Your feedback ID: " + feedbackId + "</p>");
        out.println("<h3>Your previous feedback:</h3>");
        
        renderFeedbackItems(out, feedbackList);
        
        out.println(HTML_END);
        out.close();
    }
    
    private void renderFeedbackSearchResponse(HttpServletResponse response, List<Map<String, String>> feedbackList) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println(HTML_START);
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
        
        out.println(HTML_END);
        out.close();
    }
    
    private void renderFeedbackItems(PrintWriter out, List<Map<String, String>> feedbackList) {
        for (Map<String, String> fb : feedbackList) {
            // XSS vulnerability - directly outputting user input
            out.println("<div>");
            out.println("<p><b>Name:</b> " + fb.get(FIELD_NAME) + "</p>");
            out.println("<p><b>Email:</b> " + fb.get(FIELD_EMAIL) + "</p>");
            out.println("<p><b>Category:</b> " + fb.get(FIELD_CATEGORY) + "</p>");
            out.println("<p><b>Feedback:</b> " + fb.get(FIELD_FEEDBACK) + "</p>");
            out.println("</div><hr>");
        }
    }
}
