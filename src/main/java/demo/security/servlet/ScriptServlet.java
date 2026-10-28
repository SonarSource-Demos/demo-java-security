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
        try {
            if (request.getUserPrincipal() == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (!request.isUserInRole("admin")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            String data = request.getParameter("data");
            if (data == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Utils.executeJs(data);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            log("Failed to send error response", e);
        }
    }
}