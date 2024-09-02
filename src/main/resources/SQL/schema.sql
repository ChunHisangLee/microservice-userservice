CREATE DATABASE userdb;

\c userdb

-- After connecting to the `userdb` database, run the following script:

-- Drop users' table if it exists
DROP TABLE IF EXISTS users CASCADE;

-- Create users' table
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    CONSTRAINT email_unique UNIQUE (email)
);

-- Create indexes
CREATE INDEX idx_email ON users(email);
