<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CAPTCHA Test</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 400px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        h2 {
            color: #333;
            margin-bottom: 20px;
        }
        .captcha-image {
            border: 2px solid #ddd;
            border-radius: 4px;
            margin: 20px 0;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin: 5px;
        }
        button:hover {
            background-color: #0056b3;
        }
        .info {
            color: #666;
            font-size: 12px;
            margin-top: 20px;
        }
    </style>
    <script>
        function refreshCaptcha() {
            var img = document.getElementById('captchaImage');
            img.src = 'captcha?' + Math.random();
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>CAPTCHA Test Page</h2>
        <p>This page demonstrates the CAPTCHA functionality using Kaptcha 2.3.2</p>
        
        <img id="captchaImage" src="captcha" alt="CAPTCHA" class="captcha-image">
        
        <div>
            <button onclick="refreshCaptcha()">Refresh CAPTCHA</button>
            <button onclick="window.location.href='contactFeedback'">Go to Feedback Form</button>
        </div>
        
        <div class="info">
            <p><strong>Note:</strong> This implementation uses Kaptcha 2.3.2, which is an obsolete library with known security vulnerabilities.</p>
        </div>
    </div>
</body>
</html>

