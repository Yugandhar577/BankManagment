-- Simple migration script to add outstanding_balance column
-- Run this in your MySQL client or command line

USE bankdb;

-- Add outstanding_balance column to loans table
-- If the column already exists, you'll get an error - just ignore it
ALTER TABLE loans 
ADD COLUMN outstanding_balance DOUBLE NOT NULL DEFAULT 0;

-- Update existing loans: set outstanding_balance = amount for all existing loans
UPDATE loans 
SET outstanding_balance = amount;

-- Update transactions table to include LOAN_REPAY in the enum
ALTER TABLE transactions 
MODIFY COLUMN type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER', 'LOAN_REPAY') NOT NULL;

SELECT 'Migration completed! outstanding_balance column added to loans table.' AS Status;

