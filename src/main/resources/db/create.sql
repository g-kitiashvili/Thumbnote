create database thumbnote_db;
use thumbnote_db;
SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS notebooks;
DROP TABLE IF EXISTS tags;
SET FOREIGN_KEY_CHECKS=1;


CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY  ,
                       username VARCHAR(255) NOT NULL unique,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL unique
);

create table notebooks(
    notebook_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notebook_name VARCHAR(255),
    description tinytext,
    user_id bigINT,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    unique key(notebook_id,user_id)


);
create table notes (
        note_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        notebook_id BIGINT,
        upload_date timestamp default current_timestamp,
        last_access_date timestamp,
        note_name varchar(255),
        note mediumtext,
        FOREIGN KEY (user_id) REFERENCES users(user_id),
        unique(note_name,user_id),
#         unique key(note_id,user_id)
        FOREIGN KEY (notebook_id) REFERENCES notebooks(notebook_id)

);

create table tags(
        tag_id BIGINT auto_increment primary key ,
        note_id BIGINT,
        tag_name VARCHAR(255) ,
        FOREIGN KEY (note_id) REFERENCES notes (note_id),
        unique key(note_id,tag_name)


);


