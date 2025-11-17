package demo.security.servlet;

import demo.security.util.ContactFeedbackUtil;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ContactServlet.class.getName());
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String PRIORITY_NORMAL = "normal";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String storedCaptcha = (String) session.getAttribute("captcha");
        String userCaptcha = request.getParameter("captcha");
        
        response.setContentType(CONTENT_TYPE_HTML);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            logger.severe("Error getting response writer: " + e.getMessage());
            throw e;
        }
        
        // Optional CAPTCHA validation - vulnerable to bypass
        if (storedCaptcha != null && !storedCaptcha.isEmpty() && 
            (userCaptcha == null || !userCaptcha.equals(storedCaptcha))) {
            out.println("<html><body><h2>Invalid CAPTCHA!</h2>");
            out.println("<a href='contact.jsp'>Try again</a></body></html>");
            out.close();
            return;
        }
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String priority = request.getParameter("priority");
        
        // Vulnerable: Command injection through priority field
        String priorityLevel = evaluatePriority(priority);
        
        // Vulnerable: SQL injection when storing feedback
        try {
            ContactFeedbackUtil feedbackUtil = new ContactFeedbackUtil();
            feedbackUtil.storeFeedback(name, email, subject, message, priorityLevel);
            
            out.println("<html><body><h2>Thank you for your feedback!</h2>");
            out.println("<p>Your message has been received with priority: " + priorityLevel + "</p>");
            out.println("<a href='index.jsp'>Back to Home</a></body></html>");
        } catch (Exception e) {
            logger.severe("Error storing feedback: " + e.getMessage());
            out.println("<html><body><h2>Error processing your request</h2>");
            out.println("<p>Please try again later.</p></body></html>");
        }
        
        out.close();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vulnerable: Path traversal when viewing feedback files
        String feedbackId = request.getParameter("id");
        if (feedbackId != null) {
            try {
                ContactFeedbackUtil feedbackUtil = new ContactFeedbackUtil();
                String feedbackContent = feedbackUtil.readFeedbackFile(feedbackId);
                
                response.setContentType(CONTENT_TYPE_HTML);
                PrintWriter out = response.getWriter();
                out.println("<html><body><h2>Feedback Details</h2>");
                out.println("<pre>" + feedbackContent + "</pre>");
                out.println("</body></html>");
                out.close();
            } catch (IOException e) {
                logger.severe("Error reading feedback: " + e.getMessage());
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } catch (IOException ioException) {
                    logger.severe("Error sending error response: " + ioException.getMessage());
                    throw ioException;
                }
            } catch (Exception e) {
                logger.severe("Error reading feedback: " + e.getMessage());
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } catch (IOException ioException) {
                    logger.severe("Error sending error response: " + ioException.getMessage());
                    throw ioException;
                }
            }
        } else {
            // Vulnerable: Deserialization attack through custom header
            String feedbackData = request.getHeader("X-Feedback-Data");
            if (feedbackData != null) {
                try {
                    byte[] decoded = Base64.decodeBase64(feedbackData);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decoded));
                    Object feedback = ois.readObject();
                    ois.close();
                    
                    response.setContentType(CONTENT_TYPE_HTML);
                    PrintWriter out = response.getWriter();
                    out.println("<html><body><h2>Feedback received: " + feedback.toString() + "</h2></body></html>");
                    out.close();
                } catch (IOException e) {
                    logger.severe("Error deserializing feedback: " + e.getMessage());
                    try {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    } catch (IOException ioException) {
                        logger.severe("Error sending error response: " + ioException.getMessage());
                        throw ioException;
                    }
                } catch (Exception e) {
                    logger.severe("Error deserializing feedback: " + e.getMessage());
                    try {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    } catch (IOException ioException) {
                        logger.severe("Error sending error response: " + ioException.getMessage());
                        throw ioException;
                    }
                }
            } else {
                try {
                    response.sendRedirect("contact.jsp");
                } catch (IOException e) {
                    logger.severe("Error redirecting: " + e.getMessage());
                    throw e;
                }
            }
        }
    }
    
    // Vulnerable: Command injection
    private String evaluatePriority(String priority) {
        if (priority == null || priority.isEmpty()) {
            return PRIORITY_NORMAL;
        }
        
        try {
            // Vulnerable: OS command injection
            String command = "echo " + priority;
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            process.waitFor();
            return priority;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Priority evaluation interrupted: " + e.getMessage());
            return PRIORITY_NORMAL;
        } catch (IOException e) {
            logger.severe("Error evaluating priority: " + e.getMessage());
            return PRIORITY_NORMAL;
        }
    }
}

