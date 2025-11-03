import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BankService {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO txnDAO = new TransactionDAO();
    private final LoanDAO loanDAO = new LoanDAO();


    /**
     * Deposit money into an account.
     */
    public boolean deposit(int accId, double amount) {
        if (amount <= 0) {
            System.out.println("❌ Invalid amount for deposit.");
            return false;
        }

        if (!isAccountActive(accId)) {
            System.out.println("❌ Account not found or inactive.");
            return false;
        }

        double newBal = accountDAO.getBalance(accId) + amount;
        boolean ok = accountDAO.updateBalance(accId, newBal);
        if (ok) {
            txnDAO.addTransaction(new Transaction(accId, "DEPOSIT", amount, "Cash deposit"));
            System.out.println("✅ Deposited ₹" + amount + " to Account #" + accId);
        } else {
            System.out.println("❌ Deposit failed due to a database error.");
        }
        return ok;
    }

    /**
     * Withdraw money from an account if sufficient balance exists.
     */
    public boolean withdraw(int accId, double amount) {
        if (amount <= 0) {
            System.out.println("❌ Invalid amount for withdrawal.");
            return false;
        }

        if (!isAccountActive(accId)) {
            System.out.println("❌ Account not found or inactive.");
            return false;
        }

        double bal = accountDAO.getBalance(accId);
        if (bal < amount) {
            System.out.println("⚠️ Insufficient balance.");
            return false;
        }

        boolean ok = accountDAO.updateBalance(accId, bal - amount);
        if (ok) {
            txnDAO.addTransaction(new Transaction(accId, "WITHDRAW", amount, "Cash withdrawal"));
            System.out.println("✅ Withdrawn ₹" + amount + " from Account #" + accId);
        } else {
            System.out.println("❌ Withdrawal failed due to a database error.");
        }
        return ok;
    }

    /**
     * Transfer money between two accounts atomically.
     */
    public boolean transfer(int fromAcc, int toAcc, double amount) {
        if (amount <= 0) {
            System.out.println("❌ Invalid transfer amount.");
            return false;
        }

        if (fromAcc == toAcc) {
            System.out.println("❌ Cannot transfer to the same account.");
            return false;
        }

        if (!isAccountActive(fromAcc) || !isAccountActive(toAcc)) {
            System.out.println("❌ One or both accounts are inactive or invalid.");
            return false;
        }

        double senderBal = accountDAO.getBalance(fromAcc);
        if (senderBal < amount) {
            System.out.println("⚠️ Insufficient funds for transfer.");
            return false;
        }

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false); // Start transaction

            // Deduct from sender
            PreparedStatement ps1 = conn.prepareStatement(
                "UPDATE accounts SET balance=balance-? WHERE acc_id=?"
            );
            ps1.setDouble(1, amount);
            ps1.setInt(2, fromAcc);
            ps1.executeUpdate();

            // Credit receiver
            PreparedStatement ps2 = conn.prepareStatement(
                "UPDATE accounts SET balance=balance+? WHERE acc_id=?"
            );
            ps2.setDouble(1, amount);
            ps2.setInt(2, toAcc);
            ps2.executeUpdate();

            // Record both transactions
            txnDAO.addTransaction(new Transaction(fromAcc, "TRANSFER", amount, "To Account #" + toAcc));
            txnDAO.addTransaction(new Transaction(toAcc, "TRANSFER", amount, "From Account #" + fromAcc));

            conn.commit();
            System.out.println("✅ ₹" + amount + " transferred from Account #" + fromAcc + " → Account #" + toAcc);
            return true;

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            System.out.println("❌ Transfer failed. Rolling back changes.");
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    /**
     * Get the current balance of an account.
     */
    public double getBalance(int accId) {
        if (!isAccountActive(accId)) {
            System.out.println("❌ Account not found or inactive.");
            return 0;
        }
        double bal = accountDAO.getBalance(accId);
        System.out.println("💰 Current balance of Account #" + accId + ": ₹" + bal);
        return bal;
    }

    /**
     * Helper: check if account exists and is active.
     */
    private boolean isAccountActive(int accId) {
        try {
            String sql = "SELECT status FROM accounts WHERE acc_id=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status").equalsIgnoreCase("ACTIVE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

        /**
     * View all transactions for a given account.
     */
    public void viewTransactions(int accId) {
        if (!isAccountActive(accId)) {
            System.out.println("❌ Account not found or inactive.");
            return;
        }

        String sql = "SELECT txn_id, type, amount, description, txn_time FROM transactions WHERE acc_id=? ORDER BY txn_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n📜 Transaction History for Account #" + accId + ":");
            System.out.println("--------------------------------------------------------");
            System.out.printf("%-10s %-12s %-12s %-25s %-20s%n", "Txn ID", "Type", "Amount", "Description", "Timestamp");
            System.out.println("--------------------------------------------------------");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("%-10d %-12s %-12.2f %-25s %-20s%n",
                        rs.getInt("txn_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getTimestamp("txn_time").toString());
            }

            if (!hasData) {
                System.out.println("No transactions found for this account.");
            }

            System.out.println("--------------------------------------------------------\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error fetching transaction history.");
        }
    }
    public boolean applyLoan(int accId, double amount, double rate, int months) {
        if (!isAccountActive(accId)) {
            System.out.println("Account not found!");
            return false;
        }
        Loan loan = new Loan(accId, amount, rate, months);
        boolean ok = loanDAO.applyLoan(loan);
        if (ok) System.out.println("Loan application submitted!");
        return ok;
    }

    public boolean approveLoan(int loanId, String managerName) {
        boolean ok = loanDAO.approveLoan(loanId, managerName);
        if (ok) System.out.println("Loan approved by " + managerName);
        return ok;
    }

    public boolean repayLoan(int accId, int loanId, double amount) {
        double bal = accountDAO.getBalance(accId);
        if (bal < amount) {
            System.out.println("Insufficient balance to repay loan.");
            return false;
        }

        boolean ok = accountDAO.updateBalance(accId, bal - amount);
        if (ok) {
            loanDAO.repayLoan(loanId, amount);
            txnDAO.addTransaction(new Transaction(accId, "LOAN_REPAY", amount, "Loan repayment"));
            System.out.println("Loan repaid successfully!");
        }
        return ok;
    }

    public void viewLoans(int accId) {
        loanDAO.viewLoans(accId);
    }

}

