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
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
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
            font-weight: bold;
            color: #555;
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
            min-height: 120px;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            width: 100%;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .search-section {
            margin-top: 30px;
            padding-top: 30px;
            border-top: 2px solid #ddd;
        }
        .info {
            background-color: #e7f3ff;
            padding: 15px;
            border-left: 4px solid #007bff;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Contact Feedback Form</h1>
        
        <div class="info">
            <p><strong>Note:</strong> This is a security demonstration project. This form intentionally contains vulnerabilities for educational purposes.</p>
        </div>
        
        <form action="<%= request.getContextPath() %>/contact/feedback" method="POST">
            <div class="form-group">
                <label for="name">Name *</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="subject">Subject</label>
                <input type="text" id="subject" name="subject">
            </div>
            
            <div class="form-group">
                <label for="category">Category</label>
                <select id="category" name="category">
                    <option value="general">General Inquiry</option>
                    <option value="bug">Bug Report</option>
                    <option value="feature">Feature Request</option>
                    <option value="security">Security Concern</option>
                    <option value="other">Other</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="priority">Priority</label>
                <select id="priority" name="priority">
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                    <option value="urgent">Urgent</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="message">Message *</label>
                <textarea id="message" name="message" required></textarea>
            </div>
            
            <div class="form-group">
                <label for="filename">Custom Filename (optional)</label>
                <input type="text" id="filename" name="filename" placeholder="Leave blank for auto-generated">
            </div>
            
            <button type="submit" class="btn">Submit Feedback</button>
        </form>
        
        <div class="search-section">
            <h2>Search Feedback</h2>
            <form action="<%= request.getContextPath() %>/contact/feedback" method="GET">
                <input type="hidden" name="action" value="search">
                <div class="form-group">
                    <label for="searchTerm">Search Term</label>
                    <input type="text" id="searchTerm" name="term">
                </div>
                <button type="submit" class="btn">Search</button>
            </form>
        </div>
        
        <div class="search-section">
            <h2>View Feedback</h2>
            <form action="<%= request.getContextPath() %>/contact/feedback" method="GET">
                <input type="hidden" name="action" value="view">
                <div class="form-group">
                    <label for="feedbackId">Feedback ID</label>
                    <input type="text" id="feedbackId" name="id">
                </div>
                <button type="submit" class="btn">View</button>
            </form>
        </div>
    </div>
    
    <script>
        // Add some client-side functionality (also intentionally vulnerable for demo)
        document.addEventListener('DOMContentLoaded', function() {
            // XSS vulnerability - using innerHTML with URL parameters
            var params = new URLSearchParams(window.location.search);
            var message = params.get('message');
            if (message) {
                var div = document.createElement('div');
                div.innerHTML = message; // XSS vulnerability
                document.body.insertBefore(div, document.body.firstChild);
            }
        });
    </script>
</body>
</html>

