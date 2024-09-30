package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Поступил запрос GET на получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос POST на создание фильма {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.info("Получен запрос PUT на обновление фильма {}", newFilm);
        return filmService.updateFilm(newFilm);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable("filmId") long filmId) {
        log.info("Получен запрос DELETE на удаление фильма с id = {}", filmId);
        filmService.removeFilm(filmId);
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable("filmId") long filmId) {
        log.info("Поступил запрос GET на получение данных о фильме с id = {}", filmId);
        Optional<Film> optFilm = filmService.getFilmById(filmId);
        if (optFilm.isPresent()) {
            return optFilm.get();
        }
        throw new NotFoundException(String.format("Фильм с id = %d не найден", filmId));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long id,
                        @PathVariable("userId") long userId) {
        log.info("Получен запрос PUT на добавление лайка к фильму с id = {} от пользователя с id = {}",
                id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long id,
                           @PathVariable("userId") long userId) {
        log.info("Получен запрос DELETE на удаление лайка у фильма с id = {} от пользователя с id = {}",
                id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Поступил запрос GET на получение {} наиболее популярных фильмов по количеству лайков", count);
        return filmService.getPopularFilms(count);
    }
}