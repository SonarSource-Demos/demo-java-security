package demo.security.util;

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

    // SQL Injection vulnerability - feedback search
    public List<String> findFeedback(String feedbackId) throws SQLException {
        String query = "SELECT feedback_text FROM feedback WHERE id = '" + feedbackId + "'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<String> feedbacks = new ArrayList<>();
            while (resultSet.next()){
                feedbacks.add(resultSet.getString(1));
            }
            return feedbacks;
        }
    }

    // SQL Injection vulnerability - store feedback
    public void storeFeedback(String name, String email, String message, String category) throws SQLException {
        String query = "INSERT INTO feedback (name, email, message, category) VALUES ('" + 
                       name + "', '" + email + "', '" + message + "', '" + category + "')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    // SQL Injection vulnerability - get all feedbacks
    public List<String> getAllFeedbacks() throws SQLException {
        String query = "SELECT feedback_text FROM feedback";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<String> feedbacks = new ArrayList<>();
            while (resultSet.next()){
                feedbacks.add(resultSet.getString(1));
            }
            return feedbacks;
        }
    }

    // SQL Injection vulnerability - search feedback by category
    public List<String> searchFeedbackByCategory(String category) throws SQLException {
        String query = "SELECT feedback_text FROM feedback WHERE category = '" + category + "'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<String> feedbacks = new ArrayList<>();
            while (resultSet.next()){
                feedbacks.add(resultSet.getString(1));
            }
            return feedbacks;
        }
    }
}
