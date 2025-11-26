package demo.security.servlet;

import com.google.code.kaptcha.Producer;
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

@WebServlet("/captcha-image")
public class CaptchaImageServlet extends HttpServlet {
    private Producer kaptchaProducer;

    @Override
    public void init() throws ServletException {
        Properties props = new Properties();
        props.put("kaptcha.textproducer.char.length", "5");
        Config config = new Config(props);
        kaptchaProducer = config.getProducerImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String capText = kaptchaProducer.createText();
        HttpSession session = request.getSession();
        session.setAttribute("contactFeedbackCaptcha", capText);
        BufferedImage bi = kaptchaProducer.createImage(capText);
        response.setContentType("image/png");
        ImageIO.write(bi, "png", response.getOutputStream());
    }
}
