import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LoansDataProvider {
	public static Object[][] fetchForAccount(int accId) {
		String sql = "SELECT loan_id, amount, interest_rate, duration_months, status, approved_by FROM loans WHERE acc_id=? ORDER BY loan_id DESC";
		List<Object[]> rows = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, accId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rows.add(new Object[]{
						rs.getInt("loan_id"),
						rs.getDouble("amount"),
						rs.getDouble("interest_rate"),
						rs.getInt("duration_months"),
						rs.getString("status"),
						rs.getString("approved_by")
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows.toArray(new Object[0][]);
	}
}


