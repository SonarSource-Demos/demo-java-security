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

@WebServlet("/api/user")
public class UserLookupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (username == null || username.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\":\"username parameter is required\"}");
            return;
        }

        List<String> users;
        try {
            users = new DBUtils().findUsers(username);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"lookup failed\"}");
            return;
        }

        if (users.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print("{\"error\":\"user not found\"}");
            return;
        }

        PrintWriter out = response.getWriter();
        out.print("{\"username\":\"" + escapeJson(username) + "\",\"userIds\":[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) {
                out.print(",");
            }
            out.print("\"" + escapeJson(users.get(i)) + "\"");
        }
        out.print("]}");
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
