package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void whenFilmFieldsAreOkThenDoNotThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        assertDoesNotThrow(() -> filmController.validate(film));
    }

    @Test
    public void whenFilmNameIsEmptyThenThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void whenFilmNameIsBlankThenThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name(" ")
                .description("description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void whenFilmDescriptionIsMoreThan200CharactersThenThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("Harry Potter is a film series based on the Harry Potter series of novels " +
                        "by J. K. Rowling. The series was produced and distributed by Warner Bros. Pictures and " +
                        "consists of eight fantasy films, beginning with Harry Potter and the Philosopher's Stone " +
                        "(2001) and culminating with Harry Potter and the Deathly Hallows – Part 2 (2011).")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void whenFilmDateOfReleaseIsBefore1895_12_28ThenThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1681, 10, 10))
                .duration(150)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void whenFilmDurationIsNegativeThenThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1981, 10, 10))
                .duration(-150)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void whenFilmDurationIsZeroThenThrowValidationException() {
        Film film = Film.builder()
                .id(1L)
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1981, 10, 10))
                .duration(0)
                .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    public void shouldReturnAllFilms() {
        Film film1 = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        filmController.addFilm(film1);
        Film film2 = Film.builder()
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(2005, 8, 30))
                .duration(180)
                .build();
        filmController.addFilm(film2);
        Collection<Film> firstListOfFilms = List.of(film1, film2);
        List<Film> secondListOfFilms = filmController.getAllFilms().stream().toList();
        assertEquals(firstListOfFilms, secondListOfFilms);
    }

    @Test
    public void shouldAddFilm() {
        Film film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        filmController.addFilm(film);
        assertEquals(filmController.getAllFilms().size(), 1);
        assertEquals(filmController.getAllFilms().stream().toList().getFirst(), film);
    }

    @Test
    public void shouldUpdateFilm() {
        Film film1 = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        filmController.addFilm(film1);
        Film film2 = Film.builder()
                .id(film1.getId())
                .name("newName")
                .description("newDescription")
                .releaseDate(LocalDate.of(2001, 7, 15))
                .duration(160)
                .build();
        assertEquals(filmController.getAllFilms().size(), 1);
        assertEquals(filmController.getAllFilms().stream().toList().getFirst(), film1);
        filmController.updateFilm(film2);
        assertEquals(filmController.getAllFilms().size(), 1);
        assertEquals(filmController.getAllFilms().stream().toList().getFirst().getName(), "newName");
        assertEquals(filmController.getAllFilms().stream().toList().getFirst().getDescription(), "newDescription");
        assertEquals(filmController.getAllFilms().stream().toList().getFirst().getReleaseDate(),
                LocalDate.of(2001, 7, 15));
        assertEquals(filmController.getAllFilms().stream().toList().getFirst().getDuration(), 160);
    }

    @Test
    public void shouldNotUpdateFilmWhenFieldIdIsEmpty() {
        Film film1 = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        filmController.addFilm(film1);
        Film film2 = Film.builder()
                .name("newName")
                .description("newDescription")
                .releaseDate(LocalDate.of(2001, 7, 15))
                .duration(160)
                .build();
        assertEquals(filmController.getAllFilms().size(), 1);
        assertEquals(filmController.getAllFilms().stream().toList().getFirst(), film1);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
    }

    @Test
    public void shouldNotUpdateFilmWhenThereIsNoSuchId() {
        Film film1 = Film.builder()
                .name("film1")
                .description("description1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(150)
                .build();
        filmController.addFilm(film1);
        Film film2 = Film.builder()
                .id(2L)
                .name("newName")
                .description("newDescription")
                .releaseDate(LocalDate.of(2001, 7, 15))
                .duration(160)
                .build();
        assertEquals(filmController.getAllFilms().size(), 1);
        assertEquals(filmController.getAllFilms().stream().toList().getFirst(), film1);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film2));
    }
}