DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;
DROP TABLE IF EXISTS feeds CASCADE;

--для тестирования оставлю дропы

CREATE TABLE IF NOT EXISTS ratings_mpa
(
    id          int generated by default as identity primary key,
    name        varchar(255) NOT NULL UNIQUE,
    description varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
    id           bigint generated by default as identity primary key,
    name         varchar(255) NOT NULL,
    description  varchar(200),
    release_date date         NOT NULL,
    duration     int          NOT NULL,
    rating_id    int          NOT NULL REFERENCES ratings_mpa (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS genres
(
    id   int generated by default as identity primary key,
    name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  bigint REFERENCES films (id) ON DELETE CASCADE,
    genre_id int REFERENCES genres (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users
(
    id       bigint generated by default as identity primary key,
    email    varchar(255) NOT NULL,
    login    varchar(255) NOT NULL,
    name     varchar(255),
    birthday date
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id  bigint REFERENCES films (id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    friend_id bigint REFERENCES users (id) ON DELETE CASCADE,
    status boolean
);

CREATE TABLE IF NOT EXISTS feeds
(
    feed_timestamp TIMESTAMP,
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    --изменил в схеме типы для ENUM с не большим ограничением на символы
    event_type CHARACTER VARYING (6) NOT NULL,
    operation CHARACTER VARYING (6) NOT NULL,
    eventId bigint generated always as identity primary key,
    entityId bigint
);