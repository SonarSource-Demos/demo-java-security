package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/contactFeedback")
public class ContactFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/contact-feedback.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String captcha = request.getParameter("captcha");
        String useCaptcha = request.getParameter("useCaptcha");
        
        HttpSession session = request.getSession();
        String expectedCaptcha = (String) session.getAttribute("KAPTCHA_SESSION_KEY");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Captcha validation vulnerability - weak validation
        if ("true".equals(useCaptcha)) {
            if (expectedCaptcha == null || captcha == null) {
                out.print("<h2>Error: Invalid CAPTCHA</h2>");
                out.print("<a href='contactFeedback'>Go back</a>");
                out.close();
                return;
            }
            // Vulnerability: Case-sensitive comparison, no timing-safe comparison
            if (!captcha.equals(expectedCaptcha)) {
                out.print("<h2>Error: CAPTCHA mismatch</h2>");
                out.print("<a href='contactFeedback'>Go back</a>");
                out.close();
                return;
            }
        }
        
        // XSS vulnerability - reflected output without sanitization
        out.print("<html><body>");
        out.print("<h2>Thank you for your feedback, " + name + "!</h2>");
        out.print("<p>We received your message about: " + subject + "</p>");
        out.print("<p>Your message: " + message + "</p>");
        
        // SQL Injection vulnerability - storing feedback in database
        try {
            DBUtils db = new DBUtils();
            db.saveFeedback(name, email, subject, message);
            out.print("<p>Your feedback has been stored successfully!</p>");
        } catch (SQLException e) {
            // Information disclosure vulnerability - exposing stack trace
            out.print("<h3>Database Error:</h3>");
            out.print("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(out);
        }
        
        // Check for session header with deserialization vulnerability
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader != null) {
            out.print("<p>Session user: " + sessionHeader.getUsername() + "</p>");
        }
        
        out.print("</body></html>");
        out.close();
    }
    
    // Deserialization vulnerability - untrusted data
    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth != null) {
            try {
                byte[] decoded = Base64.decodeBase64(sessionAuth);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded));
                return (SessionHeader) in.readObject();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

