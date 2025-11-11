public class Loan {
    int loanId;
    int accId;
    double amount;
    double outstandingBalance;
    double interestRate;
    int durationMonths;
    String status;
    String approvedBy;

    public Loan(int accId, double amount, double interestRate, int durationMonths) {
        this.accId = accId;
        this.amount = amount;
        this.outstandingBalance = amount; // Initially, outstanding balance equals loan amount
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.status = "PENDING";
    }
}
