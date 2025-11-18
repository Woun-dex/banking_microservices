CREATE TABLE accounts (
                          account_id UUID PRIMARY KEY,
                          user_id UUID NOT NULL,
                          balance NUMERIC(19,2) NOT NULL DEFAULT 0,
                          currency VARCHAR(10) NOT NULL,
                          version INTEGER NOT NULL DEFAULT 0
);
