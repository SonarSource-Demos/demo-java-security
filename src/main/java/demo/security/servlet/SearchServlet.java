package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SearchServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // SonarQube Issue: Taint Analysis - Complex data flow vulnerability
        String searchTerm = request.getParameter("q");
        String category = request.getParameter("category");
        String sortBy = request.getParameter("sort");
        
        // This creates a taint flow from user input to SQL query
        String dynamicQuery = buildSearchQuery(searchTerm, category, sortBy);
        
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/products", "root", "password");
            
            // SonarQube Issue: SQL Injection - taint analysis will catch this
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(dynamicQuery);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print("<h2>Search Results</h2>");
            
            while (rs.next()) {
                // SonarQube Issue: XSS vulnerability - direct output
                out.print("<div>Product: " + rs.getString("name") + 
                         " - Price: $" + rs.getString("price") + "</div>");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            // SonarQube Issue: Information disclosure in error message
            logger.severe("Search failed for term: " + searchTerm + " - " + e.getMessage());
            response.getWriter().print("Error: " + e.getMessage());
        }
    }
    
    // SonarQube Issue: Taint Analysis - This method propagates taint
    private String buildSearchQuery(String searchTerm, String category, String sortBy) {
        StringBuilder query = new StringBuilder("SELECT * FROM products WHERE 1=1");
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // Taint flows through string concatenation
            query.append(" AND name LIKE '%").append(searchTerm).append("%'");
        }
        
        if (category != null && !category.isEmpty()) {
            // Taint flows through string concatenation
            query.append(" AND category = '").append(category).append("'");
        }
        
        if (sortBy != null && !sortBy.isEmpty()) {
            // Taint flows through string concatenation - ORDER BY injection
            query.append(" ORDER BY ").append(sortBy);
        }
        
        return query.toString();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // SonarQube Issue: Taint Analysis - File upload vulnerability
        String fileName = request.getParameter("filename");
        String fileContent = request.getParameter("content");
        
        if (fileName != null && fileContent != null) {
            // SonarQube Issue: Path Traversal - taint analysis will catch this
            String filePath = "/uploads/" + fileName;
            
            try {
                // Simulate file writing (in real app, this would write to filesystem)
                logger.info("Writing file: " + filePath + " with content: " + fileContent);
                
                // SonarQube Issue: Command Injection - taint analysis will catch this
                String command = "process_file " + fileName;
                Runtime.getRuntime().exec(command);
                
                response.getWriter().print("File uploaded successfully: " + fileName);
                
            } catch (Exception e) {
                // SonarQube Issue: Generic exception handling
                throw new RuntimeException("File upload failed", e);
            }
        }
    }
    
    // SonarQube Issue: Security Hotspot - Weak cryptographic algorithm
    private String hashPassword(String password) {
        // This would be flagged as a security hotspot
        return Integer.toString(password.hashCode());
    }
    
    // SonarQube Issue: Code Smell - Long method
    private void processComplexData(HttpServletRequest request) {
        String data1 = request.getParameter("data1");
        String data2 = request.getParameter("data2");
        String data3 = request.getParameter("data3");
        String data4 = request.getParameter("data4");
        String data5 = request.getParameter("data5");
        
        // Process data1
        if (data1 != null) {
            String processed1 = data1.toUpperCase();
            logger.info("Processed data1: " + processed1);
        }
        
        // Process data2
        if (data2 != null) {
            String processed2 = data2.toLowerCase();
            logger.info("Processed data2: " + processed2);
        }
        
        // Process data3
        if (data3 != null) {
            String processed3 = data3.trim();
            logger.info("Processed data3: " + processed3);
        }
        
        // Process data4
        if (data4 != null) {
            String processed4 = data4.replace(" ", "_");
            logger.info("Processed data4: " + processed4);
        }
        
        // Process data5
        if (data5 != null) {
            String processed5 = data5.substring(0, Math.min(10, data5.length()));
            logger.info("Processed data5: " + processed5);
        }
    }
}
