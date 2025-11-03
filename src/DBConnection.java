import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

public class DBConnection {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            // If connection is already valid, reuse it
            if (conn != null && !conn.isClosed()) {
                return conn;
            }

            // Otherwise, create a new connection
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));

            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(
                props.getProperty("url"),
                props.getProperty("user"),
                props.getProperty("password")
            );

            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
