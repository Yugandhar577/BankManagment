import java.sql.*;

public class TransactionDAO {
    public void addTransaction(Transaction t) {
        try {
            String sql = "INSERT INTO transactions(acc_id,type,amount,description) VALUES(?,?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, t.accId);
            ps.setString(2, t.type);
            ps.setDouble(3, t.amount);
            ps.setString(4, t.description);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
