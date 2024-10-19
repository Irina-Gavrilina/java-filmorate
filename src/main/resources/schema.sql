DROP TABLE IF EXISTS mpa_rating, films, genres, film_genres, users, film_likes, friendship;

CREATE TABLE IF NOT EXISTS mpa_rating (
    id integer GENERATED BY DEFAULT AS identity PRIMARY KEY,
    name varchar(300) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED BY DEFAULT AS identity PRIMARY KEY,
    name varchar(300) NOT NULL,
    description varchar(300) NOT NULL,
    release_date date NOT NULL,
    duration integer NOT NULL,
    rating_id integer NOT NULL REFERENCES mpa_rating(id)
);

CREATE TABLE IF NOT EXISTS genres (
    id integer GENERATED BY DEFAULT AS identity PRIMARY KEY,
    name varchar(300) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT REFERENCES films(id) ON DELETE CASCADE,
    genre_id integer REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT exists users (
    id BIGINT GENERATED BY DEFAULT as IDENTITY PRIMARY KEY,
    email varchar(300) UNIQUE NOT NULL,
    login varchar(300) UNIQUE NOT NULL,
    name varchar(300),
    birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT REFERENCES films(id) ON DELETE CASCADE,
    user_id BIGINT references users (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT exists friendship (
    inviter_id BIGINT references users (id) ON DELETE CASCADE,
    invitee_id BIGINT references users (id) ON DELETE CASCADE,
    status varchar(300) NOT NULL,
    PRIMARY KEY (inviter_id, invitee_id)
);