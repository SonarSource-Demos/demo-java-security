package demo.security.servlet;

import demo.security.util.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    private static final String HTML_HEADER = "<html><body>";
    private static final String HTML_FOOTER = "</body></html>";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String feedbackId = request.getParameter("id");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (feedbackId != null && !feedbackId.isEmpty()) {
            try {
                DBUtils db = new DBUtils();
                List<String> feedback = db.findContactFeedback(feedbackId);
                out.println(HTML_HEADER);
                out.println("<h1>Contact Feedback</h1>");
                feedback.forEach(item -> out.println("<p>" + item + "</p>"));
                out.println(HTML_FOOTER);
            } catch (SQLException e) {
                throw new ServletException("Database connection error", e);
            } catch (Exception e) {
                throw new ServletException("Error retrieving feedback", e);
            }
        } else {
            out.println(HTML_HEADER);
            out.println("<h1>Contact Form</h1>");
            out.println("<form method='post'>");
            out.println("Name: <input type='text' name='name'/><br/>");
            out.println("Email: <input type='text' name='email'/><br/>");
            out.println("Message: <textarea name='message'></textarea><br/>");
            out.println("<input type='submit' value='Submit'/>");
            out.println("</form>");
            out.println(HTML_FOOTER);
        }
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            DBUtils db = new DBUtils();
            db.saveContactFeedback(name, email, message);

            out.println(HTML_HEADER);
            out.println("<h1>Thank You!</h1>");
            out.println("<p>Thank you, " + name + "!</p>");
            out.println("<p>We received your message:</p>");
            out.println("<p>" + message + "</p>");
            out.println("<p>We'll respond to: " + email + "</p>");
            out.println(HTML_FOOTER);
        } catch (SQLException e) {
            throw new ServletException("Database connection error", e);
        } catch (Exception e) {
            throw new ServletException("Error saving feedback", e);
        }
        out.close();
    }
}
