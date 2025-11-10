package demo.security.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.security.util.FeedbackDAO;
import demo.security.util.FeedbackDAO.FeedbackRecord;
import demo.security.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API endpoints for feedback management
 * WARNING: Contains intentional security vulnerabilities for demonstration!
 */
@WebServlet("/api/feedback/*")
public class FeedbackApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // VULNERABILITY: No CORS protection - allows requests from any origin
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-API-Key, X-Requested-With");
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/feedback/ - List all feedback
                handleListFeedback(request, response, out);
                
            } else if (pathInfo.startsWith("/search/")) {
                // GET /api/feedback/search/{term} - Search feedback
                String searchTerm = pathInfo.substring(8); // Remove "/search/"
                handleSearchFeedback(searchTerm, request, response, out);
                
            } else if (pathInfo.matches("^/\\d+$")) {
                // GET /api/feedback/{id} - Get specific feedback
                String feedbackId = pathInfo.substring(1); // Remove leading "/"
                handleGetFeedback(feedbackId, request, response, out);
                
            } else if (pathInfo.equals("/stats")) {
                // GET /api/feedback/stats - Get feedback statistics (admin only?)
                handleGetStats(request, response, out);
                
            } else {
                sendErrorResponse(response, out, 404, "API endpoint not found");
            }
            
        } catch (Exception e) {
            // VULNERABILITY: Information disclosure via detailed error messages
            sendErrorResponse(response, out, 500, "Internal Server Error: " + e.getMessage() + 
                            " | Stack trace: " + getStackTrace(e));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/feedback/ - Create new feedback
                handleCreateFeedback(request, response, out);
                
            } else if (pathInfo.equals("/bulk")) {
                // POST /api/feedback/bulk - Bulk create feedback (admin feature)
                handleBulkCreateFeedback(request, response, out);
                
            } else {
                sendErrorResponse(response, out, 404, "API endpoint not found");
            }
            
        } catch (Exception e) {
            sendErrorResponse(response, out, 500, "Error processing request: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.matches("^/\\d+$")) {
                // PUT /api/feedback/{id} - Update feedback
                String feedbackId = pathInfo.substring(1);
                handleUpdateFeedback(feedbackId, request, response, out);
            } else {
                sendErrorResponse(response, out, 404, "API endpoint not found");
            }
            
        } catch (Exception e) {
            sendErrorResponse(response, out, 500, "Error updating feedback: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.matches("^/\\d+$")) {
                // DELETE /api/feedback/{id} - Delete feedback
                String feedbackId = pathInfo.substring(1);
                handleDeleteFeedback(feedbackId, request, response, out);
            } else {
                sendErrorResponse(response, out, 404, "API endpoint not found");
            }
            
        } catch (Exception e) {
            sendErrorResponse(response, out, 500, "Error deleting feedback: " + e.getMessage());
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle preflight requests (CORS)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-API-Key, X-Requested-With");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void handleListFeedback(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws SQLException, IOException {
        
        // VULNERABILITY: No rate limiting or pagination limits
        String limitStr = request.getParameter("limit");
        String filter = request.getParameter("filter");
        
        int limit = 100; // Default limit
        if (limitStr != null) {
            try {
                limit = Integer.parseInt(limitStr);
                // VULNERABILITY: No upper bound check - could cause DoS with very large limits
            } catch (NumberFormatException ignored) {}
        }
        
        FeedbackDAO dao = new FeedbackDAO();
        
        // VULNERABILITY: Direct use of user input in SQL query
        String filterCondition = filter != null ? filter : "1=1";
        List<FeedbackRecord> feedback = dao.getRecentFeedback(limit, filterCondition);
        
        Map<String, Object> response_data = new HashMap<>();
        response_data.put("status", "success");
        response_data.put("count", feedback.size());
        response_data.put("data", feedback);
        
        // VULNERABILITY: Information leakage - exposing internal IDs, IP addresses, etc.
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void handleSearchFeedback(String searchTerm, HttpServletRequest request, 
                                    HttpServletResponse response, PrintWriter out) throws SQLException, IOException {
        
        // VULNERABILITY: No input validation or sanitization
        String orderBy = request.getParameter("orderBy");
        if (orderBy == null) orderBy = "submitted_at DESC";
        
        FeedbackDAO dao = new FeedbackDAO();
        
        // VULNERABILITY: SQL injection via search term and orderBy
        List<FeedbackRecord> results = dao.searchFeedback(searchTerm, orderBy);
        
        Map<String, Object> response_data = new HashMap<>();
        response_data.put("status", "success");
        response_data.put("searchTerm", searchTerm); // Reflecting user input without escaping
        response_data.put("count", results.size());
        response_data.put("results", results);
        
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void handleGetFeedback(String feedbackId, HttpServletRequest request, 
                                 HttpServletResponse response, PrintWriter out) throws SQLException, IOException {
        
        // VULNERABILITY: IDOR - No access control, anyone can access any feedback by ID
        FeedbackDAO dao = new FeedbackDAO();
        FeedbackRecord feedback = dao.getFeedbackById(feedbackId);
        
        if (feedback != null) {
            Map<String, Object> response_data = new HashMap<>();
            response_data.put("status", "success");
            response_data.put("data", feedback);
            out.print(objectMapper.writeValueAsString(response_data));
        } else {
            sendErrorResponse(response, out, 404, "Feedback not found with ID: " + feedbackId);
        }
    }
    
    private void handleCreateFeedback(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws SQLException, IOException {
        
        // Read JSON request body
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        
        // VULNERABILITY: No input validation before JSON parsing
        // VULNERABILITY: Jackson deserialization could be exploited with malicious JSON
        @SuppressWarnings("unchecked")
        Map<String, Object> requestData = objectMapper.readValue(requestBody.toString(), Map.class);
        
        String name = (String) requestData.get("name");
        String email = (String) requestData.get("email");
        String subject = (String) requestData.get("subject");
        String message = (String) requestData.get("message");
        
        // VULNERABILITY: No CSRF protection on API endpoints
        // VULNERABILITY: No rate limiting - could be used for spam
        
        // Basic validation (insufficient)
        if (name == null || email == null || message == null) {
            sendErrorResponse(response, out, 400, "Missing required fields: name, email, message");
            return;
        }
        
        // Get client information
        String clientIP = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        
        FeedbackDAO dao = new FeedbackDAO();
        long feedbackId = dao.storeFeedback(name, email, subject, message, clientIP, userAgent);
        
        // Send notification email
        EmailUtil emailUtil = new EmailUtil();
        boolean emailSent = emailUtil.sendFeedbackNotification(name, email, subject, message);
        
        Map<String, Object> response_data = new HashMap<>();
        response_data.put("status", "success");
        response_data.put("message", "Feedback submitted successfully");
        response_data.put("feedbackId", feedbackId);
        response_data.put("emailNotificationSent", emailSent);
        
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void handleBulkCreateFeedback(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws SQLException, IOException {
        
        // VULNERABILITY: No admin authentication check for bulk operations
        // VULNERABILITY: No rate limiting on bulk operations - potential DoS
        
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> requestData = objectMapper.readValue(requestBody.toString(), Map.class);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> feedbackList = (List<Map<String, Object>>) requestData.get("feedback");
        
        if (feedbackList == null || feedbackList.isEmpty()) {
            sendErrorResponse(response, out, 400, "No feedback data provided");
            return;
        }
        
        // VULNERABILITY: No limit on bulk size - could cause memory exhaustion
        FeedbackDAO dao = new FeedbackDAO();
        int successCount = 0;
        int errorCount = 0;
        
        for (Map<String, Object> feedbackData : feedbackList) {
            try {
                String name = (String) feedbackData.get("name");
                String email = (String) feedbackData.get("email");
                String subject = (String) feedbackData.get("subject");
                String message = (String) feedbackData.get("message");
                
                dao.storeFeedback(name, email, subject, message, getClientIP(request), 
                               request.getHeader("User-Agent"));
                successCount++;
                
            } catch (Exception e) {
                errorCount++;
            }
        }
        
        Map<String, Object> response_data = new HashMap<>();
        response_data.put("status", "completed");
        response_data.put("processed", feedbackList.size());
        response_data.put("successful", successCount);
        response_data.put("errors", errorCount);
        
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void handleUpdateFeedback(String feedbackId, HttpServletRequest request, 
                                    HttpServletResponse response, PrintWriter out) 
                                    throws SQLException, IOException {
        
        // VULNERABILITY: No authentication check - anyone can update any feedback
        // VULNERABILITY: No ownership verification
        
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> requestData = objectMapper.readValue(requestBody.toString(), Map.class);
        
        String status = (String) requestData.get("status");
        String adminNotes = (String) requestData.get("adminNotes");
        
        FeedbackDAO dao = new FeedbackDAO();
        boolean updated = dao.updateFeedbackStatus(feedbackId, status, adminNotes);
        
        Map<String, Object> response_data = new HashMap<>();
        if (updated) {
            response_data.put("status", "success");
            response_data.put("message", "Feedback updated successfully");
        } else {
            response_data.put("status", "error");
            response_data.put("message", "Failed to update feedback");
        }
        
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void handleDeleteFeedback(String feedbackId, HttpServletRequest request, 
                                    HttpServletResponse response, PrintWriter out) 
                                    throws SQLException, IOException {
        
        // VULNERABILITY: No admin authentication
        // VULNERABILITY: Uses weak token system
        String adminToken = request.getHeader("X-Admin-Token");
        if (adminToken == null) {
            adminToken = request.getParameter("token");
        }
        if (adminToken == null) {
            adminToken = "admin123"; // Default weak token!
        }
        
        FeedbackDAO dao = new FeedbackDAO();
        boolean deleted = dao.deleteFeedback(feedbackId, adminToken);
        
        Map<String, Object> response_data = new HashMap<>();
        if (deleted) {
            response_data.put("status", "success");
            response_data.put("message", "Feedback deleted successfully");
        } else {
            response_data.put("status", "error");
            response_data.put("message", "Failed to delete feedback or unauthorized");
        }
        
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void handleGetStats(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws SQLException, IOException {
        
        // VULNERABILITY: No admin authentication for sensitive statistics
        // This endpoint exposes system information that could help attackers
        
        FeedbackDAO dao = new FeedbackDAO();
        
        // Get some statistics (these queries are also vulnerable to injection)
        String timeFilter = request.getParameter("timeFilter");
        if (timeFilter == null) timeFilter = "1=1";
        
        List<FeedbackRecord> recentFeedback = dao.getRecentFeedback(1000, timeFilter);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFeedback", recentFeedback.size());
        stats.put("systemInfo", System.getProperties()); // VULNERABILITY: Information disclosure
        stats.put("environmentVars", System.getenv()); // VULNERABILITY: Environment variable exposure
        stats.put("serverTime", System.currentTimeMillis());
        stats.put("javaVersion", System.getProperty("java.version"));
        stats.put("osInfo", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Memory usage information (could help with DoS attacks)
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("totalMemory", runtime.totalMemory());
        memoryInfo.put("freeMemory", runtime.freeMemory());
        memoryInfo.put("maxMemory", runtime.maxMemory());
        memoryInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        stats.put("memoryUsage", memoryInfo);
        
        Map<String, Object> response_data = new HashMap<>();
        response_data.put("status", "success");
        response_data.put("stats", stats);
        
        out.print(objectMapper.writeValueAsString(response_data));
    }
    
    private void sendErrorResponse(HttpServletResponse response, PrintWriter out, 
                                 int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        out.print(objectMapper.writeValueAsString(errorResponse));
    }
    
    private String getClientIP(HttpServletRequest request) {
        // VULNERABILITY: IP address can be spoofed via headers
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim(); // Take first IP (can be spoofed!)
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP; // Can also be spoofed!
        }
        
        return request.getRemoteAddr();
    }
    
    private String getStackTrace(Exception e) {
        // VULNERABILITY: Exposing full stack traces in API responses
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }
}
