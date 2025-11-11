-- Migration script to add outstanding_balance column to loans table
-- Run this script to update your existing database

USE bankdb;

-- Step 1: Check if outstanding_balance column exists, if not add it
-- MySQL doesn't support IF NOT EXISTS for ALTER TABLE ADD COLUMN, so we use a stored procedure approach
-- Or simply try to add it and ignore the error if it exists

-- First, let's try to add the column (will fail if it exists, but that's okay)
-- We'll use a procedure to handle this gracefully

DELIMITER $$

DROP PROCEDURE IF EXISTS AddOutstandingBalanceColumn$$
CREATE PROCEDURE AddOutstandingBalanceColumn()
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    
    -- Check if column exists
    SELECT COUNT(*) INTO column_exists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'bankdb'
    AND TABLE_NAME = 'loans'
    AND COLUMN_NAME = 'outstanding_balance';
    
    -- Add column if it doesn't exist
    IF column_exists = 0 THEN
        ALTER TABLE loans 
        ADD COLUMN outstanding_balance DOUBLE NOT NULL DEFAULT 0;
        
        -- Update existing loans to set outstanding_balance = amount
        UPDATE loans 
        SET outstanding_balance = amount;
        
        SELECT 'Added outstanding_balance column and updated existing loans' AS Result;
    ELSE
        SELECT 'Column outstanding_balance already exists' AS Result;
    END IF;
END$$

DELIMITER ;

-- Execute the procedure
CALL AddOutstandingBalanceColumn();

-- Drop the procedure after use
DROP PROCEDURE IF EXISTS AddOutstandingBalanceColumn;

-- Step 2: Update transactions table enum to include LOAN_REPAY
-- This will work even if LOAN_REPAY is already in the enum
ALTER TABLE transactions 
MODIFY COLUMN type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER', 'LOAN_REPAY') NOT NULL;

-- Verify the changes
SELECT 'Migration completed successfully!' AS Status;

