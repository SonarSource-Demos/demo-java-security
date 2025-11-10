package demo.security.util;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class FeedbackUtils {

    private Connection connection;

    public FeedbackUtils() throws SQLException {
        // Hard-coded database credentials
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/feedback", 
                "dbuser", 
                "dbpassword123");
    }

    // SQL Injection vulnerability
    public void storeFeedback(String name, String email, String subject, String message) 
            throws SQLException {
        String query = "INSERT INTO feedback (name, email, subject, message) VALUES ('" 
                + name + "', '" + email + "', '" + subject + "', '" + message + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        statement.close();
    }

    // SQL Injection vulnerability in search
    public List<String> searchFeedback(String keyword) throws SQLException {
        String query = "SELECT * FROM feedback WHERE message LIKE '%" + keyword + "%'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> results = new ArrayList<>();
        while (resultSet.next()) {
            results.add(resultSet.getString("message"));
        }
        resultSet.close();
        statement.close();
        return results;
    }

    // LDAP Injection vulnerability
    public List<String> findUserByEmail(String email) throws Exception {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=example,dc=com");
        env.put(Context.SECURITY_CREDENTIALS, "admin123"); // Hard-coded credentials

        DirContext ctx = new InitialDirContext(env);
        
        // LDAP Injection vulnerability
        String filter = "(mail=" + email + ")";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search("dc=example,dc=com", filter, searchControls);
        
        List<String> users = new ArrayList<>();
        while (results.hasMore()) {
            SearchResult searchResult = results.next();
            users.add(searchResult.getNameInNamespace());
        }
        
        ctx.close();
        return users;
    }

    // SQL Injection vulnerability for retrieving feedback
    public String getFeedbackById(String feedbackId) throws SQLException {
        String query = "SELECT message FROM feedback WHERE id = " + feedbackId;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        String message = null;
        if (resultSet.next()) {
            message = resultSet.getString("message");
        }
        resultSet.close();
        statement.close();
        return message;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

