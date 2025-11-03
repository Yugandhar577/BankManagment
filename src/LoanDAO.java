import java.sql.*;

public class LoanDAO {

    public boolean applyLoan(Loan loan) {
        String sql = "INSERT INTO loans(acc_id, amount, interest_rate, duration_months, status) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loan.accId);
            ps.setDouble(2, loan.amount);
            ps.setDouble(3, loan.interestRate);
            ps.setInt(4, loan.durationMonths);
            ps.setString(5, loan.status);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveLoan(int loanId, String managerName) {
        String sql = "UPDATE loans SET status='APPROVED', approved_by=? WHERE loan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, managerName);
            ps.setInt(2, loanId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean repayLoan(int loanId, double amount) {
        String sql = "UPDATE loans SET status='CLOSED' WHERE loan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewLoans(int accId) {
        String sql = "SELECT * FROM loans WHERE acc_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Loan Details ---");
            System.out.printf("%-8s %-10s %-10s %-10s %-12s %-15s%n",
                    "LoanID", "Amount", "Rate(%)", "Months", "Status", "Approved By");
            while (rs.next()) {
                System.out.printf("%-8d %-10.2f %-10.2f %-10d %-12s %-15s%n",
                        rs.getInt("loan_id"),
                        rs.getDouble("amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("duration_months"),
                        rs.getString("status"),
                        rs.getString("approved_by"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
