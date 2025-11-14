package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.FeedbackUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/contact")
public class ContactFeedbackServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String feedbackId = request.getParameter("id");
        String attachmentPath = request.getParameter("attachment");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            DBUtils db = new DBUtils();
            
            // Display specific feedback if ID provided - SQL Injection vulnerability
            if (feedbackId != null) {
                List<String> feedbacks = db.findFeedback(feedbackId);
                out.println("<h2>Feedback Details</h2>");
                feedbacks.forEach(feedback ->
                    // XSS vulnerability - unsanitized output
                    out.print("<div class='feedback'>" + feedback + "</div>")
                );
            }
            
            // Path Traversal vulnerability - handle attachment download
            if (attachmentPath != null) {
                FeedbackUtils.readAttachment(attachmentPath);
                out.println("<p>Attachment processed: " + attachmentPath + "</p>");
            }
            
            // Display all feedbacks
            List<String> allFeedbacks = db.getAllFeedbacks();
            out.println("<h2>All Feedback</h2>");
            allFeedbacks.forEach(feedback ->
                out.print("<p>" + feedback + "</p>")
            );
            
            out.close();
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String category = request.getParameter("category");
        String attachment = request.getParameter("attachment");
        
        // Insecure deserialization vulnerability
        String feedbackData = request.getHeader("Feedback-Data");
        if (feedbackData != null) {
            try {
                byte[] decoded = Base64.decodeBase64(feedbackData);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded));
                in.readObject();
                in.close();
            } catch (Exception e) {
                // Ignore deserialization errors
            }
        }
        
        try {
            DBUtils db = new DBUtils();
            
            // SQL Injection vulnerability - store feedback
            db.storeFeedback(name, email, message, category);
            
            // Command injection vulnerability - process category metadata
            if (category != null) {
                FeedbackUtils.processCategory(category);
            }
            
            // Weak cryptography - encrypt sensitive feedback
            if (message != null && message.contains("confidential")) {
                FeedbackUtils.encryptFeedback(message);
            }
            
            // Path traversal vulnerability - store attachment
            if (attachment != null) {
                FeedbackUtils.storeAttachment(attachment);
            }
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Name: " + name + "</p>");
            out.println("<p>Email: " + email + "</p>");
            out.close();
            
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        } catch (Exception e) {
            throw new ServletException("Processing error", e);
        }
    }
}

