<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Contact Feedback Form - Security Demo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 3px solid #007bff;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        input[type="text"],
        input[type="email"],
        input[type="file"],
        textarea,
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            min-height: 100px;
            resize: vertical;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
        }
        button:hover {
            background-color: #0056b3;
        }
        .section {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        .warning {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            border-radius: 4px;
            padding: 15px;
            margin-bottom: 20px;
        }
        .warning strong {
            color: #856404;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="warning">
            <strong>⚠️ Security Demo Application</strong><br>
            This application intentionally contains security vulnerabilities for demonstration purposes.
            Do not use in production!
        </div>

        <h1>Contact Feedback Form</h1>
        
        <!-- Feedback Submission Form -->
        <form action="contact" method="post">
            <input type="hidden" name="action" value="submit">
            
            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="category">Category:</label>
                <select id="category" name="category">
                    <option value="general">General Inquiry</option>
                    <option value="support">Technical Support</option>
                    <option value="billing">Billing Question</option>
                    <option value="feature">Feature Request</option>
                    <option value="bug">Bug Report</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="feedback">Your Feedback:</label>
                <textarea id="feedback" name="feedback" required></textarea>
            </div>
            
            <button type="submit">Submit Feedback</button>
        </form>

        <!-- Search Feedback -->
        <div class="section">
            <h2>Search Feedback</h2>
            <form action="contact" method="post">
                <input type="hidden" name="action" value="search">
                
                <div class="form-group">
                    <label for="search">Search by category or name:</label>
                    <input type="text" id="search" name="search">
                </div>
                
                <button type="submit">Search</button>
            </form>
        </div>

        <!-- Export Feedback -->
        <div class="section">
            <h2>Export Feedback</h2>
            <form action="contact" method="post">
                <input type="hidden" name="action" value="export">
                
                <div class="form-group">
                    <label for="filename">Export Filename:</label>
                    <input type="text" id="filename" name="filename" placeholder="feedback_export">
                </div>
                
                <div class="form-group">
                    <label for="format">Format:</label>
                    <select id="format" name="format">
                        <option value="csv">CSV</option>
                        <option value="xml">XML</option>
                        <option value="json">JSON</option>
                    </select>
                </div>
                
                <button type="submit">Export</button>
            </form>
        </div>

        <!-- File Upload -->
        <div class="section">
            <h2>Upload Attachment</h2>
            <form action="contact" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="upload">
                
                <div class="form-group">
                    <label for="file">Choose file:</label>
                    <input type="file" id="file" name="file">
                </div>
                
                <button type="submit">Upload</button>
            </form>
        </div>

        <!-- Process XML -->
        <div class="section">
            <h2>Process XML Data</h2>
            <form action="contact" method="post">
                <input type="hidden" name="action" value="process">
                
                <div class="form-group">
                    <label for="xmldata">XML Data:</label>
                    <textarea id="xmldata" name="xmldata" placeholder="<root><data>value</data></root>"></textarea>
                </div>
                
                <button type="submit">Process XML</button>
            </form>
        </div>

        <div class="section">
            <p><a href="index.jsp">← Back to Home</a></p>
        </div>
    </div>
</body>
</html>

