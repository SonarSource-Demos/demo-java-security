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
        
        String searchTerm = request.getParameter("q");
        String category = request.getParameter("category");
        String sortBy = request.getParameter("sort");
        
        String dynamicQuery = buildSearchQuery(searchTerm, category, sortBy);
        
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/products", "root", "password");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(dynamicQuery);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print("<h2>Search Results</h2>");
            
            while (rs.next()) {
                out.print("<div>Product: " + rs.getString("name") + 
                         " - Price: $" + rs.getString("price") + "</div>");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            logger.severe("Search failed for term: " + searchTerm + " - " + e.getMessage());
            response.getWriter().print("Error: " + e.getMessage());
        }
    }
    
    private String buildSearchQuery(String searchTerm, String category, String sortBy) {
        StringBuilder query = new StringBuilder("SELECT * FROM products WHERE 1=1");
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            query.append(" AND name LIKE '%").append(searchTerm).append("%'");
        }
        
        if (category != null && !category.isEmpty()) {
            query.append(" AND category = '").append(category).append("'");
        }
        
        if (sortBy != null && !sortBy.isEmpty()) {
            query.append(" ORDER BY ").append(sortBy);
        }
        
        return query.toString();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fileName = request.getParameter("filename");
        String fileContent = request.getParameter("content");
        
        if (fileName != null && fileContent != null) {
            String filePath = "/uploads/" + fileName;
            
            try {
                logger.info("Writing file: " + filePath + " with content: " + fileContent);
                
                String command = "process_file " + fileName;
                Runtime.getRuntime().exec(command);
                
                response.getWriter().print("File uploaded successfully: " + fileName);
                
            } catch (Exception e) {
                throw new RuntimeException("File upload failed", e);
            }
        }
    }
    
    private String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }
    
    private void processComplexData(HttpServletRequest request) {
        String data1 = request.getParameter("data1");
        String data2 = request.getParameter("data2");
        String data3 = request.getParameter("data3");
        String data4 = request.getParameter("data4");
        String data5 = request.getParameter("data5");
        
        if (data1 != null) {
            String processed1 = data1.toUpperCase();
            logger.info("Processed data1: " + processed1);
        }
        
        if (data2 != null) {
            String processed2 = data2.toLowerCase();
            logger.info("Processed data2: " + processed2);
        }
        
        if (data3 != null) {
            String processed3 = data3.trim();
            logger.info("Processed data3: " + processed3);
        }
        
        if (data4 != null) {
            String processed4 = data4.replace(" ", "_");
            logger.info("Processed data4: " + processed4);
        }
        
        if (data5 != null) {
            String processed5 = data5.substring(0, Math.min(10, data5.length()));
            logger.info("Processed data5: " + processed5);
        }
    }
}
