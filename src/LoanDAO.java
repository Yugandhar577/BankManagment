import java.sql.*;

public class LoanDAO {

    public boolean applyLoan(Loan loan) {
        String sql = "INSERT INTO loans(acc_id, amount, outstanding_balance, interest_rate, duration_months, status) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loan.accId);
            ps.setDouble(2, loan.amount);
            ps.setDouble(3, loan.outstandingBalance);
            ps.setDouble(4, loan.interestRate);
            ps.setInt(5, loan.durationMonths);
            ps.setString(6, loan.status);
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

    /**
     * Get the current outstanding balance for a loan.
     */
    public double getOutstandingBalance(int loanId) {
        String sql = "SELECT outstanding_balance FROM loans WHERE loan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("outstanding_balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Error indicator
    }

    /**
     * Process a loan repayment (partial or full).
     * Returns the new outstanding balance after payment.
     */
    public double repayLoan(int loanId, double paymentAmount) {
        // Get current outstanding balance
        double currentBalance = getOutstandingBalance(loanId);
        if (currentBalance < 0) {
            return -1; // Error
        }

        // Calculate new outstanding balance
        double newBalance = Math.max(0, currentBalance - paymentAmount);
        String newStatus = (newBalance <= 0.01) ? "CLOSED" : "APPROVED"; // Consider fully paid if less than 1 paisa

        String sql = "UPDATE loans SET outstanding_balance=?, status=? WHERE loan_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, newStatus);
            ps.setInt(3, loanId);
            if (ps.executeUpdate() > 0) {
                return newBalance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Error
    }

    public void viewLoans(int accId) {
        String sql = "SELECT * FROM loans WHERE acc_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Loan Details ---");
            System.out.printf("%-8s %-10s %-15s %-10s %-10s %-12s %-15s%n",
                    "LoanID", "Amount", "Outstanding", "Rate(%)", "Months", "Status", "Approved By");
            while (rs.next()) {
                System.out.printf("%-8d %-10.2f %-15.2f %-10.2f %-10d %-12s %-15s%n",
                        rs.getInt("loan_id"),
                        rs.getDouble("amount"),
                        rs.getDouble("outstanding_balance"),
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
