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

    public void saveContactFeedback(String name, String email, String message) throws DatabaseException {
        String query = "INSERT INTO contact_feedback (name, email, message) VALUES ('" + name + "', '" + email + "', '" + message + "')";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save contact feedback", e);
        }
    }

    public List<String> findContactFeedback(String feedbackId) throws DatabaseException {
        String query = "SELECT name, email, message FROM contact_feedback WHERE id = '" + feedbackId + "'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            List<String> feedback = new ArrayList<>();
            while (resultSet.next()){
                feedback.add(resultSet.getString(1));
            }
            return feedback;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve contact feedback", e);
        }
    }
}
