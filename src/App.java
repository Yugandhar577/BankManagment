import java.util.Scanner;

public class App {
    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();
    static AccountDAO accountDAO = new AccountDAO();
    static BankService bankService = new BankService();

    // ANSI Colors
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String BOLD = "\u001B[1m";

    // Helper for centered text
    public static void printCentered(String text) {
        int width = 70;
        int pad = (width - text.length()) / 2;
        if (pad < 0) pad = 0;
        System.out.printf("%" + pad + "s%s%n", "", text);
    }

    public static void border() {
        System.out.println(BLUE + "==========================================================================" + RESET);
    }

    public static void header(String title) {
        border();
        printCentered(BOLD + CYAN + title + RESET);
        border();
    }

    public static void pause() {
        System.out.print(YELLOW + "\nPress Enter to continue..." + RESET);
        sc.nextLine();
    }

    public static void main(String[] args) {
        header("Welcome to the Online Bank Management System");

        while (true) {
            header("Main Menu");
            System.out.println(GREEN + "1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit" + RESET);
            border();
            System.out.print(YELLOW + "Enter choice: " + RESET);

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println(GREEN + "Goodbye! Have a great day!" + RESET);
                break;
            } else if (choice == 1) {
                register();
            } else if (choice == 2) {
                login();
            } else {
                System.out.println(RED + "Invalid choice. Try again." + RESET);
            }
            pause();
        }
    }

    static void register() {
        header("User Registration");
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        User u = new User(name, email, pass);
        if (userDAO.register(u))
            System.out.println(GREEN + "Registered successfully!" + RESET);
        else
            System.out.println(RED + "Registration failed. Email might already exist." + RESET);
    }

    static void login() {
        header("User Login");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        User u = userDAO.login(email, pass);
        if (u == null) {
            System.out.println(RED + "Invalid credentials." + RESET);
            return;
        }

        System.out.println(GREEN + "\nWelcome, " + u.name + "!" + RESET);

        while (true) {
            header("Account Menu");
            System.out.println(GREEN +
                    "1. Create New Account\n" +
                    "2. Deposit Money\n" +
                    "3. Withdraw Money\n" +
                    "4. Transfer Money\n" +
                    "5. Check Balance\n" +
                    "6. View Transactions\n" +
                    "7. Apply for Loan\n" +
                    "8. View Loans\n" +
                    "9. Repay Loan\n" +
                    "0. Logout" + RESET);
            border();
            System.out.print(YELLOW + "Choose: " + RESET);

            int ch = sc.nextInt();
            sc.nextLine(); // buffer clear

            if (ch == 0) {
                System.out.println(GREEN + "Logged out successfully!" + RESET);
                break;
            }

            switch (ch) {
                case 1:
                    header("Create New Account");
                    int accId = accountDAO.createAccount(u.id, 0);
                    System.out.println(GREEN + "New Account Created! Account ID: " + accId + RESET);
                    break;

                case 2:
                    header("Deposit Money");
                    System.out.print("Account ID: ");
                    int depId = sc.nextInt();
                    System.out.print("Amount: ");
                    double depAmt = sc.nextDouble();
                    sc.nextLine();
                    if (bankService.deposit(depId, depAmt))
                        System.out.println(GREEN + "Amount Deposited Successfully!" + RESET);
                    else
                        System.out.println(RED + "Deposit Failed!" + RESET);
                    break;

                case 3:
                    header("Withdraw Money");
                    System.out.print("Account ID: ");
                    int wId = sc.nextInt();
                    System.out.print("Amount: ");
                    double wAmt = sc.nextDouble();
                    sc.nextLine();
                    if (bankService.withdraw(wId, wAmt))
                        System.out.println(GREEN + "Withdrawal Successful!" + RESET);
                    else
                        System.out.println(RED + "Insufficient Balance or Error." + RESET);
                    break;

                case 4:
                    header("Transfer Money");
                    System.out.print("From Account ID: ");
                    int fromId = sc.nextInt();
                    System.out.print("To Account ID: ");
                    int toId = sc.nextInt();
                    System.out.print("Amount: ");
                    double tAmt = sc.nextDouble();
                    sc.nextLine();
                    if (bankService.transfer(fromId, toId, tAmt))
                        System.out.println(GREEN + "Transfer Successful!" + RESET);
                    else
                        System.out.println(RED + "Transfer Failed! Check balances or IDs." + RESET);
                    break;

                case 5:
                    header("Check Balance");
                    System.out.print("Account ID: ");
                    int balId = sc.nextInt();
                    double bal = bankService.getBalance(balId);
                    System.out.println(CYAN + "Current Balance: Rs. " + bal + RESET);
                    sc.nextLine();
                    break;

                case 6:
                    header("Transaction History");
                    System.out.print("Account ID: ");
                    int txAcc = sc.nextInt();
                    sc.nextLine();
                    bankService.viewTransactions(txAcc);
                    break;

                case 7:
                    header("Apply for Loan");
                    System.out.print("Account ID: ");
                    int loanAcc = sc.nextInt();
                    System.out.print("Loan Amount: ");
                    double loanAmt = sc.nextDouble();
                    System.out.print("Interest Rate (%): ");
                    double rate = sc.nextDouble();
                    System.out.print("Duration (months): ");
                    int months = sc.nextInt();
                    sc.nextLine();
                    bankService.applyLoan(loanAcc, loanAmt, rate, months);
                    break;

                case 8:
                    header("View Loans");
                    System.out.print("Account ID: ");
                    int viewAcc = sc.nextInt();
                    sc.nextLine();
                    bankService.viewLoans(viewAcc);
                    break;

                case 9:
                    header("Repay Loan");
                    System.out.print("Account ID: ");
                    int repayAcc = sc.nextInt();
                    System.out.print("Loan ID: ");
                    int loanId = sc.nextInt();
                    System.out.print("Repay Amount: ");
                    double repayAmt = sc.nextDouble();
                    sc.nextLine();
                    bankService.repayLoan(repayAcc, loanId, repayAmt);
                    break;

                default:
                    System.out.println(RED + "Invalid choice. Try again." + RESET);
                    break;
            }
            pause();
        }
    }
}
