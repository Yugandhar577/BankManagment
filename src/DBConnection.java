import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

public class DBConnection {
    private static Connection conn;

    public static Connection getConnection() {
        if (conn != null) return conn;
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));

            // ✅ Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(
                props.getProperty("url"),
                props.getProperty("user"),
                props.getProperty("password")
            );

            System.out.println("✅ Database connected successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
