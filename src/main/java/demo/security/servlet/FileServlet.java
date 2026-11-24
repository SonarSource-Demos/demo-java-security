package demo.security.servlet;

import demo.security.util.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

@WebServlet("/files")
public class FileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getUserPrincipal() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!request.isUserInRole("admin")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String data = request.getParameter("data");
        if (data == null || data.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        File baseDir = (File) getServletContext().getAttribute(ServletContext.TEMPDIR);
        if (baseDir == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        try {
            Utils.deleteFile(baseDir, data);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchFileException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
