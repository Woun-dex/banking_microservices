CREATE TABLE users (
         id UUID PRIMARY KEY,
         username VARCHAR(100) NOT NULL,
         email VARCHAR(150) NOT NULL UNIQUE,
         password VARCHAR(100) NOT NULL,
         role VARCHAR(50) NOT NULL
);
