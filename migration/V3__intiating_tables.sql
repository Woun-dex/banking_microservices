CREATE TABLE accounts
(
    account_id UUID NOT NULL,
    user_id    UUID,
    balance    DECIMAL,
    currency   VARCHAR(255),
    version    INTEGER,
    CONSTRAINT pk_accounts PRIMARY KEY (account_id)
);

CREATE TABLE ledger_entries
(
    ledger_id       UUID NOT NULL,
    account_id      UUID,
    transaction_ref VARCHAR(255),
    amount          DECIMAL,
    type            VARCHAR(255),
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_ledger_entries PRIMARY KEY (ledger_id)
);