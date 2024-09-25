package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    Optional<Film> findFilmById(long filmId);
}