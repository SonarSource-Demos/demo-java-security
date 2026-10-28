package demo.security.servlet;

import demo.security.util.Utils;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/scripts")
public class ScriptServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getUserPrincipal() == null || !request.isUserInRole("admin")) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (IOException e) {
                log("Failed to send forbidden response", e);
            }
            return;
        }
        String data = request.getParameter("data");
        if (data == null || data.isEmpty()) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {
                log("Failed to send bad request response", e);
            }
            return;
        }
        try {
            Utils.executeJs(data);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}