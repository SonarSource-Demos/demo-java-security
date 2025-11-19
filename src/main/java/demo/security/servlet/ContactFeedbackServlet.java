package demo.security.servlet;

import demo.security.util.ContactFeedbackUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // XSS vulnerability - user input reflected without sanitization
        String message = request.getParameter("message");
        String searchEmail = request.getParameter("search");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body>");
        
        if (message != null) {
            // XSS vulnerability - directly outputting user input
            out.println("<h2>Your message: " + message + "</h2>");
        }
        
        if (searchEmail != null) {
            try {
                ContactFeedbackUtil util = new ContactFeedbackUtil();
                // SQL Injection through search parameter
                List<String> feedbacks = util.getFeedbackByEmail(searchEmail);
                out.println("<h3>Feedback for: " + searchEmail + "</h3>");
                for (String feedback : feedbacks) {
                    // XSS vulnerability - feedback content not sanitized
                    out.println("<p>" + feedback + "</p>");
                }
                util.closeConnection();
            } catch (Exception e) {
                // Information disclosure - showing stack trace
                out.println("<pre>" + e.getMessage() + "</pre>");
            }
        }
        
        out.println("</body></html>");
        out.close();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String feedbackMessage = request.getParameter("feedback");
        String rating = request.getParameter("rating");
        String fileOperation = request.getParameter("fileOp");
        String filename = request.getParameter("filename");
        String exportFormat = request.getParameter("export");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
            ContactFeedbackUtil util = new ContactFeedbackUtil();
            
            // Save feedback with SQL injection vulnerability
            if (name != null && email != null && feedbackMessage != null) {
                util.saveFeedback(name, email, feedbackMessage, rating);
                
                // Generate insecure token
                String token = util.generateFeedbackToken();
                
                // Insecure cookie - no HttpOnly, no Secure flags
                Cookie cookie = new Cookie("feedback_token", token);
                response.addCookie(cookie);
                
                // XSS vulnerability - reflecting user input
                out.println("<html><body>");
                out.println("<h2>Thank you, " + name + "!</h2>");
                out.println("<p>Your feedback has been saved: " + feedbackMessage + "</p>");
                out.println("</body></html>");
            }
            
            // Path Traversal vulnerability through file operations
            if ("read".equals(fileOperation) && filename != null) {
                String content = util.readFeedbackFile(filename);
                out.println("<pre>" + content + "</pre>");
            }
            
            if ("write".equals(fileOperation) && filename != null && feedbackMessage != null) {
                util.saveFeedbackToFile(filename, feedbackMessage);
                out.println("<p>Feedback saved to file: " + filename + "</p>");
            }
            
            // Command Injection vulnerability
            if (exportFormat != null) {
                String result = util.exportFeedbackToFile(exportFormat);
                out.println("<p>Export result: " + result + "</p>");
            }
            
            // LDAP Injection vulnerability
            if (email != null) {
                String ldapQuery = util.searchUserByEmail(email);
                out.println("<!-- LDAP Query: " + ldapQuery + " -->");
            }
            
            // XPath Injection vulnerability
            if (rating != null) {
                String xpathQuery = util.searchFeedbackByRating(rating);
                out.println("<!-- XPath Query: " + xpathQuery + " -->");
            }
            
            util.closeConnection();
            
        } catch (Exception e) {
            // Information disclosure - exposing exception details
            out.println("<html><body>");
            out.println("<h2>Error processing feedback</h2>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
            out.println("</body></html>");
        }
        
        out.close();
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        // Missing authentication check - security vulnerability
        try {
            doPost(request, response);
        } catch (ServletException | IOException e) {
            // Information disclosure - logging exception details
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        // Missing authentication check - security vulnerability
        String email = request.getParameter("email");
        
        if (email != null) {
            try {
                ContactFeedbackUtil util = new ContactFeedbackUtil();
                // SQL Injection vulnerability
                util.getFeedbackByEmail(email);
                util.closeConnection();
                
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                out.println("Feedback deleted for: " + email);
                out.close();
            } catch (Exception e) {
                // Information disclosure - logging exception details
                e.printStackTrace();
            }
        }
    }
}
