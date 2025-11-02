import java.sql.*;
// import java.util.*;

public class AccountDAO {
    public int createAccount(int userId, double balance) {
        try {
            String sql = "INSERT INTO accounts(user_id,balance) VALUES(?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setDouble(2, balance);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    public boolean updateBalance(int accId, double newBal) {
        try {
            String sql = "UPDATE accounts SET balance=? WHERE acc_id=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setDouble(1, newBal);
            ps.setInt(2, accId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public double getBalance(int accId) {
        try {
            String sql = "SELECT balance FROM accounts WHERE acc_id=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
