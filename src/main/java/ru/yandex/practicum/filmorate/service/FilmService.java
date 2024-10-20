package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {

    private FilmStorage filmStorage;
    private GenreService genreService;
    private MpaRatingService mpaRatingService;

    public List<FilmResponse> getAllFilms() {
        return filmStorage.getAllFilms().stream()
                .map(FilmMapper::mapToFilmResponse)
                .toList();
    }

    public FilmResponse addFilm(NewFilmRequest film) {
        validateFilm(FilmMapper.mapToFilm(film));
        if (film.getMpa() != null) {
            try {
                mpaRatingService.getById(film.getMpa().getId());
            } catch (Exception e) {
                throw new ValidationException(String.format("Рейтинг с id = %s не найден", film.getMpa()));
            }
        }
        if (CollectionUtils.isNotEmpty(film.getGenres())) {
            for (var genre : film.getGenres()) {
                try {
                    genreService.getById(genre.getId());
                } catch (Exception e) {
                    throw new ValidationException(String.format("Жанр с id = %s не найден", genre.getId()));
                }
            }
        }

        FilmResponse filmResponse = FilmMapper.mapToFilmResponse(filmStorage.addFilm(FilmMapper.mapToFilm(film)));

        if (CollectionUtils.isNotEmpty(film.getGenres())) {
            Set<Integer> createdGenreIds = new HashSet<>();
            for (var genre : film.getGenres()) {
                if (!createdGenreIds.contains(genre.getId())) {
                    filmStorage.addGenreToFilm(filmResponse.getId(), genre.getId());
                    createdGenreIds.add(genre.getId());
                }
            }

            filmResponse.setGenres(film.getGenres().stream()
                    .map(GenreMapper::mapToGenreResponse)
                    .toList());
        }

        return filmResponse;
    }

    public FilmResponse updateFilm(long filmId, UpdateFilmRequest request) {
        Film oldFilm = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
        validateFilm(FilmMapper.mapToFilm(request));
        filmStorage.removeGenreFromFilm(filmId);
        if (request.hasGenres()) {
            List<Integer> newGenreIds = request.getGenres().stream()
                    .map(GenreRequest::getId)
                    .toList();
            for (Integer newGenreId : newGenreIds) {
                filmStorage.addGenreToFilm(filmId, newGenreId);
            }
        }
        Film updatedFilm = FilmMapper.updateFilmFields(oldFilm, request);
        filmStorage.updateFilm(updatedFilm);
        return FilmMapper.mapToFilmResponse(updatedFilm);
    }

    public void removeFilm(long filmId) {
        filmStorage.removeFilm(filmId);
    }

    public FilmResponse findFilmById(long filmId) {
        return filmStorage.findFilmById(filmId)
                .map(FilmMapper::mapToFilmResponse)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %d не найден", filmId)));
    }

    public void addLike(long filmId, long userId) {
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.removeLikeFromFilm(filmId, userId);
    }

    public List<FilmResponse> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count)
                .stream()
                .map(FilmMapper::mapToFilmResponse)
                .toList();
    }

    private void validateFilm(Film film) {
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