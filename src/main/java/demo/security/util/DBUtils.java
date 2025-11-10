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

    public List<String> findUsers(String user) throws SQLException {
        String query = "SELECT userid FROM users WHERE username = '" + user  + "'";
        List<String> users = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()){
                users.add(resultSet.getString(1));
            }
        }
        return users;
    }

    public List<String> findItem(String itemId) throws SQLException {
        String query = "SELECT item_id FROM items WHERE item_id = '" + itemId  + "'";
        List<String> items = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()){
                items.add(resultSet.getString(1));
            }
        }
        return items;
    }

    public List<String> findFeedback(String feedbackId) throws SQLException {
        String query = "SELECT message FROM feedback WHERE id = '" + feedbackId  + "'";
        List<String> feedback = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()){
                feedback.add(resultSet.getString(1));
            }
        }
        return feedback;
    }
}
