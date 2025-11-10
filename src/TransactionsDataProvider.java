import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionsDataProvider {
	public static Object[][] fetchForAccount(int accId) {
		String sql = "SELECT txn_id, type, amount, description, txn_time FROM transactions WHERE acc_id=? ORDER BY txn_time DESC";
		List<Object[]> rows = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, accId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rows.add(new Object[]{
						rs.getInt("txn_id"),
						rs.getString("type"),
						rs.getDouble("amount"),
						rs.getString("description"),
						rs.getTimestamp("txn_time")
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows.toArray(new Object[0][]);
	}
}


