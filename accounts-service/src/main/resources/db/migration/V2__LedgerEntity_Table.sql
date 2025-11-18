CREATE TABLE ledger_entries (
                                ledger_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                account_id UUID,
                                transaction_ref VARCHAR(255),
                                amount NUMERIC(19,4),
                                type VARCHAR(50),
                                created_at TIMESTAMP
);
