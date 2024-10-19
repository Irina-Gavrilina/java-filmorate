package ru.yandex.practicum.filmorate.dal.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate.*")
public class FilmDbStorageTest {

    private final FilmStorage filmStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    public void afterEach() {
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    public void addFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 6, 1))
                .mpa(MpaRating.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .build();
        filmStorage.addFilm(film);
        final Long filmId = film.getId();
        Optional<Film> filmFromBaseOptional = filmStorage.findFilmById(filmId);
        assertThat(filmFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(filmFromBase ->
                        assertThat(filmFromBase)
                                .hasFieldOrPropertyWithValue("id", filmId));
    }

    @Test
    public void getAllFilms() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2005, 5, 1))
                .mpa(MpaRating.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .build();
        filmStorage.addFilm(film);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        assertThat(films.size()).isEqualTo(2);
    }

    @Test
    public void findFilmById() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 6, 1))
                .mpa(MpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();
        film = filmStorage.addFilm(film);
        final Long filmId = film.getId();
        Optional<Film> filmFromBaseOptional = filmStorage.findFilmById(filmId);
        assertThat(filmFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(filmFromBase ->
                        assertThat(filmFromBase)
                                .hasFieldOrPropertyWithValue("id", filmId));
    }

    @Test
    public void updateFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 6, 1))
                .mpa(MpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();
        film = filmStorage.addFilm(film);
        film.setName("newName");
        filmStorage.updateFilm(film);
        final Long filmId = film.getId();
        Optional<Film> filmFromBaseOptional = filmStorage.findFilmById(filmId);
        assertThat(filmFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(filmFromBase ->
                        assertThat(filmFromBase)
                                .hasFieldOrPropertyWithValue("name", "newName"));
    }

    @Test
    public void addGenreToFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 6, 1))
                .mpa(MpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();
        film = filmStorage.addFilm(film);
        final Long filmId = film.getId();
        filmStorage.addGenreToFilm(filmId, 1);
        Optional<Film> filmFromBaseOptional = filmStorage.findFilmById(filmId);
        assertThat(filmFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(filmFromBase ->
                        assertThat(filmFromBase.getGenres().getFirst())
                                .hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    public void removeGenreFromFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 6, 1))
                .mpa(MpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();
        film = filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(film.getId(), 1);
        final Long filmId = film.getId();
        filmStorage.removeGenreFromFilm(filmId);
        Optional<Film> filmFromBaseOptional = filmStorage.findFilmById(filmId);
        assertThat(filmFromBaseOptional)
                .isPresent()
                .hasValueSatisfying(filmFromBase ->
                        assertThat(filmFromBase.getGenres()).isEqualTo(null));
    }

    @Test
    public void getPopularFilms() {
        Film film1 = Film.builder()
                .name("name")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 6, 1))
                .mpa(MpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();
        filmStorage.addFilm(film1);

        Film film2 = Film.builder()
                .name("anotherName")
                .description("anotherDescription")
                .duration(150)
                .releaseDate(LocalDate.of(2005, 5, 1))
                .mpa(MpaRating.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .build();
        filmStorage.addFilm(film2);

        User user = User.builder()
                .email("mail@yandex.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2005, 5, 1))
                .build();
        userDbStorage.createUser(user);
        filmStorage.addLikeToFilm(film2.getId(), user.getId());
        List<Film> films = filmStorage.getPopularFilms(1);
        assertThat(films.getFirst().getId()).isEqualTo(2L);
    }
}