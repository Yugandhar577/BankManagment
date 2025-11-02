CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(150) UNIQUE,
  password VARCHAR(100)
);

CREATE TABLE accounts (
  acc_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  balance DOUBLE DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transactions (
  txn_id INT AUTO_INCREMENT PRIMARY KEY,
  acc_id INT,
  type VARCHAR(20),
  amount DOUBLE,
  description VARCHAR(200),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (acc_id) REFERENCES accounts(acc_id)
);
