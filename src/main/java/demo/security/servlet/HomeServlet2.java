package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public HomeServlet2() {
        super();
        // Constructor logic can be added here if needed
    }


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name != null) {
            name = name.trim();
        } else {
            name = "Guest";
        }
        // Sanitize user input to prevent XSS
        name = name.replaceAll("[<>]", "");
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            out.print("<h2>Hello " + name + "</h2>");
        } catch (IOException e) {
            // Handle IOException from getWriter
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing response");
            } catch (IOException ex) {
                // Log error or take further action if needed
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        // POST-specific logic can be added here if needed
        try {
            doGet(request, response);
        } catch (ServletException | IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in doPost");
            } catch (IOException ex) {
                // Log error or take further action if needed
            }
        }
    }

}
