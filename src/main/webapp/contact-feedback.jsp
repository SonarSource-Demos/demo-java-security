<html>
<head>
    <title>Contact Feedback Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="email"], textarea, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        textarea {
            height: 100px;
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
        }
        button:hover {
            background-color: #45a049;
        }
        .search-section {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 2px solid #ccc;
        }
    </style>
</head>
<body>
    <h1>Contact Feedback Form</h1>
    
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
            <label for="category">Category:</label>
            <select id="category" name="category" required>
                <option value="">Select a category</option>
                <option value="General">General</option>
                <option value="Technical">Technical</option>
                <option value="Billing">Billing</option>
                <option value="Feature Request">Feature Request</option>
                <option value="Bug Report">Bug Report</option>
            </select>
        </div>
        
        <div class="form-group">
            <label for="feedback">Feedback:</label>
            <textarea id="feedback" name="feedback" required></textarea>
        </div>
        
        <button type="submit">Submit Feedback</button>
    </form>
    
    <div class="search-section">
        <h2>Search Feedback</h2>
        
        <h3>Search by Email</h3>
        <form action="contact-feedback" method="get">
            <div class="form-group">
                <label for="search-email">Email:</label>
                <input type="text" id="search-email" name="email">
            </div>
            <button type="submit">Search by Email</button>
        </form>
        
        <h3>Search by Category</h3>
        <form action="contact-feedback" method="get">
            <div class="form-group">
                <label for="search-category">Category:</label>
                <input type="text" id="search-category" name="category">
            </div>
            <button type="submit">Search by Category</button>
        </form>
    </div>
</body>
</html>

