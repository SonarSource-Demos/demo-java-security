<html>
<head>
    <title>Contact Us - Security Demo</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
        .container { background-color: white; padding: 30px; border-radius: 8px; max-width: 600px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="email"], textarea { 
            width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; 
        }
        textarea { height: 120px; resize: vertical; }
        .submit-btn { 
            background-color: #007cba; color: white; padding: 10px 20px; 
            border: none; border-radius: 4px; cursor: pointer; font-size: 16px;
        }
        .submit-btn:hover { background-color: #005a87; }
        .nav-links { margin-bottom: 20px; }
        .nav-links a { margin-right: 15px; text-decoration: none; color: #007cba; }
    </style>
</head>
<body>
    <div class="container">
        <div class="nav-links">
            <a href="index.jsp">← Home</a>
            <a href="user.jsp">Find Users</a>
        </div>
        
        <h2>Contact Us - Send Feedback</h2>
        <p>We'd love to hear from you! Please fill out the form below to send us your feedback.</p>
        
        <form action="contact" method="post">
            <div class="form-group">
                <label for="name">Your Name:</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Your Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="subject">Subject:</label>
                <input type="text" id="subject" name="subject" required>
            </div>
            
            <div class="form-group">
                <label for="message">Message:</label>
                <textarea id="message" name="message" placeholder="Please share your feedback, suggestions, or questions..." required></textarea>
            </div>
            
            <div class="form-group">
                <button type="submit" class="submit-btn">Send Feedback</button>
            </div>
        </form>
        
        <!-- Admin link (intentionally visible for demo purposes) -->
        <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #666;">
            <a href="admin/feedback">View All Feedback (Admin)</a>
        </div>
    </div>
</body>
</html>
