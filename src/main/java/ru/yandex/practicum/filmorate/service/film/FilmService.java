package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Optional<Film> getFilmById(long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public void addLike(long filmId, long userId) {
        Film film;
        User user;
        Optional<Film> optFilm = filmStorage.findFilmById(filmId);
        if (optFilm.isPresent()) {
            film = optFilm.get();
        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException(String.format("Фильма с id = %d нет в базе", filmId));
        }
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        film.addLike(user.getId());
    }

    public void removeLike(long filmId, long userId) {
        Film film;
        User user;
        Optional<Film> optFilm = filmStorage.findFilmById(filmId);
        if (optFilm.isPresent()) {
            film = optFilm.get();
        } else {
            log.error("Фильм с id = {} не был найден", filmId);
            throw new NotFoundException(String.format("Фильма с id = %d нет в базе", filmId));
        }
        Optional<User> optUser = userStorage.findUserById(userId);
        if (optUser.isPresent()) {
            user = optUser.get();
        } else {
            log.error("Пользователь с id = {} не был найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        if (film.getLikes().contains(user.getId())) {
            film.removeLike(user.getId());
        } else {
            log.error("Лайк пользователя с id = {} к фильму с id = {} не найден",
                    userId, filmId);
            throw new NotFoundException(String.format("Лайк пользователя с id = %d к фильму с id = %d не найден",
                    userId, filmId));
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}