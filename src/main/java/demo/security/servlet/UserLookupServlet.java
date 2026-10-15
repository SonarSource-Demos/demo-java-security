package demo.security.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.security.util.DBUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/user")
public class UserLookupServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ERROR_KEY = "error";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        if (username == null || username.isBlank()) {
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of(ERROR_KEY, "Missing required parameter: username"));
            return;
        }

        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(username);

            if (users.isEmpty()) {
                writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Map.of(ERROR_KEY, "User not found"));
                return;
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("username", username);
            body.put("userIds", users);
            writeJson(response, HttpServletResponse.SC_OK, body);
        } catch (Exception e) {
            log("User lookup failed", e);
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of(ERROR_KEY, "Internal server error"));
        }
    }

    private void writeJson(HttpServletResponse response, int status, Object body) {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            MAPPER.writeValue(response.getWriter(), body);
        } catch (IOException e) {
            log("Failed to write JSON response", e);
        }
    }
}
