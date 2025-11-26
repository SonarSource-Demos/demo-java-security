package demo.security.servlet;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

@WebServlet("/captcha")
public class CaptchaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configure CAPTCHA
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial");
        
        DefaultKaptcha captchaProducer = new DefaultKaptcha();
        captchaProducer.setConfig(new Config(properties));
        
        // Generate CAPTCHA
        String captchaText = captchaProducer.createText();
        
        // Store in session for validation
        request.getSession().setAttribute("captcha", captchaText);
        
        // Create image
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);
        
        // Send image response
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(captchaImage, "jpg", out);
        out.flush();
        out.close();
    }
}
