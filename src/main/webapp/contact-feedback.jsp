<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Contact Feedback Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f4f4f4;
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
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        .captcha-container {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
        }
        .captcha-image {
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .captcha-input {
            width: 150px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            width: 100%;
        }
        button:hover {
            background-color: #45a049;
        }
        .note {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 12px;
            margin-bottom: 20px;
            color: #856404;
        }
        .debug-panel {
            margin-top: 20px;
            padding: 10px;
            background-color: #f8f9fa;
            border: 1px dashed #ccc;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Contact Feedback Form</h1>
        
        <div class="note">
            <strong>Demo Security Note:</strong> This form demonstrates various security vulnerabilities 
            for educational purposes, including SQL injection, XSS, CSRF, weak CAPTCHA, and more.
        </div>
        
        <form action="/contact-feedback" method="post" enctype="multipart/form-data">
            <!-- CSRF vulnerability - no CSRF token -->
            
            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="message">Message:</label>
                <textarea id="message" name="message" required></textarea>
            </div>
            
            <div class="form-group">
                <label for="attachment">Attachment (optional):</label>
                <input type="file" id="attachment" name="attachment">
            </div>
            
            <div class="captcha-container">
                <img src="/captcha" alt="CAPTCHA" class="captcha-image" id="captchaImage">
                <input type="text" name="captcha" placeholder="Enter CAPTCHA" class="captcha-input">
                <a href="javascript:void(0)" onclick="refreshCaptcha()">Refresh</a>
            </div>
            
            <!-- Debug option to skip CAPTCHA (security vulnerability) -->
            <div class="form-group" style="display:none;">
                <label>
                    <input type="checkbox" name="skipCaptcha" value="true">
                    Skip CAPTCHA (Debug Mode)
                </label>
            </div>
            
            <button type="submit">Submit Feedback</button>
        </form>
        
        <div class="debug-panel">
            <strong>Vulnerabilities in this form:</strong>
            <ul>
                <li>SQL Injection in feedback storage</li>
                <li>XSS in output rendering</li>
                <li>CSRF - no token protection</li>
                <li>Obsolete CAPTCHA library (kaptcha 0.0.9) with CVEs</li>
                <li>Path Traversal in file upload</li>
                <li>No file type validation</li>
                <li>Weak encryption (DES)</li>
                <li>Predictable tokens (java.util.Random)</li>
                <li>Information disclosure</li>
                <li>Hard-coded credentials</li>
            </ul>
        </div>
        
        <div style="margin-top: 20px; text-align: center;">
            <a href="/">Back to Home</a>
        </div>
    </div>
    
    <script>
        function refreshCaptcha() {
            var img = document.getElementById('captchaImage');
            img.src = '/captcha?' + Math.random();
        }
    </script>
</body>
</html>

