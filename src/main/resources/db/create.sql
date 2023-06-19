create database thumbnote_db;
use thumbnote_db;
CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL
);
select * from users