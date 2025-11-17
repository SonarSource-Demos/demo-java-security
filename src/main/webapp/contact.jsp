<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.google.code.kaptcha.servlet.KaptchaServlet"%>
<%@ page import="com.google.code.kaptcha.util.Config"%>
<%@ page import="com.google.code.kaptcha.impl.DefaultKaptcha"%>
<%@ page import="java.util.Properties"%>
<%@ page import="javax.imageio.ImageIO"%>
<%@ page import="java.awt.image.BufferedImage"%>
<%@ page import="java.io.ByteArrayOutputStream"%>
<%@ page import="java.util.Base64"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Contact Feedback Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        input[type="text"],
        input[type="email"],
        textarea,
        select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        .captcha-container {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .captcha-image {
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover {
            background-color: #45a049;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
    </style>
</head>
<body>
    <h1>Contact Feedback Form</h1>
    
    <form action="contact" method="post">
        <div class="form-group">
            <label for="name">Name:</label>
            <input type="text" id="name" name="name" required>
        </div>
        
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
        </div>
        
        <div class="form-group">
            <label for="subject">Subject:</label>
            <input type="text" id="subject" name="subject" required>
        </div>
        
        <div class="form-group">
            <label for="priority">Priority:</label>
            <select id="priority" name="priority">
                <option value="low">Low</option>
                <option value="normal" selected>Normal</option>
                <option value="high">High</option>
                <option value="urgent">Urgent</option>
            </select>
        </div>
        
        <div class="form-group">
            <label for="message">Message:</label>
            <textarea id="message" name="message" required></textarea>
        </div>
        
        <%
            // Generate CAPTCHA
            String captchaText = "";
            DefaultKaptcha captchaProducer = new DefaultKaptcha();
            Properties props = new Properties();
            props.setProperty("kaptcha.border", "yes");
            props.setProperty("kaptcha.border.color", "black");
            props.setProperty("kaptcha.textproducer.font.color", "black");
            props.setProperty("kaptcha.textproducer.char.length", "5");
            props.setProperty("kaptcha.image.width", "150");
            props.setProperty("kaptcha.image.height", "50");
            
            Config config = new Config(props);
            captchaProducer.setConfig(config);
            
            captchaText = captchaProducer.createText();
            BufferedImage captchaImage = captchaProducer.createImage(captchaText);
            
            // Store CAPTCHA in session
            session.setAttribute("captcha", captchaText);
            
            // Convert image to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(captchaImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        %>
        
        <div class="form-group">
            <label for="captcha-input">CAPTCHA (Optional):</label>
            <div class="captcha-container">
                <img src="data:image/png;base64,<%= base64Image %>" alt="CAPTCHA" class="captcha-image"/>
                <input type="text" id="captcha-input" name="captcha" placeholder="Enter CAPTCHA" style="width: auto;">
            </div>
        </div>
        
        <button type="submit">Submit Feedback</button>
    </form>
    
    <a href="index.jsp" class="back-link">Back to Home</a>
</body>
</html>

