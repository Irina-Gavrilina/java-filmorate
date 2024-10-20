package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    void removeFilm(long filmId);

    Optional<Film> findFilmById(long filmId);

    List<Film> getPopularFilms(int count);

    void addGenreToFilm(Long filmId, int genreId);

    void removeGenreFromFilm(Long filmId);

    void addLikeToFilm(Long filmId, Long userId);

    void removeLikeFromFilm(Long filmId, Long userId);
}