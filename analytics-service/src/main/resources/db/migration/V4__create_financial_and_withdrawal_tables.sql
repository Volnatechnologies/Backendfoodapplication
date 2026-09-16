-- ====================================================================
-- V4: Create Financial Accounts, Bank Accounts, Withdrawals, and Transactions
-- ====================================================================

CREATE TABLE IF NOT EXISTS business_financial_accounts (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL UNIQUE,
    available_balance NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    pending_balance NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    monthly_goal NUMERIC(14,2) NOT NULL DEFAULT 42500.00,
    monthly_goal_achieved NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    next_payout_date DATE,
    verified_by VARCHAR(100) DEFAULT 'Partner Central Finance',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bank_accounts (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_holder_name VARCHAR(150) NOT NULL,
    account_type VARCHAR(50) NOT NULL DEFAULT 'Checking Account',
    masked_account_number VARCHAR(50) NOT NULL,
    routing_number VARCHAR(50),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bank_accounts_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bank_accounts_business_id ON bank_accounts(business_id);

CREATE TABLE IF NOT EXISTS withdrawals (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    bank_account_id UUID NOT NULL,
    amount NUMERIC(12,2) NOT NULL CHECK (amount >= 100.00),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    idempotency_key VARCHAR(100) UNIQUE,
    transaction_reference VARCHAR(100) NOT NULL,
    failure_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_withdrawals_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_withdrawals_bank FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_withdrawals_business_id ON withdrawals(business_id);
CREATE INDEX IF NOT EXISTS idx_withdrawals_status ON withdrawals(status);
CREATE INDEX IF NOT EXISTS idx_withdrawals_idempotency_key ON withdrawals(idempotency_key);

CREATE TABLE IF NOT EXISTS financial_transactions (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    description VARCHAR(255) NOT NULL,
    reference_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_tx_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_financial_tx_business_id ON financial_transactions(business_id);
CREATE INDEX IF NOT EXISTS idx_financial_tx_business_created_at ON financial_transactions(business_id, created_at);
