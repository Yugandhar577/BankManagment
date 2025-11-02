USE bankdb;

-- Clear old data if re-running
DELETE FROM transactions;
DELETE FROM accounts;
DELETE FROM users;

-- 👤 Sample Users
INSERT INTO users (name, email, password) VALUES
('Alice Johnson', 'alice@example.com', 'alice123'),
('Bob Smith', 'bob@example.com', 'bob123'),
('Charlie Brown', 'charlie@example.com', 'charlie123'),
('Diana Prince', 'diana@example.com', 'wonderwoman'),
('Ethan Clark', 'ethan@example.com', 'ethanpass'),
('Fiona Adams', 'fiona@example.com', 'fiona321');

-- 💳 Sample Accounts
INSERT INTO accounts (user_id, balance) VALUES
(1, 12000.00),
(2, 5600.50),
(3, 8900.75),
(4, 25000.00),
(5, 1500.00),
(6, 670.00);

-- 💸 Sample Transactions
INSERT INTO transactions (acc_id, type, amount, description) VALUES
(1, 'DEPOSIT', 12000, 'Initial deposit'),
(2, 'DEPOSIT', 5600.50, 'Initial deposit'),
(3, 'DEPOSIT', 8900.75, 'Initial deposit'),
(4, 'DEPOSIT', 25000.00, 'Salary credit'),
(5, 'DEPOSIT', 1500.00, 'First savings deposit'),
(6, 'DEPOSIT', 670.00, 'Cash deposit'),

-- Withdrawals
(1, 'WITHDRAW', 2000, 'ATM withdrawal'),
(3, 'WITHDRAW', 1500, 'Online shopping'),
(4, 'WITHDRAW', 5000, 'Rent payment'),

-- More deposits
(2, 'DEPOSIT', 2000, 'Salary credited'),
(5, 'DEPOSIT', 3500, 'Freelance payment'),
(6, 'DEPOSIT', 200, 'Interest added'),

-- Transfers
(1, 'TRANSFER', 1500, 'Transfer to Bob'),
(2, 'TRANSFER', 1500, 'Received from Alice'),
(4, 'TRANSFER', 2500, 'Transfer to Fiona'),
(6, 'TRANSFER', 2500, 'Received from Diana');
