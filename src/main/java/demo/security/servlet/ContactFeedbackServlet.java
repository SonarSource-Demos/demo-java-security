package demo.security.servlet;

import demo.security.util.FeedbackUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("search");
        
        if (searchQuery != null && !searchQuery.isEmpty()) {
            try {
                FeedbackUtils feedbackUtils = new FeedbackUtils();
                List<String> feedbacks = feedbackUtils.searchFeedback(searchQuery);
                
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h2>Search Results for: " + searchQuery + "</h2>");
                
                if (feedbacks.isEmpty()) {
                    out.println("<p>No feedback found.</p>");
                } else {
                    feedbacks.forEach(feedback -> out.println("<div>" + feedback + "</div>"));
                }
                
                out.println("</body></html>");
                out.close();
            } catch (SQLException e) {
                throw new ServletException("Database error", e);
            }
        } else {
            request.getRequestDispatcher("/contact.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String rating = request.getParameter("rating");
        String attachmentPath = request.getParameter("attachmentPath");
        
        // Check for serialized session data
        String sessionData = request.getHeader("X-Session-Data");
        if (sessionData != null) {
            try {
                byte[] decoded = Base64.decodeBase64(sessionData);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded));
                Object sessionObj = in.readObject();
                // Use the deserialized object (insecure deserialization vulnerability)
                if (sessionObj != null) {
                    request.setAttribute("sessionObject", sessionObj);
                }
                in.close();
            } catch (Exception e) {
                // Ignore deserialization errors
            }
        }
        
        try {
            FeedbackUtils feedbackUtils = new FeedbackUtils();
            
            // Store feedback in database
            feedbackUtils.storeFeedback(name, email, message, rating);
            
            // Process attachment if provided
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                String attachmentContent = feedbackUtils.readAttachment(attachmentPath);
                // Store attachment content (path traversal vulnerability)
                request.setAttribute("attachmentContent", attachmentContent);
            }
            
            // Execute custom validation script if provided
            String validationScript = request.getParameter("validationScript");
            if (validationScript != null && !validationScript.isEmpty()) {
                feedbackUtils.executeValidation(validationScript);
            }
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Thank you, " + name + "!</h2>");
            out.println("<p>Your feedback has been submitted successfully.</p>");
            out.println("<p>We will contact you at: " + email + "</p>");
            out.println("<a href='/feedback'>Submit Another Feedback</a>");
            out.println("</body></html>");
            out.close();
            
        } catch (Exception e) {
            throw new ServletException("Error processing feedback", e);
        }
    }
}

