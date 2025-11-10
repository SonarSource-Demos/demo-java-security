<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Contact Feedback Form</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .form-container { max-width: 600px; margin: 0 auto; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="email"], textarea, select { 
            width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; 
        }
        textarea { height: 120px; resize: vertical; }
        button { padding: 10px 20px; background-color: #007cba; color: white; 
                border: none; border-radius: 4px; cursor: pointer; margin-right: 10px; }
        button:hover { background-color: #005a87; }
        .vulnerability-demo { 
            background-color: #fff3cd; border: 1px solid #ffeaa7; 
            padding: 15px; margin: 20px 0; border-radius: 4px; 
        }
        .error { color: red; font-weight: bold; }
        .search-section { margin-top: 30px; padding-top: 20px; border-top: 2px solid #eee; }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Contact Feedback Form</h1>
        <p>Please fill out this form to send us your feedback or questions.</p>
        
        <% 
            String error = request.getParameter("error");
            if (error != null) { 
        %>
            <div class="error">Error: <%= error %></div>
        <% } %>

        <!-- Main Contact Form -->
        <form action="contact" method="POST">
            <input type="hidden" name="action" value="submit">
            
            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" maxlength="1000">
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" maxlength="1000">
            </div>
            
            <div class="form-group">
                <label for="subject">Subject:</label>
                <input type="text" id="subject" name="subject" maxlength="2000">
            </div>
            
            <div class="form-group">
                <label for="message">Message:</label>
                <textarea id="message" name="message" maxlength="10000"></textarea>
            </div>
            
            <div class="form-group">
                <label for="attachment">Attachment Path (optional):</label>
                <input type="text" id="attachment" name="attachment" placeholder="/path/to/file" maxlength="500">
            </div>
            
            <button type="submit">Submit Feedback</button>
        </form>

        <!-- Search Feedback Section -->
        <div class="search-section">
            <h2>Search Previous Feedback</h2>
            <form action="contact" method="GET">
                <input type="hidden" name="action" value="search">
                <div class="form-group">
                    <label for="search">Search Term:</label>
                    <input type="text" id="search" name="q" placeholder="Enter keywords to search feedback...">
                </div>
                <button type="submit">Search</button>
            </form>
        </div>

        <!-- View Specific Feedback -->
        <div class="search-section">
            <h2>View Feedback by ID</h2>
            <form action="contact" method="GET">
                <input type="hidden" name="action" value="view">
                <div class="form-group">
                    <label for="feedbackId">Feedback ID:</label>
                    <input type="text" id="feedbackId" name="id" placeholder="Enter feedback ID (e.g., 1, 2, 3...)">
                </div>
                <button type="submit">View Feedback</button>
            </form>
        </div>

        <!-- File Upload Section -->
        <div class="search-section">
            <h2>Upload File</h2>
            <form action="contact" method="POST">
                <input type="hidden" name="action" value="upload">
                <div class="form-group">
                    <label for="filename">File Name:</label>
                    <input type="text" id="filename" name="filename" placeholder="Enter filename (e.g., feedback.txt, ../../../etc/passwd)">
                </div>
                <div class="form-group">
                    <label for="content">File Content:</label>
                    <textarea id="content" name="content" placeholder="Enter file content..."></textarea>
                </div>
                <button type="submit">Upload File</button>
            </form>
        </div>

        <!-- Session Processing Section -->
        <div class="search-section">
            <h2>Process Session Data</h2>
            <form action="contact" method="POST">
                <input type="hidden" name="action" value="process_session">
                <div class="form-group">
                    <label for="sessionData">Base64 Encoded Session Data:</label>
                    <input type="text" id="sessionData" name="session_data" placeholder="Enter base64 encoded session data...">
                </div>
                <button type="submit">Process Session</button>
            </form>
        </div>

        <!-- Vulnerability Demonstration Notice -->
        <div class="vulnerability-demo">
            <h3>⚠️ Security Vulnerability Demonstration</h3>
            <p>This form intentionally contains multiple security vulnerabilities for educational purposes:</p>
            <ul>
                <li><strong>SQL Injection:</strong> User inputs are directly concatenated into SQL queries</li>
                <li><strong>Cross-Site Scripting (XSS):</strong> User inputs are displayed without proper HTML escaping</li>
                <li><strong>Path Traversal:</strong> File upload allows arbitrary file paths</li>
                <li><strong>Deserialization:</strong> Session data is deserialized without validation</li>
                <li><strong>Information Disclosure:</strong> Error messages expose system details</li>
                <li><strong>Input Validation:</strong> No proper validation of input lengths or content</li>
            </ul>
            <p><strong>DO NOT USE IN PRODUCTION!</strong> These vulnerabilities can be exploited by attackers.</p>
        </div>

        <!-- Example Payloads for Testing -->
        <div class="vulnerability-demo">
            <h3>Example Test Payloads</h3>
            <p><strong>XSS Test:</strong></p>
            <code>&lt;script&gt;alert('XSS')&lt;/script&gt;</code>
            
            <p><strong>SQL Injection Test (Search):</strong></p>
            <code>' UNION SELECT username, password, 'admin', 'leaked' FROM users--</code>
            
            <p><strong>Path Traversal Test (File Upload):</strong></p>
            <code>../../../etc/passwd</code>
            
            <p><strong>SQL Injection Test (Feedback ID):</strong></p>
            <code>1 UNION SELECT 'hacked', 'admin@evil.com', 'pwned', 'database compromised', '/etc/shadow'</code>
        </div>

        <div style="margin-top: 30px;">
            <a href="index.jsp">← Back to Home</a>
        </div>
    </div>
</body>
</html>
