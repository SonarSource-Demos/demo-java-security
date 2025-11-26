<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Contact Feedback Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: bold;
        }
        input[type="text"],
        input[type="email"],
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
        .checkbox-group {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }
        .checkbox-group input[type="checkbox"] {
            width: auto;
            margin-right: 8px;
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
        .back-link {
            display: block;
            text-align: center;
            margin-top: 15px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
    <script>
        function toggleCaptcha() {
            var checkbox = document.getElementById('useCaptcha');
            var captchaDiv = document.getElementById('captchaSection');
            if (checkbox.checked) {
                captchaDiv.style.display = 'block';
                refreshCaptcha();
            } else {
                captchaDiv.style.display = 'none';
            }
        }
        
        function refreshCaptcha() {
            var img = document.getElementById('captchaImage');
            img.src = 'captcha?' + Math.random();
        }
        
        window.onload = function() {
            toggleCaptcha();
        };
    </script>
</head>
<body>
    <div class="container">
        <h2>Contact Feedback Form</h2>
        <p>Please share your feedback with us. We value your opinion!</p>
        
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
                <label for="message">Message:</label>
                <textarea id="message" name="message" required></textarea>
            </div>
            
            <div class="checkbox-group">
                <input type="checkbox" id="useCaptcha" name="useCaptcha" value="true" onchange="toggleCaptcha()" checked>
                <label for="useCaptcha" style="margin-bottom: 0;">Enable CAPTCHA verification (recommended)</label>
            </div>
            
            <div id="captchaSection" style="display:none;">
                <div class="captcha-container">
                    <img id="captchaImage" src="" alt="CAPTCHA" class="captcha-image">
                    <a href="javascript:void(0);" onclick="refreshCaptcha()" class="refresh-captcha">Refresh</a>
                </div>
                <div class="form-group">
                    <label for="captcha">Enter CAPTCHA:</label>
                    <input type="text" id="captcha" name="captcha" autocomplete="off">
                </div>
            </div>
            
            <button type="submit">Submit Feedback</button>
        </form>
        
        <a href="index.jsp" class="back-link">Back to Home</a>
    </div>
</body>
</html>

