import java.util.Scanner;

public class App {
    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();
    static AccountDAO accountDAO = new AccountDAO();
    static BankService bankService = new BankService();

    public static void main(String[] args) {
        System.out.println("=== Online Bank Management System ===");
        while (true) {
            System.out.println("1. Register  2. Login  0. Exit");
            int choice = sc.nextInt(); sc.nextLine();
            if (choice == 0) break;
            if (choice == 1) register();
            if (choice == 2) login();
        }
    }

    static void register() {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        User u = new User(name, email, pass);
        if (userDAO.register(u)) System.out.println("Registered successfully!");
    }

    static void login() {
        System.out.print("Email: "); String email = sc.nextLine();
        System.out.print("Password: "); String pass = sc.nextLine();
        User u = userDAO.login(email, pass);
        if (u == null) { System.out.println("Invalid credentials."); return; }

        System.out.println("Welcome, " + u.name);
        while (true) {
            System.out.println("1. Create Account  2. Deposit  3. Withdraw  0. Logout");
            int ch = sc.nextInt();
            if (ch == 0) break;
            if (ch == 1) {
                int id = accountDAO.createAccount(u.id, 0);
                System.out.println("Created Account ID: " + id);
            }
            if (ch == 2) {
                System.out.print("Account ID: "); int accId = sc.nextInt();
                System.out.print("Amount: "); double amt = sc.nextDouble();
                if (bankService.deposit(accId, amt)) System.out.println("Deposited.");
            }
            if (ch == 3) {
                System.out.print("Account ID: "); int accId = sc.nextInt();
                System.out.print("Amount: "); double amt = sc.nextDouble();
                if (bankService.withdraw(accId, amt)) System.out.println("Withdrawn.");
                else System.out.println("Insufficient balance.");
            }
        }
    }
}
