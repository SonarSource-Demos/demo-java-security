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
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<String> users = new ArrayList<>();
            while (resultSet.next()){
                users.add(resultSet.getString(1));
            }
            return users;
        }
    }

    public List<String> findItem(String itemId) throws SQLException {
        String query = "SELECT item_id FROM items WHERE item_id = '" + itemId  + "'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<String> items = new ArrayList<>();
            while (resultSet.next()){
                items.add(resultSet.getString(1));
            }
            return items;
        }
    }

    public void saveFeedback(String name, String email, String subject, String message) throws SQLException {
        String query = "INSERT INTO feedback (name, email, subject, message) VALUES ('" + name + "', '" + email + "', '" + subject + "', '" + message + "')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }
}
