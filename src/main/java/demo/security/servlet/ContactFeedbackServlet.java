package demo.security.servlet;

import demo.security.util.ContactFeedbackUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String feedbackFile = request.getParameter("feedbackFile");
        String exportFormat = request.getParameter("exportFormat");
        
        response.setContentType("text/html");
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new ServletException("Unable to get response writer", e);
        }
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            
            // XSS vulnerability - reflecting user input without sanitization
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Name: " + name + "</p>");
            out.println("<p>Email: " + email + "</p>");
            out.println("<p>Message: " + message + "</p>");
            
            // SQL Injection vulnerability - storing feedback
            if (name != null && email != null && message != null) {
                util.storeFeedback(name, email, message);
            }
            
            // Path Traversal vulnerability - reading feedback file
            if (feedbackFile != null && !feedbackFile.isEmpty()) {
                String feedbackContent = util.readFeedbackFile(feedbackFile);
                out.println("<h3>Previous Feedback:</h3>");
                out.println("<pre>" + feedbackContent + "</pre>");
            }
            
            // Command Injection vulnerability - exporting feedback
            if (exportFormat != null && !exportFormat.isEmpty()) {
                String exportResult = util.exportFeedback(email, exportFormat);
                out.println("<p>Export completed: " + exportResult + "</p>");
            }
            
            // Weak cryptography - encrypting sensitive data
            if (email != null) {
                String encryptedEmail = util.encryptEmail(email);
                out.println("<p>Encrypted email reference: " + encryptedEmail + "</p>");
            }
            
            out.println("</body></html>");
            out.close();
            
        } catch (Exception e) {
            // Information disclosure - exposing stack trace
            out.println("<h3>Error occurred:</h3>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.sendRedirect("contact-feedback.jsp");
        } catch (IOException e) {
            throw new ServletException("Unable to redirect", e);
        }
    }
}

