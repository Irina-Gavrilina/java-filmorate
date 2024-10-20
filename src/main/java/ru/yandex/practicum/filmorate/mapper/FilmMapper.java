package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmResponse;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .build();
        if (request.getMpa() != null) {
            film.setMpa(MpaRatingMapper.mapToMpaRating(request.getMpa()));
        }
        if (request.getGenres() != null) {
            film.setGenres(request.getGenres().stream().map(GenreMapper::mapToGenre).toList());
        }
        return film;
    }

    public static Film mapToFilm(UpdateFilmRequest request) {
        Film film = Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .build();
        if (request.getMpa() != null) {
            film.setMpa(MpaRatingMapper.mapToMpaRating(request.getMpa()));
        }
        if (request.getGenres() != null) {
            film.setGenres(request.getGenres().stream().map(GenreMapper::mapToGenre).toList());
        }
        return film;
    }

    public static FilmResponse mapToFilmResponse(Film film) {
        FilmResponse filmResponse = FilmResponse.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .build();
        if (film.getMpa() != null) {
            filmResponse.setMpa(MpaRatingMapper.mapToMpaRatingResponse(film.getMpa()));
        }
        if (film.getGenres() != null) {
            filmResponse.setGenres(film.getGenres().stream().map(GenreMapper::mapToGenreResponse).toList());
        }
        return filmResponse;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }

        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }

        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }

        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }

        if (request.hasRatingId()) {
            film.setMpa(MpaRatingMapper.mapToMpaRating(request.getMpa()));
        }

        if (request.hasGenres()) {
            if (film.getGenres() == null) {
                film.setGenres(new ArrayList<>());
            }

            film.setGenres(request.getGenres().stream().map(GenreMapper::mapToGenre).toList());
        } else {
            film.setGenres(null);
        }
        return film;
    }
}