package demo.security.servlet;

import demo.security.util.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ContactServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String feedbackId = request.getParameter("id");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (feedbackId != null && !feedbackId.isEmpty()) {
            try {
                DBUtils db = new DBUtils();
                List<String> feedback = db.findContactFeedback(feedbackId);
                out.println("<html><body>");
                out.println("<h1>Contact Feedback</h1>");
                feedback.forEach(item -> {
                    out.println("<p>" + item + "</p>");
                });
                out.println("</body></html>");
            } catch (Exception e) {
                logger.severe("Error retrieving feedback: " + e.getMessage());
                throw new ServletException("Database error", e);
            }
        } else {
            out.println("<html><body>");
            out.println("<h1>Contact Form</h1>");
            out.println("<form method='post'>");
            out.println("Name: <input type='text' name='name'/><br/>");
            out.println("Email: <input type='text' name='email'/><br/>");
            out.println("Message: <textarea name='message'></textarea><br/>");
            out.println("<input type='submit' value='Submit'/>");
            out.println("</form>");
            out.println("</body></html>");
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

            out.println("<html><body>");
            out.println("<h1>Thank You!</h1>");
            out.println("<p>Thank you, " + name + "!</p>");
            out.println("<p>We received your message:</p>");
            out.println("<p>" + message + "</p>");
            out.println("<p>We'll respond to: " + email + "</p>");
            out.println("</body></html>");
        } catch (Exception e) {
            logger.severe("Error saving feedback: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
        out.close();
    }
}

