import java.sql.*;

public class UserDAO {
    public boolean register(User u) {
        try {
            String sql = "INSERT INTO users(name,email,password) VALUES(?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, u.name);
            ps.setString(2, u.email);
            ps.setString(3, u.password);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public User login(String email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.id = rs.getInt("user_id");
                u.name = rs.getString("name");
                return u;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
