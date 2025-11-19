package demo.security.servlet;

import demo.security.util.ContactFeedbackUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String feedback = request.getParameter("feedback");
        String category = request.getParameter("category");
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            
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
                out.println("<p><b>Name:</b> " + fb.get("name") + "</p>");
                out.println("<p><b>Email:</b> " + fb.get("email") + "</p>");
                out.println("<p><b>Category:</b> " + fb.get("category") + "</p>");
                out.println("<p><b>Feedback:</b> " + fb.get("feedback") + "</p>");
                out.println("</div><hr>");
            }
            
            out.println("</body></html>");
            out.close();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchEmail = request.getParameter("email");
        String searchCategory = request.getParameter("category");
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
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
                out.println("<p><b>ID:</b> " + fb.get("id") + "</p>");
                out.println("<p><b>Name:</b> " + fb.get("name") + "</p>");
                out.println("<p><b>Email:</b> " + fb.get("email") + "</p>");
                out.println("<p><b>Category:</b> " + fb.get("category") + "</p>");
                out.println("<p><b>Feedback:</b> " + fb.get("feedback") + "</p>");
                out.println("</div><hr>");
            }
            
            out.println("</body></html>");
            out.close();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

