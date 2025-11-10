package demo.security.util;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Email utility class for sending feedback notifications
 * WARNING: This class contains intentional security vulnerabilities for demonstration purposes!
 */
public class EmailUtil {
    
    private static final Logger logger = Logger.getLogger(EmailUtil.class.getName());
    private static final String SMTP_HOST = "localhost";
    private static final int SMTP_PORT = 25;
    private static final String FROM_EMAIL = "noreply@security-demo.com";
    private static final String ADMIN_EMAIL = "admin@security-demo.com";
    
    /**
     * Sends feedback notification email
     * VULNERABILITY: Email Header Injection - user input directly inserted into headers
     */
    public boolean sendFeedbackNotification(String name, String email, String subject, String message) {
        try {
            // Create email content with user input (Header Injection vulnerability!)
            StringBuilder emailContent = new StringBuilder();
            
            // Email headers - VULNERABLE to header injection!
            emailContent.append("From: ").append(FROM_EMAIL).append("\r\n");
            emailContent.append("To: ").append(ADMIN_EMAIL).append("\r\n");
            emailContent.append("Reply-To: ").append(email).append("\r\n"); // VULNERABLE - no validation!
            emailContent.append("Subject: New Feedback: ").append(subject).append("\r\n"); // VULNERABLE!
            emailContent.append("Date: ").append(new Date().toString()).append("\r\n");
            emailContent.append("Content-Type: text/html; charset=UTF-8\r\n");
            emailContent.append("\r\n"); // Empty line to separate headers from body
            
            // Email body with HTML content
            emailContent.append("<html><body>");
            emailContent.append("<h2>New Feedback Received</h2>");
            emailContent.append("<p><strong>From:</strong> ").append(name).append(" &lt;").append(email).append("&gt;</p>");
            emailContent.append("<p><strong>Subject:</strong> ").append(escapeHtml(subject)).append("</p>");
            emailContent.append("<p><strong>Message:</strong></p>");
            emailContent.append("<div style='background:#f9f9f9;padding:15px;border-left:3px solid #007cba;'>");
            emailContent.append(message.replace("\n", "<br>"));  // Basic newline conversion, but no XSS protection
            emailContent.append("</div>");
            emailContent.append("<hr>");
            emailContent.append("<p><small>This is an automated message from the Security Demo application.</small></p>");
            emailContent.append("</body></html>");
            
            // Simulate sending email via SMTP (for demo - actual connection will fail)
            return sendViaSmtp(emailContent.toString());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send email notification: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Simulates SMTP email sending
     * In a real application, this would connect to an actual SMTP server
     */
    private boolean sendViaSmtp(String emailContent) {
        try {
            // Log the email content (in real app, this would send to SMTP server)
            logger.info("=== EMAIL NOTIFICATION (would be sent via SMTP) ===");
            logger.info(emailContent);
            logger.info("=== END EMAIL ===");
            
            // Simulate network delay
            Thread.sleep(100);
            
            // For demonstration purposes, we'll return true (email "sent")
            // In reality, this would attempt SMTP connection to localhost:25
            return true;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "SMTP simulation failed: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Sends a confirmation email to the user (additional vulnerability demo)
     * VULNERABILITY: Allows arbitrary email content injection
     */
    public boolean sendUserConfirmation(String toEmail, String userName, String subject, String customMessage) {
        try {
            StringBuilder emailContent = new StringBuilder();
            
            // More header injection vulnerabilities
            emailContent.append("From: ").append(FROM_EMAIL).append("\r\n");
            emailContent.append("To: ").append(toEmail).append("\r\n"); // No validation on email format!
            emailContent.append("Subject: ").append(subject).append("\r\n"); // Direct injection possible!
            emailContent.append("Content-Type: text/html\r\n\r\n");
            
            // Email body
            emailContent.append("<h2>Thank you for your feedback, ").append(userName).append("!</h2>");
            emailContent.append("<p>").append(customMessage).append("</p>"); // XSS in email content!
            
            return sendViaSmtp(emailContent.toString());
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Basic HTML escaping (incomplete protection)
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("&", "&amp;")
                   .replace("\"", "&quot;");
        // NOTE: This is incomplete - missing single quotes and other characters
    }
    
    /**
     * Vulnerable email validation (demo of insufficient validation)
     */
    public boolean isValidEmail(String email) {
        // VULNERABILITY: Very basic validation that can be bypassed
        return email != null && email.contains("@") && email.contains(".");
        // Missing proper RFC 5322 validation, allows malicious inputs
    }
}
