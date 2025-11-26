package demo.security.servlet;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@WebServlet("/captcha")
public class CaptchaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DefaultKaptcha kaptcha;
    
    @Override
    public void init() throws ServletException {
        super.init();
        kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "black");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        Config config = new Config(properties);
        kaptcha.setConfig(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Generate captcha text
        String captchaText = kaptcha.createText();
        
        // Store in session
        HttpSession session = request.getSession();
        session.setAttribute("KAPTCHA_SESSION_KEY", captchaText);
        
        // Generate captcha image
        BufferedImage captchaImage = kaptcha.createImage(captchaText);
        
        // Set response headers
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        
        // Write image to response
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(captchaImage, "jpg", outputStream);
        byte[] captchaBytes = outputStream.toByteArray();
        
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(captchaBytes);
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}

