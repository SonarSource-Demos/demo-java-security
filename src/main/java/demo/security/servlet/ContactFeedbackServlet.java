package demo.security.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/contact-feedback")
public class ContactFeedbackServlet extends HttpServlet {
    private static final List<Map<String, String>> FEEDBACKS = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String category = request.getParameter("category");
        String feedback = request.getParameter("feedback");
        String captcha = request.getParameter("captcha");
        String captchaExpected = (String) request.getSession().getAttribute("contactFeedbackCaptcha");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (captchaExpected == null || captcha == null || !captchaExpected.equalsIgnoreCase(captcha)) {
            out.println("<html><body><h2>CAPTCHA validation failed!</h2><a href='contact-feedback.jsp'>Back</a></body></html>");
            return;
        }
        Map<String, String> entry = new HashMap<>();
        entry.put("name", name);
        entry.put("email", email);
        entry.put("category", category);
        entry.put("feedback", feedback);
        FEEDBACKS.add(entry);
        out.println("<html><body><h2>Thank you for your feedback!</h2><a href='contact-feedback.jsp'>Back</a></body></html>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String category = request.getParameter("category");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body><h2>Feedback Results</h2>");
        for (Map<String, String> fb : FEEDBACKS) {
            if ((email != null && !email.isEmpty() && email.equals(fb.get("email"))) ||
                (category != null && !category.isEmpty() && category.equals(fb.get("category")))) {
                out.println("<div><b>Name:</b> " + fb.get("name") + "<br><b>Email:</b> " + fb.get("email") + "<br><b>Category:</b> " + fb.get("category") + "<br><b>Feedback:</b> " + fb.get("feedback") + "</div><hr>");
            }
        }
        out.println("<a href='contact-feedback.jsp'>Back</a></body></html>");
    }
}
