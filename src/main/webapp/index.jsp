<html>
<head>
    <title>Java Security Demo</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .demo-links { margin: 20px 0; }
        .demo-links a { 
            display: block; margin: 10px 0; padding: 10px 15px; 
            background-color: #f8f9fa; border: 1px solid #ddd; 
            text-decoration: none; color: #007cba; border-radius: 4px; 
        }
        .demo-links a:hover { background-color: #e9ecef; }
        .warning { 
            background-color: #fff3cd; border: 1px solid #ffeaa7; 
            padding: 15px; margin: 20px 0; border-radius: 4px; 
        }
    </style>
</head>
<body>
    <h1>Java Security Demo Application</h1>
    <p>This application demonstrates various security vulnerabilities for educational purposes.</p>
    
    <div class="warning">
        <strong>⚠️ WARNING:</strong> This application contains intentional security vulnerabilities. 
        Do not deploy in production environments!
    </div>

    <h2>Vulnerability Demonstrations:</h2>
    <div class="demo-links">
        <a href="helloWorld?name=test">XSS Demo - Hello World Servlet</a>
        <a href="users?username=admin">SQL Injection Demo - User Search</a>
        <a href="contact.jsp">Contact Feedback Form (Multiple Vulnerabilities)</a>
        <a href="user.jsp">User Management Page</a>
    </div>

    <h2>Original Test Form:</h2>
    <form action="helloWorld" method="post">
        Enter Name: <input type="text" name="name" size="20" placeholder="Try: <script>alert('XSS')</script>">
        <input type="submit" value="Submit" />
    </form>

    <h2>Quick Vulnerability Tests:</h2>
    <div class="demo-links">
        <a href="users?username=' OR '1'='1">SQL Injection Test (Users)</a>
        <a href="helloWorld?name=<script>alert('XSS')</script>">XSS Test (Hello World)</a>
        <a href="contact?action=search&q=' UNION SELECT 'hack','admin@evil.com','pwned','compromised'--">SQL Injection Test (Contact Search)</a>
        <a href="contact?action=view&id=1 UNION SELECT 'hacked','evil@hacker.com','security','breached','/etc/passwd'">SQL Injection Test (View Feedback)</a>
    </div>
</body>
</html>