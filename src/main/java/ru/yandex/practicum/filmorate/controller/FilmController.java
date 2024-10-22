package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmResponse> getAllFilms() {
        log.info("Поступил запрос GET на получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponse addFilm(@RequestBody NewFilmRequest film) {
        log.info("Получен запрос POST на создание фильма {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmResponse updateFilm(@RequestBody UpdateFilmRequest request) {
        log.info("Получен запрос PUT на обновление фильма {}", request);
        return filmService.updateFilm(request.getId(), request);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable("filmId") long filmId) {
        log.info("Получен запрос DELETE на удаление фильма с id = {}", filmId);
        filmService.removeFilm(filmId);
    }

    @GetMapping("/{filmId}")
    public FilmResponse getFilmById(@PathVariable("filmId") long filmId) {
        log.info("Поступил запрос GET на получение данных о фильме с id = {}", filmId);
        return filmService.findFilmById(filmId);
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
    public List<FilmResponse> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Поступил запрос GET на получение {} наиболее популярных фильмов по количеству лайков", count);
        return filmService.getPopularFilms(count);
    }
}