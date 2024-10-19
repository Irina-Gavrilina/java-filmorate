package ru.yandex.practicum.filmorate.dal.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String GET_ALL_QUERY = """
            SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.rating_id AS mpa_id, mpa.name AS mpa_name 
            FROM films AS f 
            LEFT JOIN mpa_rating AS mpa ON mpa.id = f.rating_id
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO films(name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.rating_id AS mpa_id, mpa.name AS mpa_name,
            g.id AS genre_id, g.name AS genre_name
            FROM films AS f
            LEFT JOIN mpa_rating AS mpa ON mpa.id = f.rating_id
            LEFT JOIN film_genres AS fg ON fg.film_id = f.id
            LEFT JOIN genres AS g ON g.id = fg.genre_id
            WHERE f.id = ?
    """;

    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?
            """;

    private static final String REMOVE_QUERY = """
            DELETE FROM films
            WHERE id = ?
            """;

    private static final String GET_TOP_POPULAR_FILMS_QUERY = """
            SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.rating_id AS mpa_id, mpa.name AS mpa_name,
            COUNT(fl.user_id <> 0) AS likes
            FROM films AS f
            LEFT JOIN film_likes AS fl ON fl.film_id = f.id
            LEFT JOIN mpa_rating AS mpa ON mpa.id = f.rating_id
            GROUP BY f.id, mpa.name
            ORDER BY likes DESC, name
            LIMIT ?
            """;

    private static final String ADD_LIKE_TO_FILM_QUERY = """
            INSERT INTO film_likes (film_id, user_id)
            VALUES (?, ?)
            """;

    private static final String REMOVE_LIKE_FROM_FILM_QUERY = """
            DELETE FROM film_likes
            WHERE film_id = ?
            AND user_id = ?
            """;

    private static final String ADD_GENRE_TO_FILM_QUERY = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?)
            """;

    private static final String REMOVE_GENRE_FROM_FILM_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = ?
            """;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = findMany(GET_ALL_QUERY);
        findGenresForFilms(films);
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null);
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public void removeFilm(long filmId) {
        if (!delete(REMOVE_QUERY, filmId)) {
            throw new InternalServerException("Не найден фильм для удаления");
        }
    }

    @Override
    public Optional<Film> findFilmById(long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = findMany(GET_TOP_POPULAR_FILMS_QUERY, count);
        findGenresForFilms(films);
        return films;
    }

    @Override
    public void addGenreToFilm(Long filmId, int genreId) {
        update(ADD_GENRE_TO_FILM_QUERY, filmId, genreId);
    }

    @Override
    public void removeGenreFromFilm(Long filmId) {
        delete(REMOVE_GENRE_FROM_FILM_QUERY, filmId);
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        update(ADD_LIKE_TO_FILM_QUERY, filmId, userId);
    }

    @Override
    public void removeLikeFromFilm(Long filmId, Long userId) {
        if (!delete(REMOVE_LIKE_FROM_FILM_QUERY, filmId, userId)) {
            throw new InternalServerException("Не найден лайк для удаления");
        }
    }

    private void findGenresForFilms(List<Film> films) {
        ArrayList<Long> filmIds = new ArrayList<>();
        for (Film film : films) {
            filmIds.add(film.getId());
        }
        List<FilmGenre> filmGenres = jdbc.query(connection -> {
            StringBuilder query = new StringBuilder();
            query.append("SELECT g.id AS genre_id, g.name AS genre_name, fg.film_id " +
                    "FROM film_genres AS fg LEFT join genres AS g ON fg.genre_id = g.id " +
                    "WHERE film_id IN (");
            for (int i = 0; i < filmIds.size(); i++) {
                if (i == 0) {
                    query.append("?");
                    continue;
                }
                query.append(", ?");
            }
            query.append(")");

            PreparedStatement stmt = connection.prepareStatement(query.toString());

            for (int i = 0; i < filmIds.size(); i++) {
                stmt.setLong(i + 1, filmIds.get(i));
            }
            return stmt;
        }, mapFilmGenre);

        Map<Long, Film> mapFilmIdToFilm = new HashMap<>();

        for (Film film : films) {
            mapFilmIdToFilm.put(film.getId(), film);
        }

        for (FilmGenre filmGenre : filmGenres) {
            Film film = mapFilmIdToFilm.get(filmGenre.getFilmId());
            if (film.getGenres() == null) {
                film.setGenres(new ArrayList<>());
            }
            film.getGenres().add(Genre.builder()
                    .id((int) filmGenre.getGenreId())
                    .name(filmGenre.getGenreName())
                    .build());
        }
    }

    @Data
    @Builder
    private static class FilmGenre {
        private long filmId;
        private long genreId;
        private String genreName;
    }

    private final RowMapper<FilmGenre> mapFilmGenre = (ResultSet rs, int rowNum) -> FilmGenre.builder()
            .filmId(rs.getLong("film_id"))
            .genreId(rs.getLong("genre_id"))
            .genreName(rs.getString("name"))
            .build();
}