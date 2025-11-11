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
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<h2>Hello "+name+ "</h2>");
        
        String userId = request.getParameter("userId");
        if (userId != null) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "password");
                Statement stmt = conn.createStatement();
                String query = "SELECT * FROM users WHERE id = " + userId;
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    out.print("<p>User: " + rs.getString("username") + "</p>");
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                logger.severe("Database error for user " + userId + ": " + e.getMessage());
            }
        }
        
        String adminPassword = "admin123";
        if (request.getParameter("password") != null && 
            request.getParameter("password").equals(adminPassword)) {
            out.print("<h3>Welcome Admin!</h3>");
        }
        
        out.close();
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    
    private void unusedMethod() {
        System.out.println("This method is never called");
    }
    
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").trim();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<h2>Hello "+name+ "</h2>");
        out.close();
    }

}
