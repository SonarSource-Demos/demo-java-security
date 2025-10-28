package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    
    // SonarQube Issue: Magic number
    private static final int MAX_RETRIES = 3;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // SonarQube Issue: Null pointer vulnerability - no null check
        String user = request.getParameter("username");
        
        // SonarQube Issue: Taint analysis - user input used in SQL without sanitization
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            // SonarQube Issue: XSS vulnerability - direct output without escaping
            users.forEach((result) -> {
                out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            // SonarQube Issue: Generic exception handling
            throw new RuntimeException(e);
        }
    }

    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth != null) {
            try {
                byte[] decoded = Base64.decodeBase64(sessionAuth);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(decoded));
                // SonarQube Issue: Deserialization vulnerability - unsafe deserialization
                return (SessionHeader) in.readObject();
            } catch (Exception e) {
                // SonarQube Issue: Silent exception handling
                return null;
            }
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader == null) return;
        
        // SonarQube Issue: Null pointer vulnerability - no null check
        String user = sessionHeader.getUsername();
        
        // SonarQube Issue: Taint analysis - session data used without validation
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            // SonarQube Issue: XSS vulnerability - direct output without escaping
            users.forEach((result) -> {
                out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            // SonarQube Issue: Generic exception handling
            throw new RuntimeException(e);
        }
    }
    
    // SonarQube Issue: Performance issue - inefficient loop
    private void inefficientLoop() {
        for (int i = 0; i < 1000000; i++) {
            String temp = "Processing item " + i;
            // Unnecessary string concatenation in loop
        }
    }
    
    // SonarQube Issue: Security hotspot - hardcoded cryptographic key
    private String encryptData(String data) {
        String key = "mySecretKey123";
        // This would be flagged as a security hotspot
        return data + "_encrypted_with_" + key;
    }
    
    // SonarQube Issue: Code smell - too many parameters
    private void processUserData(String username, String password, String email, 
                               String firstName, String lastName, String phone, 
                               String address, String city, String state, String zip) {
        // Method with too many parameters
    }
    
    // SonarQube Issue: Dead code - unreachable code
    private void unreachableCode() {
        return;
        System.out.println("This will never execute"); // Dead code
    }
}
