package demo.security.servlet;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

@WebServlet("/captcha")
public class CaptchaServlet extends HttpServlet {
    
    private Producer captchaProducer;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Configure Kaptcha
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        this.captchaProducer = defaultKaptcha;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Generate CAPTCHA text
        String captchaText = captchaProducer.createText();
        
        // Store CAPTCHA answer in session
        session.setAttribute("captchaAnswer", captchaText);
        
        // Generate CAPTCHA image
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // Write image to response
        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
    }
}

