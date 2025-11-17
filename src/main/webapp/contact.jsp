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
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h2 {
            color: #333;
            margin-bottom: 20px;
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
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        textarea {
            height: 150px;
            resize: vertical;
        }
        .optional {
            color: #888;
            font-weight: normal;
            font-size: 12px;
        }
        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
        }
        input[type="submit"]:hover {
            background-color: #45a049;
        }
        .back-link {
            display: inline-block;
            margin-top: 15px;
            color: #4CAF50;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Contact Feedback Form</h2>
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
                <label for="message">Message:</label>
                <textarea id="message" name="message" required></textarea>
            </div>
            
            <div class="form-group">
                <label for="captcha">
                    CAPTCHA <span class="optional">(Optional - Enter numeric verification code)</span>:
                </label>
                <input type="text" id="captcha" name="captcha" placeholder="Enter verification code">
            </div>
            
            <div class="form-group">
                <label for="filename">
                    Save As <span class="optional">(Optional - Custom filename for backup)</span>:
                </label>
                <input type="text" id="filename" name="filename" placeholder="feedback.txt">
            </div>
            
            <input type="submit" value="Submit Feedback">
        </form>
        <a href="index.jsp" class="back-link">&larr; Back to Home</a>
    </div>
</body>
</html>

