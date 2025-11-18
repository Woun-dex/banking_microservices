CREATE TABLE transactions
(
    trx             UUID         NOT NULL,
    transaction_ref VARCHAR(255) NOT NULL,
    from_account_id UUID,
    to_account_id   UUID,
    amount          DECIMAL,
    status          VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (trx)
);

ALTER TABLE transactions
    ADD CONSTRAINT uc_fe14dc2e39b6a5763105bd5fc UNIQUE (transaction_ref);

ALTER TABLE transactions
    ADD CONSTRAINT uc_transactions_createdat UNIQUE (created_at);

ALTER TABLE transactions
    ADD CONSTRAINT uc_transactions_updatedat UNIQUE (updated_at);