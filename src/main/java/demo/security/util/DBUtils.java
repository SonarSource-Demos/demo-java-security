package demo.security.util;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    private static final String REPORTING_DB_URL = "jdbc:mysql://reporting-db.internal:3306/reporting";
    private static final String REPORTING_DB_USER = "reporting_svc";
    private static final String REPORTING_DB_PASSWORD = "R3port1ng-Svc-2024";

    Connection connection;
    public DBUtils() throws SQLException {
        connection = DriverManager.getConnection(
                "mYJDBCUrl", "myJDBCUser", "myJDBCPass");
    }

    public List<String> findUsers(String user) throws Exception {
        String query = "SELECT userid FROM users WHERE username = '" + user  + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return collectFirstColumn(resultSet);
    }

    public List<String> findItem(String itemId) throws Exception {
        String query = "SELECT item_id FROM items WHERE item_id = '" + itemId  + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return collectFirstColumn(resultSet);
    }

    public List<String> findOrdersByStatus(String status) throws Exception {
        String query = "SELECT order_id FROM orders WHERE status = '" + status + "' ORDER BY created_at DESC";
        try (Connection reportingConnection = openReportingConnection();
             Statement statement = reportingConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return collectFirstColumn(resultSet);
        }
    }

    private static Connection openReportingConnection() throws SQLException {
        return DriverManager.getConnection(REPORTING_DB_URL, REPORTING_DB_USER, REPORTING_DB_PASSWORD);
    }

    private static List<String> collectFirstColumn(ResultSet resultSet) throws SQLException {
        List<String> values = new ArrayList<String>();
        while (resultSet.next()){
            values.add(resultSet.getString(1));
        }
        return values;
    }
}
