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

    public List<String> findContactFeedback(String feedbackId) throws Exception {
        String query = "SELECT name, email, message FROM contact_feedback WHERE id = '" + feedbackId + "'";
        List<String> feedback = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()){
                feedback.add(resultSet.getString(1) + " - " + resultSet.getString(2) + ": " + resultSet.getString(3));
            }
        }
        return feedback;
    }

    public void saveContactFeedback(String name, String email, String message, String attachment) throws Exception {
        String query = "INSERT INTO contact_feedback (name, email, message, attachment) VALUES ('" + 
                      name + "', '" + email + "', '" + message + "', '" + attachment + "')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
}
