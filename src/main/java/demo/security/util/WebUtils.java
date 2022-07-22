package demo.security.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class WebUtils {

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie c = new Cookie(name, value);
        response.addCookie(c);
    }
}
