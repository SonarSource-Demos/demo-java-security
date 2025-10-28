package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HomeServlet.class.getName());

    public HomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        // SonarQube Issue: Null pointer vulnerability - no null check
        String name = request.getParameter("name").trim();
        
        // SonarQube Issue: XSS vulnerability - direct output without escaping
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<h2>Hello "+name+ "</h2>");
        
        // SonarQube Issue: SQL Injection vulnerability - taint analysis will catch this
        String userId = request.getParameter("userId");
        if (userId != null) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "password");
                Statement stmt = conn.createStatement();
                // This is a classic SQL injection - taint analysis will flag this
                String query = "SELECT * FROM users WHERE id = " + userId;
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    out.print("<p>User: " + rs.getString("username") + "</p>");
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                // SonarQube Issue: Information disclosure - logging sensitive data
                logger.severe("Database error for user " + userId + ": " + e.getMessage());
            }
        }
        
        // SonarQube Issue: Hardcoded password
        String adminPassword = "admin123";
        if (request.getParameter("password") != null && 
            request.getParameter("password").equals(adminPassword)) {
            out.print("<h3>Welcome Admin!</h3>");
        }
        
        out.close();
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        // SonarQube Issue: Empty method - code smell
        doGet(request, response);
    }
    
    // SonarQube Issue: Dead code - unused method
    private void unusedMethod() {
        System.out.println("This method is never called");
    }
    
    // SonarQube Issue: Duplicated code - same logic as doGet
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<h2>Hello "+name+ "</h2>");
        out.close();
    }

}
