package demo.security.servlet;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import demo.security.util.ContactFeedbackUtil;
import demo.security.util.WebUtils;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

@WebServlet("/contact-feedback")
@MultipartConfig
public class ContactFeedbackServlet extends HttpServlet {

    private DefaultKaptcha captchaProducer;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize CAPTCHA with obsolete library
        captchaProducer = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "black");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "50");
        Config config = new Config(properties);
        captchaProducer.setConfig(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("search".equals(action)) {
            handleSearch(request, response);
        } else if ("export".equals(action)) {
            handleExport(request, response);
        } else if ("template".equals(action)) {
            handleTemplate(request, response);
        } else if ("delete".equals(action)) {
            handleDelete(request, response);
        } else if ("captcha".equals(action)) {
            handleCaptcha(request, response);
        } else {
            displayFeedbackForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("submit".equals(action)) {
            handleSubmit(request, response);
        } else if ("upload".equals(action)) {
            handleUpload(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    /**
     * Display the feedback form
     */
    private void displayFeedbackForm(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // XSS vulnerability - directly outputting user input
        String userName = request.getParameter("name");
        String message = request.getParameter("prefill");
        
        out.println("<html><head><title>Contact Feedback</title></head><body>");
        out.println("<h1>Contact Feedback Form</h1>");
        
        if (userName != null) {
            // XSS vulnerability
            out.println("<p>Welcome back, " + userName + "!</p>");
        }
        
        out.println("<form method='post' action='/contact-feedback?action=submit'>");
        out.println("<label>Name: <input type='text' name='name' ");
        if (userName != null) {
            // XSS vulnerability
            out.println("value='" + userName + "'");
        }
        out.println("/></label><br>");
        out.println("<label>Email: <input type='email' name='email' required/></label><br>");
        out.println("<label>Category: <input type='text' name='category'/></label><br>");
        out.println("<label>Message: <textarea name='message' required>");
        if (message != null) {
            // XSS vulnerability
            out.print(message);
        }
        out.println("</textarea></label><br>");
        out.println("<label>CAPTCHA: <input type='text' name='captcha' required/></label><br>");
        out.println("<img src='/contact-feedback?action=captcha' alt='CAPTCHA'/><br>");
        out.println("<input type='submit' value='Submit Feedback'/>");
        out.println("</form>");
        out.println("<br><a href='/contact-feedback?action=search'>Search Feedback</a>");
        out.println("</body></html>");
        out.close();
    }

    /**
     * Handle feedback submission with CSRF vulnerability
     */
    private void handleSubmit(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // CSRF vulnerability - no token validation
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String category = request.getParameter("category");
        String captcha = request.getParameter("captcha");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            // Optional CAPTCHA validation (can be bypassed)
            HttpSession session = request.getSession(false);
            if (session != null) {
                String expectedCaptcha = (String) session.getAttribute("captcha");
                if (expectedCaptcha != null && !expectedCaptcha.equals(captcha)) {
                    // Weak validation - can be bypassed by not including CAPTCHA parameter
                    out.println("<html><body><h2>Invalid CAPTCHA</h2></body></html>");
                    return;
                }
            }
            
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            
            // Validate email with ReDoS vulnerability
            if (!util.validateEmail(email)) {
                out.println("<html><body><h2>Invalid email format</h2></body></html>");
                return;
            }
            
            // Save feedback with SQL injection vulnerability
            util.saveFeedback(name, email, message, category);
            
            // Set insecure cookie
            WebUtils webUtils = new WebUtils();
            webUtils.addCookie(response, "last_feedback_user", name);
            
            // Session fixation vulnerability
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("user", name);
            
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback, " + name + "!</h2>");
            out.println("<p>We have received your message: " + message + "</p>");
            out.println("<a href='/contact-feedback'>Submit Another</a>");
            out.println("</body></html>");
            
            util.close();
        } catch (Exception e) {
            // Information disclosure - exposing exception details
            out.println("<html><body><h2>Error</h2><pre>" + e.getMessage() + "</pre></body></html>");
        }
        
        out.close();
    }

    /**
     * Handle search with SQL injection vulnerability
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String searchTerm = request.getParameter("q");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Search Feedback</title></head><body>");
        out.println("<h1>Search Feedback</h1>");
        out.println("<form method='get' action='/contact-feedback'>");
        out.println("<input type='hidden' name='action' value='search'/>");
        out.println("<input type='text' name='q' placeholder='Search...' ");
        if (searchTerm != null) {
            // XSS vulnerability
            out.print("value='" + searchTerm + "'");
        }
        out.println("/>");
        out.println("<input type='submit' value='Search'/>");
        out.println("</form>");
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            try {
                ContactFeedbackUtil util = new ContactFeedbackUtil();
                List<ContactFeedbackUtil.Feedback> feedbacks = util.searchFeedback(searchTerm);
                
                out.println("<h2>Search Results for: " + searchTerm + "</h2>");
                for (ContactFeedbackUtil.Feedback feedback : feedbacks) {
                    out.println("<div>");
                    out.println("<h3>" + feedback.getName() + "</h3>");
                    out.println("<p>" + feedback.getMessage() + "</p>");
                    out.println("<a href='/contact-feedback?action=delete&id=" + feedback.getId() + "'>Delete</a>");
                    out.println("</div><hr>");
                }
                
                util.close();
            } catch (Exception e) {
                out.println("<p>Error: " + e.getMessage() + "</p>");
            }
        }
        
        out.println("</body></html>");
        out.close();
    }

    /**
     * Handle export with command injection vulnerability
     */
    private void handleExport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String feedbackId = request.getParameter("id");
        String format = request.getParameter("format");
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            String exportData = util.exportFeedbackToFile(feedbackId, format);
            out.println(exportData);
            util.close();
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
        
        out.close();
    }

    /**
     * Handle template reading with path traversal vulnerability
     */
    private void handleTemplate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String templateName = request.getParameter("name");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            String template = util.readFeedbackTemplate(templateName);
            out.println(template);
            util.close();
        } catch (Exception e) {
            out.println("Error reading template: " + e.getMessage());
        }
        
        out.close();
    }

    /**
     * Handle delete with SQL injection and CSRF vulnerability
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // CSRF vulnerability - no token validation for destructive operation
        String feedbackId = request.getParameter("id");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            String result = util.deleteFeedback(feedbackId);
            out.println("<html><body><h2>" + result + "</h2></body></html>");
            util.close();
        } catch (Exception e) {
            out.println("<html><body><h2>Error: " + e.getMessage() + "</h2></body></html>");
        }
        
        out.close();
    }

    /**
     * Generate CAPTCHA image
     */
    private void handleCaptcha(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // Generate CAPTCHA using obsolete library
        ContactFeedbackUtil util;
        try {
            util = new ContactFeedbackUtil();
            String captchaCode = util.generateCaptchaCode();
            
            // Store CAPTCHA in session
            HttpSession session = request.getSession(true);
            session.setAttribute("captcha", captchaCode);
            
            // Generate image
            String captchaText = captchaProducer.createText();
            session.setAttribute("captcha", captchaText);
            
            response.setContentType("image/jpeg");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            
            // In a real implementation, would generate and output CAPTCHA image
            response.getOutputStream().write(new byte[0]);
            
            util.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Handle file upload with multiple vulnerabilities
     */
    private void handleUpload(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            String uploadedPath = util.uploadAttachment(request);
            
            out.println("<html><body>");
            out.println("<h2>File uploaded successfully</h2>");
            out.println("<p>Path: " + uploadedPath + "</p>");
            out.println("</body></html>");
            
            util.close();
        } catch (Exception e) {
            out.println("<html><body><h2>Upload failed: " + e.getMessage() + "</h2></body></html>");
        }
        
        out.close();
    }
}

