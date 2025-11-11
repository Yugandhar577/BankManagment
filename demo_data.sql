-- Demo data script for presentation
-- Run this after schema.sql to set up a good demo scenario
-- This creates a user with accounts and a loan ready for partial payment demo

USE bankdb;

-- Clear existing demo data (optional - comment out if you want to keep existing data)
-- DELETE FROM transactions WHERE acc_id IN (SELECT acc_id FROM accounts WHERE user_id = 999);
-- DELETE FROM loans WHERE acc_id IN (SELECT acc_id FROM accounts WHERE user_id = 999);
-- DELETE FROM accounts WHERE user_id = 999;
-- DELETE FROM users WHERE user_id = 999;

-- Create demo user
INSERT INTO users (user_id, name, email, password) VALUES
(999, 'Demo User', 'demo@bank.com', 'demo123')
ON DUPLICATE KEY UPDATE name='Demo User', email='demo@bank.com';

-- Create two accounts for demo user
INSERT INTO accounts (user_id, type, balance, status) VALUES
(999, 'SAVINGS', 50000.00, 'ACTIVE'),
(999, 'CURRENT', 25000.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE balance=VALUES(balance);

-- Get the account IDs (you'll need to check these after running)
-- Account 1 will be used for loan demo
-- Account 2 will be used for transfer demo

-- Create some initial transactions
INSERT INTO transactions (acc_id, type, amount, description) 
SELECT acc_id, 'DEPOSIT', 50000.00, 'Initial deposit for demo'
FROM accounts WHERE user_id = 999 AND type = 'SAVINGS'
LIMIT 1;

INSERT INTO transactions (acc_id, type, amount, description) 
SELECT acc_id, 'DEPOSIT', 25000.00, 'Initial deposit for demo'
FROM accounts WHERE user_id = 999 AND type = 'CURRENT'
LIMIT 1;

-- Create a loan ready for partial payment demo
-- Note: You'll need to manually approve this loan or update the status
INSERT INTO loans (acc_id, amount, outstanding_balance, interest_rate, duration_months, status, approved_by)
SELECT acc_id, 30000.00, 30000.00, 8.5, 12, 'APPROVED', 'Manager Demo'
FROM accounts WHERE user_id = 999 AND type = 'SAVINGS'
LIMIT 1;

-- Display the created data
SELECT 'Demo user created!' AS Status;
SELECT user_id, name, email FROM users WHERE user_id = 999;
SELECT acc_id, user_id, type, balance, status FROM accounts WHERE user_id = 999;
SELECT loan_id, acc_id, amount, outstanding_balance, status FROM loans 
WHERE acc_id IN (SELECT acc_id FROM accounts WHERE user_id = 999);

-- Instructions
SELECT 'Demo Setup Complete!' AS Message;
SELECT 'Login with: demo@bank.com / demo123' AS Credentials;
SELECT 'Check accounts table for Account IDs to use in demo' AS NextStep;

