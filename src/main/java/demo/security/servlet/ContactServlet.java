package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String feedbackId = request.getParameter("id");
        
        if (feedbackId == null || feedbackId.isEmpty()) {
            response.sendRedirect("contact.jsp");
            return;
        }
        
        try {
            DBUtils db = new DBUtils();
            List<String> feedbackList = db.findContactFeedback(feedbackId);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>Contact Feedback</h1>");
            
            feedbackList.forEach(feedback -> out.println("<div>" + feedback + "</div>"));
            
            out.println("</body></html>");
            out.close();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving feedback");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        String attachment = request.getParameter("attachment");
        
        if (name == null || email == null || message == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
            return;
        }
        
        try {
            DBUtils db = new DBUtils();
            db.saveContactFeedback(name, email, message, attachment);
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>Thank you for your feedback!</h1>");
            out.println("<p>Name: " + name + "</p>");
            out.println("<p>Email: " + email + "</p>");
            out.println("<p>Message: " + message + "</p>");
            
            if (attachment != null && !attachment.isEmpty()) {
                String attachmentPath = WebUtils.resolveAttachmentPath(attachment);
                out.println("<p>Attachment saved at: " + attachmentPath + "</p>");
            }
            
            out.println("</body></html>");
            out.close();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error saving feedback");
        }
    }
}

