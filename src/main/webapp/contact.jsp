<!DOCTYPE html>
<html>
<head>
    <title>Contact Feedback Form</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .form-container { max-width: 600px; margin: 0 auto; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="email"], select, textarea { 
            width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; 
        }
        textarea { height: 100px; resize: vertical; }
        .submit-btn { 
            background-color: #007bff; color: white; padding: 10px 20px; 
            border: none; border-radius: 4px; cursor: pointer; font-size: 16px;
        }
        .submit-btn:hover { background-color: #0056b3; }
        .advanced-options { 
            margin-top: 20px; padding: 15px; background-color: #f8f9fa; 
            border-radius: 4px; border-left: 4px solid #17a2b8; 
        }
        .advanced-options h3 { margin-top: 0; color: #17a2b8; }
        .small-text { font-size: 12px; color: #6c757d; }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Contact Feedback Form</h1>
        <p>Please fill out this form to provide feedback or contact us.</p>
        
        <form action="contact" method="post">
            <div class="form-group">
                <label for="name">Name *</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="message">Message *</label>
                <textarea id="message" name="message" placeholder="Please describe your feedback or inquiry..." required></textarea>
            </div>
            
            <div class="form-group">
                <label for="priority">Priority</label>
                <select id="priority" name="priority">
                    <option value="low">Low</option>
                    <option value="medium" selected>Medium</option>
                    <option value="high">High</option>
                    <option value="urgent">Urgent</option>
                </select>
            </div>
            
            <div class="advanced-options">
                <h3>Advanced Options</h3>
                
                <div class="form-group">
                    <label for="attachmentPath">Attachment File Path</label>
                    <input type="text" id="attachmentPath" name="attachmentPath" 
                           placeholder="e.g., /path/to/your/file.txt">
                    <small class="small-text">Enter the full path to a file you want to attach (for demo purposes)</small>
                </div>
                
                <div class="form-group">
                    <label for="userPrefs">User Preferences (Base64 encoded)</label>
                    <input type="text" id="userPrefs" name="userPrefs" 
                           placeholder="Base64 encoded preferences data">
                    <small class="small-text">Advanced: Serialized user preferences</small>
                </div>
                
                <div class="form-group">
                    <label for="xmlFeedback">XML Feedback Data</label>
                    <textarea id="xmlFeedback" name="xmlFeedback" 
                              placeholder="&lt;feedback&gt;&lt;rating&gt;5&lt;/rating&gt;&lt;/feedback&gt;"></textarea>
                    <small class="small-text">Optional: XML structured feedback data</small>
                </div>
            </div>
            
            <div class="form-group" style="margin-top: 20px;">
                <button type="submit" class="submit-btn">Submit Feedback</button>
            </div>
        </form>
        
        <hr style="margin: 30px 0;">
        
        <h2>View Existing Feedback</h2>
        <form action="contact" method="get">
            <div class="form-group">
                <label for="feedbackId">Feedback ID</label>
                <input type="text" id="feedbackId" name="feedbackId" 
                       placeholder="Enter feedback ID to view">
                <button type="submit" class="submit-btn" style="margin-top: 10px;">View Feedback</button>
            </div>
        </form>
        
        <p style="margin-top: 30px;">
            <a href="index.jsp">← Back to Home</a> | 
            <a href="user.jsp">User Search</a>
        </p>
    </div>
</body>
</html>
