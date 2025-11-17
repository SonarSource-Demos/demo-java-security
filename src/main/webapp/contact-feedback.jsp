<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="demo.security.util.ContactFeedbackUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Contact Feedback Form</title>
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
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 20px;
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
            min-height: 150px;
            resize: vertical;
        }
        .submit-btn {
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
        }
        .submit-btn:hover {
            background-color: #0056b3;
        }
        .captcha-section {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        .captcha-input {
            flex: 1;
        }
        .links {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
        }
        .links a {
            color: #007bff;
            text-decoration: none;
            margin-right: 15px;
        }
        .links a:hover {
            text-decoration: underline;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .search-results {
            margin-top: 30px;
        }
        .feedback-item {
            border: 1px solid #ddd;
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 4px;
            background-color: #fafafa;
        }
        .feedback-item h3 {
            margin-top: 0;
            color: #007bff;
        }
        .feedback-actions {
            margin-top: 10px;
        }
        .feedback-actions a {
            color: #dc3545;
            text-decoration: none;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <%
            String action = request.getParameter("action");
            String message = request.getParameter("msg");
            String error = request.getParameter("error");
            
            // XSS vulnerability - directly outputting user parameters
            String userName = request.getParameter("name");
            String prefillMessage = request.getParameter("prefill");
        %>
        
        <% if (message != null) { %>
            <!-- XSS vulnerability - unescaped output -->
            <div class="alert alert-success"><%= message %></div>
        <% } %>
        
        <% if (error != null) { %>
            <!-- XSS vulnerability - unescaped output -->
            <div class="alert alert-error"><%= error %></div>
        <% } %>
        
        <h1>Contact Feedback Form</h1>
        
        <% if ("search".equals(action)) { %>
            <!-- Search Section -->
            <h2>Search Feedback</h2>
            <form method="get" action="contact-feedback.jsp">
                <input type="hidden" name="action" value="search"/>
                <div class="form-group">
                    <input type="text" name="q" placeholder="Enter search term..." 
                           value="<%= request.getParameter("q") != null ? request.getParameter("q") : "" %>"/>
                </div>
                <button type="submit" class="submit-btn">Search</button>
            </form>
            
            <%
                String searchTerm = request.getParameter("q");
                if (searchTerm != null && !searchTerm.isEmpty()) {
                    try {
                        ContactFeedbackUtil util = new ContactFeedbackUtil();
                        List<ContactFeedbackUtil.Feedback> feedbacks = util.searchFeedback(searchTerm);
            %>
                        <div class="search-results">
                            <h2>Search Results for: <%= searchTerm %></h2>
                            <% if (feedbacks.isEmpty()) { %>
                                <p>No feedback found.</p>
                            <% } else { %>
                                <% for (ContactFeedbackUtil.Feedback feedback : feedbacks) { %>
                                    <div class="feedback-item">
                                        <h3><%= feedback.getName() %></h3>
                                        <p><strong>Email:</strong> <%= feedback.getEmail() %></p>
                                        <p><strong>Message:</strong> <%= feedback.getMessage() %></p>
                                        <div class="feedback-actions">
                                            <!-- CSRF vulnerability - no token for delete operation -->
                                            <a href="contact-feedback.jsp?action=delete&id=<%= feedback.getId() %>">Delete</a>
                                        </div>
                                    </div>
                                <% } %>
                            <% } %>
                        </div>
            <%
                        util.close();
                    } catch (Exception e) {
                        // Information disclosure - exposing exception details
                        out.println("<div class='alert alert-error'>Error: " + e.getMessage() + "</div>");
                    }
                }
            %>
            
        <% } else if ("delete".equals(action)) { %>
            <%
                // CSRF vulnerability - no token validation
                String feedbackId = request.getParameter("id");
                try {
                    ContactFeedbackUtil util = new ContactFeedbackUtil();
                    String result = util.deleteFeedback(feedbackId);
                    util.close();
            %>
                    <div class="alert alert-success"><%= result %></div>
            <%
                } catch (Exception e) {
                    out.println("<div class='alert alert-error'>Error: " + e.getMessage() + "</div>");
                }
            %>
            
        <% } else { %>
            <!-- Main Feedback Form -->
            <% if (userName != null) { %>
                <!-- XSS vulnerability - unescaped output -->
                <p style="color: #007bff; font-size: 18px;">Welcome back, <%= userName %>!</p>
            <% } %>
            
            <!-- CSRF vulnerability - no CSRF token -->
            <form method="post" action="/contact-feedback" enctype="multipart/form-data">
                <input type="hidden" name="action" value="submit"/>
                
                <div class="form-group">
                    <label for="name">Name *</label>
                    <!-- XSS vulnerability - unescaped value attribute -->
                    <input type="text" id="name" name="name" required 
                           value="<%= userName != null ? userName : "" %>"/>
                </div>
                
                <div class="form-group">
                    <label for="email">Email *</label>
                    <input type="email" id="email" name="email" required/>
                </div>
                
                <div class="form-group">
                    <label for="category">Category</label>
                    <input type="text" id="category" name="category" 
                           placeholder="e.g., Bug Report, Feature Request, General Feedback"/>
                </div>
                
                <div class="form-group">
                    <label for="message">Message *</label>
                    <!-- XSS vulnerability - unescaped textarea content -->
                    <textarea id="message" name="message" required><%= prefillMessage != null ? prefillMessage : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="attachment">Attachment (optional)</label>
                    <!-- File upload vulnerability - no validation -->
                    <input type="file" id="attachment" name="attachment"/>
                </div>
                
                <div class="form-group">
                    <label for="captcha">CAPTCHA *</label>
                    <div class="captcha-section">
                        <img src="/contact-feedback?action=captcha" alt="CAPTCHA" 
                             style="border: 1px solid #ddd; border-radius: 4px;"/>
                        <input type="text" id="captcha" name="captcha" 
                               class="captcha-input" placeholder="Enter CAPTCHA" required/>
                    </div>
                </div>
                
                <button type="submit" class="submit-btn">Submit Feedback</button>
            </form>
        <% } %>
        
        <div class="links">
            <a href="contact-feedback.jsp">New Feedback</a>
            <a href="contact-feedback.jsp?action=search">Search Feedback</a>
            <a href="index.jsp">Home</a>
        </div>
    </div>
    
    <script>
        // Client-side validation bypass - can be easily disabled
        document.querySelector('form')?.addEventListener('submit', function(e) {
            const email = document.getElementById('email')?.value;
            // Weak email validation on client side only
            if (email && !email.includes('@')) {
                alert('Please enter a valid email address');
                e.preventDefault();
            }
        });
    </script>
</body>
</html>

