-- ✅ Create the main database
CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

-- 👤 Users table: stores login and profile data
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 🏦 Accounts table: each user can have multiple accounts
CREATE TABLE accounts (
  acc_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  type ENUM('SAVINGS', 'CURRENT') DEFAULT 'SAVINGS',
  balance DOUBLE DEFAULT 0,
  status ENUM('ACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 💸 Transactions table: tracks all financial movements
CREATE TABLE transactions (
  txn_id INT AUTO_INCREMENT PRIMARY KEY,
  acc_id INT NOT NULL,
  type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER', 'LOAN_REPAY') NOT NULL,
  amount DOUBLE NOT NULL CHECK (amount >= 0),
  description VARCHAR(255),
  txn_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (acc_id) REFERENCES accounts(acc_id) ON DELETE CASCADE
);

-- 💰 Loans table: tracks loan applications and repayments
CREATE TABLE loans (
  loan_id INT AUTO_INCREMENT PRIMARY KEY,
  acc_id INT NOT NULL,
  amount DOUBLE NOT NULL CHECK (amount > 0),
  outstanding_balance DOUBLE NOT NULL CHECK (outstanding_balance >= 0),
  interest_rate DOUBLE NOT NULL CHECK (interest_rate >= 0),
  duration_months INT NOT NULL CHECK (duration_months > 0),
  status ENUM('PENDING', 'APPROVED', 'CLOSED') DEFAULT 'PENDING',
  approved_by VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (acc_id) REFERENCES accounts(acc_id) ON DELETE CASCADE
);
