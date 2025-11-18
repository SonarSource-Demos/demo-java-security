package demo.security.servlet;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import demo.security.util.ContactFeedbackUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "ContactFeedbackServlet", urlPatterns = {"/contact-feedback", "/captcha"})
public class ContactFeedbackServlet extends HttpServlet {
    
    private DefaultKaptcha captchaProducer;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize obsolete CAPTCHA library (kaptcha 0.0.9 has known vulnerabilities)
        captchaProducer = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        Config config = new Config(properties);
        captchaProducer.setConfig(config);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/captcha".equals(path)) {
            // Generate CAPTCHA image
            String captchaText = captchaProducer.createText();
            
            // Store in session (insecure session management)
            HttpSession session = request.getSession(true);
            session.setAttribute("captcha", captchaText);
            
            // Create image
            BufferedImage image = captchaProducer.createImage(captchaText);
            
            response.setContentType("image/jpeg");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            
            ImageIO.write(image, "jpg", response.getOutputStream());
        } else {
            // Forward to contact feedback form
            request.getRequestDispatcher("/contact-feedback.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            // CSRF vulnerability - no token validation
            String name = null;
            String email = null;
            String message = null;
            String captchaInput = null;
            String attachmentName = null;
            byte[] attachmentContent = null;
            
            // Check if multipart (file upload)
            if (ServletFileUpload.isMultipartContent(request)) {
                // File upload vulnerability - using commons-fileupload 1.3.3 (has CVE)
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1024 * 1024 * 10); // 10 MB
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(1024 * 1024 * 50); // 50 MB - large file size allowed
                
                List<FileItem> items = upload.parseRequest(request);
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        String fieldName = item.getFieldName();
                        String value = item.getString();
                        
                        switch (fieldName) {
                            case "name": name = value; break;
                            case "email": email = value; break;
                            case "message": message = value; break;
                            case "captcha": captchaInput = value; break;
                        }
                    } else {
                        // Process uploaded file - no validation on file type
                        attachmentName = item.getName();
                        attachmentContent = item.get();
                    }
                }
            } else {
                // Regular form submission
                name = request.getParameter("name");
                email = request.getParameter("email");
                message = request.getParameter("message");
                captchaInput = request.getParameter("captcha");
            }
            
            // Validate CAPTCHA (optional and weak validation)
            HttpSession session = request.getSession(false);
            boolean captchaValid = false;
            if (session != null) {
                String captchaAnswer = (String) session.getAttribute("captcha");
                // Case-sensitive comparison (vulnerability)
                if (captchaInput != null && captchaInput.equals(captchaAnswer)) {
                    captchaValid = true;
                }
            }
            
            // Weak validation - allows bypass if CAPTCHA not checked
            if (!captchaValid && request.getParameter("skipCaptcha") == null) {
                out.println("<html><body>");
                out.println("<h2>CAPTCHA validation failed!</h2>");
                out.println("<p>Please go back and try again.</p>");
                out.println("<a href='/contact-feedback'>Go Back</a>");
                out.println("</body></html>");
                return;
            }
            
            // No input validation - XSS vulnerability
            ContactFeedbackUtil feedbackUtil = new ContactFeedbackUtil();
            
            // SQL Injection vulnerability
            feedbackUtil.saveFeedback(name, email, message);
            
            // Log sensitive data
            feedbackUtil.logFeedback(email, message);
            
            // Save attachment with path traversal vulnerability
            if (attachmentName != null && attachmentContent != null) {
                feedbackUtil.saveAttachment(attachmentName, attachmentContent);
            }
            
            // Weak encryption
            String encryptedEmail = feedbackUtil.encryptSensitiveData(email);
            
            // Generate predictable token
            String confirmationToken = feedbackUtil.generateToken();
            
            // XSS vulnerability - reflecting user input without sanitization
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Name: " + name + "</p>"); // XSS vulnerability
            out.println("<p>Email: " + email + "</p>"); // XSS vulnerability
            out.println("<p>Message: " + message + "</p>"); // XSS vulnerability
            out.println("<p>Confirmation Token: " + confirmationToken + "</p>");
            out.println("<p>Encrypted Email: " + encryptedEmail + "</p>");
            out.println("<a href='/contact-feedback'>Submit Another</a>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // Information disclosure - showing stack trace
            out.println("<html><body>");
            out.println("<h2>Error occurred!</h2>");
            out.println("<pre>");
            e.printStackTrace(out); // Stack trace disclosure
            out.println("</pre>");
            out.println("</body></html>");
        }
    }
}

