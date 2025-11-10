package demo.security.servlet;

import demo.security.util.DBUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<h2>Contact Feedback Form</h2>");
        out.print("<p>Please use the form to submit your feedback.</p>");
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        
        try {
            DBUtils db = new DBUtils();
            db.saveFeedback(name, email, subject, message);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.print("<h2>Thank you for your feedback!</h2>");
            out.print("<p>Name: " + name + "</p>");
            out.print("<p>Email: " + email + "</p>");
            out.print("<p>Subject: " + subject + "</p>");
            out.print("<p>Message: " + message + "</p>");
            out.close();
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}

