CREATE TABLE accounts (
                          id SERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          balance NUMERIC(19,2) NOT NULL DEFAULT 0,
                          currency VARCHAR(10) NOT NULL,
                          version INTEGER NOT NULL DEFAULT 0
);
