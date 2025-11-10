# Bank Management System (Java, Swing, MySQL)

A simple end-to-end bank management system featuring a modern Swing UI and MySQL persistence. Users can register/login, create accounts, deposit/withdraw, transfer funds, view transactions, apply for loans, and repay loans.

## Features
- Login and Registration
- Create Account
- Deposit / Withdraw
- Transfer Funds
- Check Balance
- Transaction History (table)
- Loans: Apply, View, Repay
- Themed Swing UI with navbar and larger fonts

## Tech Stack
- Java 17+ (JDK 17 or JDK 21 recommended)
- Swing (desktop UI)
- MySQL 8+
- JDBC (MySQL Connector/J)

## Prerequisites
- Install a JDK (recommended: Adoptium Temurin JDK 17).
- Install MySQL and create a database.
- Ensure the MySQL JDBC driver JAR exists at `lib/mysql-connector-j-9.4.0.jar` (already included in this repo).

## Setup

1) Database
- Create a database (e.g., `bankdb`) and user/password with appropriate privileges.
- Apply schema and optionally seed data:

```sql
-- From MySQL client
SOURCE /absolute/path/to/schema.sql;
SOURCE /absolute/path/to/sample_data.sql; -- optional
```

2) Configure connection
- Create/verify `db.properties` in the project root:

```properties
url=jdbc:mysql://localhost:3306/bankdb?useSSL=false&serverTimezone=UTC
user=your_mysql_user
password=your_mysql_password
```

3) Build and Run

Windows PowerShell:
```powershell
javac -cp ".;lib\mysql-connector-j-9.4.0.jar" src\*.java
java -cp ".;lib\mysql-connector-j-9.4.0.jar;src" BankAppUI
```

macOS/Linux (bash/zsh):
```bash
javac -cp ".:lib/mysql-connector-j-9.4.0.jar" src/*.java
java -cp ".:lib/mysql-connector-j-9.4.0.jar:src" BankAppUI
```

If you prefer the original terminal app:
```bash
java -cp ".:lib/mysql-connector-j-9.4.0.jar:src" App
```
(Use `;` instead of `:` on Windows.)

## Project Structure
```
bankManagementSystem/
├─ db.properties
├─ schema.sql
├─ sample_data.sql
├─ lib/
│  └─ mysql-connector-j-9.4.0.jar
└─ src/
   ├─ App.java                    # Legacy terminal UI
   ├─ BankAppUI.java              # Swing UI entry point
   ├─ DBConnection.java           # JDBC connection factory
   ├─ User.java / UserDAO.java
   ├─ Account.java / AccountDAO.java
   ├─ Transaction.java / TransactionDAO.java
   ├─ Loan.java / LoanDAO.java
   ├─ BankService.java            # Business logic
   ├─ TransactionsDataProvider.java
   └─ LoansDataProvider.java
```

## Notes
- The UI uses a light theme with a primary blue accent and larger Segoe UI fonts. You can tweak colors and sizes inside `BankAppUI` (theme section).
- Ensure `db.properties` is readable by the app (it is loaded relative to the working directory).

## Troubleshooting
- Classpath errors: Double-check separators (`;` on Windows, `:` on Unix) and that `src` and the MySQL driver JAR are on the classpath.
- MySQL connection: Verify `db.properties`, DB is running, and credentials are correct. Test with a MySQL client.
- Missing tables: Re-run `schema.sql`. Seed `sample_data.sql` if needed.
- Button colors on Windows: The app forces button background/hover colors to look consistent across Look & Feels.

## License
This project is provided as-is for learning/demo purposes.


