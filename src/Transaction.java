public class Transaction {
    int txnId, accId;
    String type, description;
    double amount;

    public Transaction(int accId, String type, double amount, String description) {
        this.accId = accId; this.type = type; this.amount = amount; this.description = description;
    }
}
