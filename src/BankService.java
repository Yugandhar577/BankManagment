public class BankService {
    AccountDAO accountDAO = new AccountDAO();
    TransactionDAO txnDAO = new TransactionDAO();

    public boolean deposit(int accId, double amount) {
        double bal = accountDAO.getBalance(accId) + amount;
        boolean ok = accountDAO.updateBalance(accId, bal);
        if (ok) txnDAO.addTransaction(new Transaction(accId, "DEPOSIT", amount, "Cash deposit"));
        return ok;
    }

    public boolean withdraw(int accId, double amount) {
        double bal = accountDAO.getBalance(accId);
        if (bal < amount) return false;
        boolean ok = accountDAO.updateBalance(accId, bal - amount);
        if (ok) txnDAO.addTransaction(new Transaction(accId, "WITHDRAW", amount, "Cash withdrawal"));
        return ok;
    }
}
