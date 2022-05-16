drop table if exists user_secure;
drop table if exists messages;
drop table if exists chats;
drop table if exists users;

CREATE TABLE users
(
    id         bigserial primary key,
    user_name  varchar(128) unique NOT NULL,
    first_name varchar(128)        NOT NULL,
    last_name  varchar(128)        NOT NULL,
    image_path varchar(128)        NOT NULL
);

create table user_secure
(
    id            bigserial primary key,
    user_login     varchar(128) unique NOT NULL,
    user_password varchar(128)        not null
);

create table chats
(
    id             bigserial primary key,
    owner_user_id  bigserial REFERENCES users (id) ON DELETE CASCADE,
    friend_user_id bigserial REFERENCES users (id) ON DELETE CASCADE
);

create table messages
(
    id           bigserial primary key,
    chat_id      bigserial REFERENCES chats (id) ON DELETE CASCADE,
    user_from_id bigserial REFERENCES users (id),
    user_to_id   bigserial REFERENCES users (id),
    text         varchar   not null,
    time         timestamp not null
);