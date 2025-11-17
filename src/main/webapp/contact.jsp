<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Contact Feedback Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"],
        input[type="email"],
        textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            min-height: 100px;
            resize: vertical;
        }
        .captcha-container {
            margin: 15px 0;
        }
        .captcha-image {
            border: 1px solid #ddd;
            margin-bottom: 10px;
        }
        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        input[type="submit"]:hover {
            background-color: #45a049;
        }
        .error {
            color: red;
            margin-top: 10px;
        }
        .success {
            color: green;
            margin-top: 10px;
        }
        .checkbox-container {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }
        .checkbox-container input[type="checkbox"] {
            width: auto;
            margin-right: 10px;
        }
    </style>
</head>
<body>
    <h2>Contact Feedback Form</h2>
    
    <% 
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        if (message != null && message.equals("success")) {
    %>
        <div class="success">Thank you for your feedback! Your message has been received.</div>
    <% 
        }
        if (error != null) {
    %>
        <div class="error"><%= error %></div>
    <% 
        }
    %>
    
    <form action="contactFeedback" method="post">
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
            <label for="feedbackMessage">Message:</label>
            <textarea id="feedbackMessage" name="feedbackMessage" required></textarea>
        </div>
        
        <div class="checkbox-container">
            <input type="checkbox" id="enableCaptcha" name="enableCaptcha" value="true">
            <label for="enableCaptcha" style="font-weight: normal; margin-bottom: 0;">Enable CAPTCHA verification (optional)</label>
        </div>
        
        <div id="captchaSection" class="captcha-container" style="display: none;">
            <label for="captchaInput">Please enter the characters shown below:</label>
            <img src="captcha" alt="CAPTCHA" class="captcha-image" id="captchaImage">
            <input type="text" name="captchaInput" id="captchaInput" placeholder="Enter CAPTCHA">
        </div>
        
        <input type="submit" value="Submit Feedback">
    </form>
    
    <script>
        document.getElementById('enableCaptcha').addEventListener('change', function() {
            var captchaSection = document.getElementById('captchaSection');
            var captchaInput = document.getElementById('captchaInput');
            if (this.checked) {
                captchaSection.style.display = 'block';
                captchaInput.required = true;
                // Refresh captcha image
                document.getElementById('captchaImage').src = 'captcha?' + new Date().getTime();
            } else {
                captchaSection.style.display = 'none';
                captchaInput.required = false;
            }
        });
    </script>
</body>
</html>

