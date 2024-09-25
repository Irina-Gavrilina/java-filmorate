package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Не указан id фильма {}", newFilm);
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            validate(newFilm);
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException(String.format("Фильма с id = %d нет в базе", newFilm.getId()));
    }

    @Override
    public Optional<Film> findFilmById(long filmId) {
        return films.values()
                .stream()
                .filter(film -> film.getId() == filmId)
                .findFirst();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(Film film) {
        log.info("Запущен процесс валидации для фильма {}", film);
        if (film.getName().isBlank()) {
            log.error("Отсутствует название фильма {}", film);
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма превышает максимальную длину 200 символов");
            throw new ValidationException("Максимальная длина описания фильма 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма должна быть не раньше 28 декабря 1895 года {}", film);
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма отрицательное число или равно нулю {}", film);
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}