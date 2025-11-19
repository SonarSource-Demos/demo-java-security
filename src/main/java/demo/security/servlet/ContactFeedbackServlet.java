package demo.security.servlet;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/contact")
public class ContactFeedbackServlet extends HttpServlet {

    private static final String CONTENT_TYPE_HTML = "text/html";

    @Override
    @SuppressWarnings("java:S1989")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }

    @Override
    @SuppressWarnings("java:S1989")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("submit".equals(action)) {
            handleFeedbackSubmission(request, response);
        } else if ("search".equals(action)) {
            handleFeedbackSearch(request, response);
        } else if ("export".equals(action)) {
            handleFeedbackExport(request, response);
        } else if ("process".equals(action)) {
            handleFeedbackProcessing(request, response);
        } else if ("load".equals(action)) {
            handleFeedbackLoad(request, response);
        }
    }

    // SQL Injection vulnerability - concatenating user input directly into SQL query
    private void handleFeedbackSubmission(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String category = request.getParameter("category");

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            String query = "INSERT INTO feedback (name, email, message, category) VALUES ('" + 
                          name + "', '" + email + "', '" + message + "', '" + category + "')";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
            }
            
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter out = response.getWriter();
            out.println("<h2>Thank you for your feedback, " + name + "!</h2>");
            out.println("<p>We received your message: " + message + "</p>");
            out.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // SQL Injection vulnerability - searching feedback with user-controlled query
    private void handleFeedbackSearch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("search");

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            String query = "SELECT * FROM feedback WHERE message LIKE '%" + searchTerm + "%' OR name LIKE '%" + searchTerm + "%'";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                
                response.setContentType(CONTENT_TYPE_HTML);
                PrintWriter out = response.getWriter();
                out.println("<h2>Search Results</h2>");
                while (resultSet.next()) {
                    out.println("<div>");
                    out.println("<p>Name: " + resultSet.getString("name") + "</p>");
                    out.println("<p>Message: " + resultSet.getString("message") + "</p>");
                    out.println("</div>");
                }
                out.close();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Path Traversal vulnerability - user controls file path
    private void handleFeedbackExport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = request.getParameter("filename");
        String exportPath = "/var/feedback/exports/" + filename;
        
        File file = new File(exportPath);
        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    // Command Injection vulnerability - executing system commands with user input
    private void handleFeedbackProcessing(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String feedbackId = request.getParameter("id");
        String processorType = request.getParameter("processor");
        
        try {
            String[] command = {"python3", "/opt/feedback/process.py", "--id=" + feedbackId, "--type=" + processorType};
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter out = response.getWriter();
            out.println("<h2>Processing Results</h2>");
            String line;
            while ((line = reader.readLine()) != null) {
                out.println("<p>" + line + "</p>");
            }
            out.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Insecure Deserialization vulnerability - deserializing user-provided data
    private void handleFeedbackLoad(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = request.getParameter("data");
        
        try {
            byte[] decoded = Base64.decodeBase64(data);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decoded));
            Object feedbackData = ois.readObject();
            
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter out = response.getWriter();
            out.println("<h2>Feedback Loaded</h2>");
            out.println("<p>Data: " + feedbackData.toString() + "</p>");
            out.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

