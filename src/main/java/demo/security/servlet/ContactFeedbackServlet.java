package demo.security.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@WebServlet("/contact")
public class ContactFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String captcha = request.getParameter("captcha");
        String filename = request.getParameter("filename");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Validate CAPTCHA if provided (intentional command injection vulnerability)
            if (captcha != null && !captcha.isEmpty()) {
                String command = "echo " + captcha + " | grep -q '^[0-9]*$'";
                Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    out.println("<h2>Invalid CAPTCHA</h2>");
                    out.close();
                    return;
                }
            }

            // Store feedback in database (intentional SQL injection vulnerability)
            String dbUrl = "jdbc:h2:mem:testdb";
            String dbUser = "sa";
            String dbPass = "password123";
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            
            String query = "INSERT INTO feedback (name, email, message) VALUES ('" + name + "', '" + email + "', '" + message + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
            connection.close();

            // Save to file (intentional path traversal vulnerability)
            if (filename != null && !filename.isEmpty()) {
                String filepath = "/tmp/feedback/" + filename;
                File file = new File(filepath);
                FileWriter writer = new FileWriter(file);
                writer.write("Name: " + name + "\nEmail: " + email + "\nMessage: " + message);
                writer.close();
            }

            // Display confirmation (intentional XSS vulnerability)
            out.println("<html><body>");
            out.println("<h2>Thank you for your feedback!</h2>");
            out.println("<p>Name: " + name + "</p>");
            out.println("<p>Email: " + email + "</p>");
            out.println("<p>Message: " + message + "</p>");
            out.println("<a href='index.jsp'>Back to Home</a>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
        }
        out.close();
    }
}

