CREATE TABLE transaction (
                             trx UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             transaction_ref VARCHAR(255) NOT NULL UNIQUE,
                             from_account_id UUID,
                             to_account_id UUID,
                             amount NUMERIC(19, 4),
                             status VARCHAR(50),
                             created_at TIMESTAMP NOT NULL UNIQUE,
                             updated_at TIMESTAMP NOT NULL UNIQUE
);
