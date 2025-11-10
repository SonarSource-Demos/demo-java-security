package demo.security.util;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    Connection connection;
    public DBUtils() throws SQLException {
        connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
    }

    public List<String> findUsers(String user) throws Exception {
        String query = "SELECT userid FROM users WHERE username = '" + user  + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> users = new ArrayList<String>();
        while (resultSet.next()){
            users.add(resultSet.getString(0));
        }
        return users;
    }

    public List<String> findItem(String itemId) throws Exception {
        String query = "SELECT item_id FROM items WHERE item_id = '" + itemId  + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> items = new ArrayList<String>();
        while (resultSet.next()){
            items.add(resultSet.getString(0));
        }
        return items;
    }

    // SQL Injection vulnerability: storing feedback without parameterized queries
    public void storeFeedback(String name, String email, String subject, String message, String attachment) throws Exception {
        String query = "INSERT INTO feedback (name, email, subject, message, attachment, submitted_at) VALUES ('" 
                + name + "', '" + email + "', '" + subject + "', '" + message + "', '" + attachment + "', NOW())";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    // SQL Injection vulnerability: searching feedback without parameterized queries
    public List<String> searchFeedback(String searchTerm) throws Exception {
        String query = "SELECT name, email, subject, message FROM feedback WHERE message LIKE '%" + searchTerm + "%'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> results = new ArrayList<String>();
        while (resultSet.next()){
            String result = "Name: " + resultSet.getString("name") + 
                          ", Email: " + resultSet.getString("email") + 
                          ", Subject: " + resultSet.getString("subject") + 
                          ", Message: " + resultSet.getString("message");
            results.add(result);
        }
        return results;
    }

    // SQL Injection vulnerability: getting feedback by ID
    public String getFeedbackById(String feedbackId) throws Exception {
        String query = "SELECT name, email, subject, message, attachment FROM feedback WHERE id = " + feedbackId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()){
            return "Name: " + resultSet.getString("name") + 
                   "<br>Email: " + resultSet.getString("email") + 
                   "<br>Subject: " + resultSet.getString("subject") + 
                   "<br>Message: " + resultSet.getString("message") + 
                   "<br>Attachment: " + resultSet.getString("attachment");
        }
        return null;
    }
}
