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
        .form-container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
        textarea,
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        textarea {
            min-height: 100px;
            resize: vertical;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
        }
        button:hover {
            background-color: #45a049;
        }
        .search-section {
            margin-top: 30px;
            padding-top: 30px;
            border-top: 2px solid #eee;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Contact Feedback Form</h2>
        
        <form action="contact-feedback" method="post">
            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="feedback">Your Feedback:</label>
                <textarea id="feedback" name="feedback" required></textarea>
            </div>
            
            <div class="form-group">
                <label for="rating">Rating:</label>
                <select id="rating" name="rating">
                    <option value="1">1 - Poor</option>
                    <option value="2">2 - Fair</option>
                    <option value="3">3 - Good</option>
                    <option value="4">4 - Very Good</option>
                    <option value="5" selected>5 - Excellent</option>
                </select>
            </div>
            
            <button type="submit">Submit Feedback</button>
        </form>
        
        <div class="search-section">
            <h3>Search Feedback</h3>
            <form action="contact-feedback" method="get">
                <div class="form-group">
                    <label for="search">Search by Email:</label>
                    <input type="text" id="search" name="search">
                </div>
                <button type="submit">Search</button>
            </form>
        </div>
    </div>
</body>
</html>
