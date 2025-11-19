<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Contact Feedback Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }
        .form-section {
            margin-bottom: 30px;
        }
        h2 {
            color: #4CAF50;
            font-size: 1.3em;
            margin-top: 20px;
        }
        label {
            display: block;
            margin: 10px 0 5px;
            font-weight: bold;
            color: #555;
        }
        input[type="text"],
        input[type="email"],
        textarea,
        select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            min-height: 100px;
            resize: vertical;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
            margin-right: 10px;
        }
        button:hover {
            background-color: #45a049;
        }
        .search-button {
            background-color: #2196F3;
        }
        .search-button:hover {
            background-color: #0b7dda;
        }
        .export-button {
            background-color: #ff9800;
        }
        .export-button:hover {
            background-color: #e68900;
        }
        .process-button {
            background-color: #9c27b0;
        }
        .process-button:hover {
            background-color: #7b1fa2;
        }
        .load-button {
            background-color: #607d8b;
        }
        .load-button:hover {
            background-color: #455a64;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Contact Feedback Form</h1>
        <p>Welcome to our feedback system. Please use the forms below to submit, search, export, or process feedback.</p>

        <div class="form-section">
            <h2>Submit Feedback</h2>
            <form action="/contact" method="post">
                <input type="hidden" name="action" value="submit">
                
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required>
                
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
                
                <label for="category">Category:</label>
                <select id="category" name="category">
                    <option value="general">General</option>
                    <option value="technical">Technical Support</option>
                    <option value="billing">Billing</option>
                    <option value="feature">Feature Request</option>
                </select>
                
                <label for="message">Message:</label>
                <textarea id="message" name="message" required></textarea>
                
                <button type="submit">Submit Feedback</button>
            </form>
        </div>

        <div class="form-section">
            <h2>Search Feedback</h2>
            <form action="/contact" method="post">
                <input type="hidden" name="action" value="search">
                
                <label for="search">Search Term:</label>
                <input type="text" id="search" name="search" placeholder="Enter keyword to search...">
                
                <button type="submit" class="search-button">Search</button>
            </form>
        </div>

        <div class="form-section">
            <h2>Export Feedback</h2>
            <form action="/contact" method="post">
                <input type="hidden" name="action" value="export">
                
                <label for="filename">Filename:</label>
                <input type="text" id="filename" name="filename" placeholder="feedback_export.csv">
                
                <button type="submit" class="export-button">Export</button>
            </form>
        </div>

        <div class="form-section">
            <h2>Process Feedback</h2>
            <form action="/contact" method="post">
                <input type="hidden" name="action" value="process">
                
                <label for="id">Feedback ID:</label>
                <input type="text" id="id" name="id" placeholder="12345">
                
                <label for="processor">Processor Type:</label>
                <select id="processor" name="processor">
                    <option value="sentiment">Sentiment Analysis</option>
                    <option value="category">Auto-Categorize</option>
                    <option value="priority">Priority Assessment</option>
                </select>
                
                <button type="submit" class="process-button">Process</button>
            </form>
        </div>

        <div class="form-section">
            <h2>Load Feedback Data</h2>
            <form action="/contact" method="post">
                <input type="hidden" name="action" value="load">
                
                <label for="data">Data (Base64 encoded):</label>
                <textarea id="data" name="data" placeholder="Enter base64 encoded feedback data..."></textarea>
                
                <button type="submit" class="load-button">Load Data</button>
            </form>
        </div>
    </div>
</body>
</html>

