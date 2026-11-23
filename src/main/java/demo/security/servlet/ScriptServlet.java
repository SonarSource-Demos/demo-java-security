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
        if (request.getUserPrincipal() == null) {
            reject(response, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!request.isUserInRole("admin")) {
            reject(response, HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String data = request.getParameter("data");
        try {
            Utils.executeJs(data);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reject(HttpServletResponse response, int status) {
        try {
            response.sendError(status);
        } catch (IOException e) {
            response.setStatus(status);
        }
    }
}