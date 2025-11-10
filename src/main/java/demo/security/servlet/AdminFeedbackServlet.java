package demo.security.servlet;

import demo.security.util.FeedbackDAO;
import demo.security.util.FeedbackDAO.FeedbackRecord;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Admin servlet for managing feedback
 * WARNING: Contains intentional security vulnerabilities for demonstration!
 */
@WebServlet("/admin/feedback")
public class AdminFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // VULNERABILITY: No authentication/authorization check!
        // Anyone can access admin functionality
        
        String action = request.getParameter("action");
        String feedbackId = request.getParameter("id");
        String searchTerm = request.getParameter("search");
        String orderBy = request.getParameter("orderBy");
        String filterCondition = request.getParameter("filter");
        
        try {
            FeedbackDAO dao = new FeedbackDAO();
            
            // Generate admin panel HTML
            out.println("<html><head>");
            out.println("<title>Admin - Feedback Management</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f0f0f0; }");
            out.println(".container { background: white; padding: 20px; border-radius: 8px; }");
            out.println(".warning { background: #ffe6e6; padding: 15px; border-left: 4px solid #ff4444; margin-bottom: 20px; }");
            out.println(".feedback-item { background: #f9f9f9; margin: 10px 0; padding: 15px; border-left: 3px solid #007cba; }");
            out.println(".controls { margin: 20px 0; padding: 15px; background: #e6f3ff; border-radius: 4px; }");
            out.println("input, select { margin: 5px; padding: 5px; }");
            out.println("</style>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            out.println("<h1>🔧 Admin Panel - Feedback Management</h1>");
            
            // Warning notice
            out.println("<div class='warning'>");
            out.println("<strong>⚠️ Security Warning:</strong> This admin panel has no authentication! ");
            out.println("In a real application, this would be a serious security vulnerability.");
            out.println("</div>");
            
            // Admin controls
            out.println("<div class='controls'>");
            out.println("<h3>Search & Filter Controls</h3>");
            out.println("<form method='get'>");
            out.println("Search: <input type='text' name='search' value='" + (searchTerm != null ? searchTerm : "") + "'>");
            out.println("Order By: <select name='orderBy'>");
            out.println("<option value='submitted_at DESC'" + (isSelected(orderBy, "submitted_at DESC")) + ">Latest First</option>");
            out.println("<option value='submitted_at ASC'" + (isSelected(orderBy, "submitted_at ASC")) + ">Oldest First</option>");
            out.println("<option value='name'" + (isSelected(orderBy, "name")) + ">Name</option>");
            out.println("<option value='subject'" + (isSelected(orderBy, "subject")) + ">Subject</option>");
            out.println("</select>");
            out.println("<br>Advanced Filter: <input type='text' name='filter' value='" + (filterCondition != null ? filterCondition : "") + "' placeholder='SQL WHERE condition'>");
            out.println("<input type='submit' value='Search'>");
            out.println("</form>");
            out.println("</div>");
            
            if ("delete".equals(action) && feedbackId != null) {
                // VULNERABILITY: Missing CSRF protection!
                // VULNERABILITY: Weak authorization (hardcoded token)
                String adminToken = request.getParameter("token");
                if (adminToken == null) adminToken = "admin123"; // Default weak token!
                
                try {
                    boolean deleted = dao.deleteFeedback(feedbackId, adminToken);
                    if (deleted) {
                        out.println("<p style='color:green;'>✓ Feedback #" + feedbackId + " deleted successfully!</p>");
                    } else {
                        out.println("<p style='color:red;'>✗ Failed to delete feedback #" + feedbackId + "</p>");
                    }
                } catch (Exception e) {
                    out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
                }
            }
            
            // Display feedback based on parameters
            if (feedbackId != null && !"delete".equals(action)) {
                // VULNERABILITY: IDOR - Direct object reference without access control
                displaySingleFeedback(out, dao, feedbackId);
                
            } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // VULNERABILITY: SQL injection via search
                if (orderBy == null) orderBy = "submitted_at DESC";
                displaySearchResults(out, dao, searchTerm, orderBy);
                
            } else if (filterCondition != null && !filterCondition.trim().isEmpty()) {
                // VULNERABILITY: Advanced SQL injection via custom filter
                displayFilteredFeedback(out, dao, filterCondition);
                
            } else {
                // Display recent feedback by default
                displayRecentFeedback(out, dao);
            }
            
            out.println("</div>");
            out.println("</body></html>");
            
        } catch (Exception e) {
            // VULNERABILITY: Information disclosure via detailed error messages
            out.println("<h2 style='color:red;'>Database Error</h2>");
            out.println("<pre style='background:#ffe6e6;padding:15px;'>");
            out.println("Error Type: " + e.getClass().getSimpleName());
            out.println("Error Message: " + e.getMessage());
            if (e.getCause() != null) {
                out.println("Root Cause: " + e.getCause().getMessage());
            }
            out.println("</pre>");
        }
    }
    
    private void displaySingleFeedback(PrintWriter out, FeedbackDAO dao, String feedbackId) throws SQLException {
        FeedbackRecord feedback = dao.getFeedbackById(feedbackId);
        
        if (feedback != null) {
            out.println("<h2>Feedback Details #" + feedbackId + "</h2>");
            out.println("<div class='feedback-item'>");
            out.println("<p><strong>Name:</strong> " + feedback.getName() + "</p>");
            out.println("<p><strong>Email:</strong> " + feedback.getEmail() + "</p>");
            out.println("<p><strong>Subject:</strong> " + feedback.getSubject() + "</p>");
            out.println("<p><strong>Message:</strong></p>");
            out.println("<div style='background:white;padding:10px;border:1px solid #ddd;'>");
            out.println(feedback.getMessage()); // VULNERABILITY: Stored XSS - no HTML escaping!
            out.println("</div>");
            out.println("<p><small>Submitted: " + new Date(feedback.getSubmittedAt()) + "</small></p>");
            out.println("<p><small>Client IP: " + feedback.getClientIP() + "</small></p>");
            out.println("<p><small>User Agent: " + feedback.getUserAgent() + "</small></p>");
            
            // Admin actions (vulnerable to CSRF)
            out.println("<hr>");
            out.println("<p><strong>Admin Actions:</strong></p>");
            out.println("<a href='?action=delete&id=" + feedbackId + "' onclick='return confirm(\"Delete this feedback?\")' style='color:red;'>🗑️ Delete</a>");
            out.println("</div>");
        } else {
            out.println("<p>Feedback #" + feedbackId + " not found.</p>");
        }
    }
    
    private void displaySearchResults(PrintWriter out, FeedbackDAO dao, String searchTerm, String orderBy) throws SQLException {
        List<FeedbackRecord> results = dao.searchFeedback(searchTerm, orderBy);
        
        out.println("<h2>Search Results for: \"" + searchTerm + "\"</h2>");
        out.println("<p>Found " + results.size() + " result(s)</p>");
        
        for (FeedbackRecord feedback : results) {
            out.println("<div class='feedback-item'>");
            out.println("<h4>" + feedback.getSubject() + " <small>(#" + feedback.getId() + ")</small></h4>");
            out.println("<p><strong>From:</strong> " + feedback.getName() + " &lt;" + feedback.getEmail() + "&gt;</p>");
            out.println("<p>" + truncateMessage(feedback.getMessage()) + "</p>");
            out.println("<p><a href='?id=" + feedback.getId() + "'>View Details</a> | ");
            out.println("<a href='?action=delete&id=" + feedback.getId() + "' style='color:red;'>Delete</a></p>");
            out.println("</div>");
        }
    }
    
    private void displayFilteredFeedback(PrintWriter out, FeedbackDAO dao, String filterCondition) throws SQLException {
        List<FeedbackRecord> results = dao.getRecentFeedback(100, filterCondition);
        
        out.println("<h2>Filtered Results</h2>");
        out.println("<p><strong>Filter:</strong> <code>" + filterCondition + "</code></p>");
        out.println("<p>Found " + results.size() + " result(s)</p>");
        
        for (FeedbackRecord feedback : results) {
            out.println("<div class='feedback-item'>");
            out.println("<h4>" + feedback.getSubject() + " <small>(#" + feedback.getId() + ")</small></h4>");
            out.println("<p><strong>From:</strong> " + feedback.getName() + "</p>");
            out.println("<p><a href='?id=" + feedback.getId() + "'>View Details</a></p>");
            out.println("</div>");
        }
    }
    
    private void displayRecentFeedback(PrintWriter out, FeedbackDAO dao) throws SQLException {
        List<FeedbackRecord> recent = dao.getRecentFeedback(20, null);
        
        out.println("<h2>Recent Feedback (Latest 20)</h2>");
        
        if (recent.isEmpty()) {
            out.println("<p>No feedback submitted yet. <a href='../contact.jsp'>Submit the first one!</a></p>");
        } else {
            for (FeedbackRecord feedback : recent) {
                out.println("<div class='feedback-item'>");
                out.println("<h4>" + feedback.getSubject() + " <small>(#" + feedback.getId() + ")</small></h4>");
                out.println("<p><strong>From:</strong> " + feedback.getName() + " &lt;" + feedback.getEmail() + "&gt;</p>");
                out.println("<p>" + truncateMessage(feedback.getMessage()) + "</p>");
                out.println("<p><small>" + new Date(feedback.getSubmittedAt()) + "</small></p>");
                out.println("<p><a href='?id=" + feedback.getId() + "'>View Details</a> | ");
                out.println("<a href='?action=delete&id=" + feedback.getId() + "' style='color:red;'>Delete</a></p>");
                out.println("</div>");
            }
        }
    }
    
    private String truncateMessage(String message) {
        if (message == null) return "";
        if (message.length() <= 200) return message;
        return message.substring(0, 197) + "...";
    }
    
    private String isSelected(String current, String value) {
        return value.equals(current) ? " selected" : "";
    }
}
