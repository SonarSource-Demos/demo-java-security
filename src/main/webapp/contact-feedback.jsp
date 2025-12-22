<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
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
        .form-container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
            color: #555;
            font-weight: bold;
        }
        input[type="text"],
        input[type="email"],
        select,
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        .captcha-container {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 15px;
        }
        .captcha-image {
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .refresh-captcha {
            cursor: pointer;
            color: #007bff;
            text-decoration: underline;
            font-size: 12px;
        }
        input[type="checkbox"] {
            margin-right: 5px;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            width: 100%;
        }
        button:hover {
            background-color: #0056b3;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
    </style>
    <script>
        function toggleCaptcha() {
            var captchaEnabled = document.getElementById('captcha_enabled').checked;
            var captchaSection = document.getElementById('captcha-section');
            captchaSection.style.display = captchaEnabled ? 'block' : 'none';
            
            // Update hidden field
            document.getElementById('captcha_enabled_hidden').value = captchaEnabled;
        }
        
        function refreshCaptcha() {
            document.getElementById('captcha-img').src = '/captcha?' + new Date().getTime();
        }
        
        function validateForm() {
            var name = document.getElementById('name').value;
            var email = document.getElementById('email').value;
            var subject = document.getElementById('subject').value;
            var message = document.getElementById('message').value;
            
            if (!name || !email || !subject || !message) {
                alert('Please fill in all required fields');
                return false;
            }
            
            var captchaEnabled = document.getElementById('captcha_enabled').checked;
            if (captchaEnabled) {
                var captcha = document.getElementById('captcha').value;
                if (!captcha) {
                    alert('Please enter the CAPTCHA code');
                    return false;
                }
            }
            
            return true;
        }
    </script>
</head>
<body>
    <div class="form-container">
        <h1>Contact Feedback Form</h1>
        
        <% 
        String success = request.getParameter("success");
        String token = request.getParameter("token");
        if ("true".equals(success)) {
        %>
            <div class="success-message">
                Thank you for your feedback! Your submission token is: <%= token %>
            </div>
        <% } %>
        
        <form action="/contact-feedback" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="name">Name: *</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email: *</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="subject">Subject: *</label>
                <input type="text" id="subject" name="subject" required>
            </div>
            
            <div class="form-group">
                <label for="message">Message: *</label>
                <textarea id="message" name="message" required></textarea>
            </div>
            
            <div class="form-group">
                <label for="priority">Priority:</label>
                <select id="priority" name="priority">
                    <option value="low">Low</option>
                    <option value="medium" selected>Medium</option>
                    <option value="high">High</option>
                    <option value="urgent">Urgent</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>
                    <input type="checkbox" id="captcha_enabled" onchange="toggleCaptcha()">
                    Enable CAPTCHA validation
                </label>
            </div>
            
            <input type="hidden" id="captcha_enabled_hidden" name="captcha_enabled" value="false">
            
            <div id="captcha-section" style="display: none;">
                <div class="form-group">
                    <label>CAPTCHA:</label>
                    <div class="captcha-container">
                        <img id="captcha-img" src="/captcha" alt="CAPTCHA" class="captcha-image" width="200" height="50">
                        <a class="refresh-captcha" onclick="refreshCaptcha()">Refresh</a>
                    </div>
                    <input type="text" id="captcha" name="captcha" placeholder="Enter CAPTCHA code">
                </div>
            </div>
            
            <button type="submit">Submit Feedback</button>
        </form>
    </div>
</body>
</html>
