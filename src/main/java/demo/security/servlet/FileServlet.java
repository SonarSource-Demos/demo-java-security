package demo.security.servlet;

import demo.security.util.RateLimitFilter;
import demo.security.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/files")
public class FileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!RateLimitFilter.allowRequest(request, response)) return;
        String data = request.getParameter("data");
        Utils.deleteFile(data);
    }
}
