package ru.yandex.practicum.filmorate.dto.film;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequest;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest {

    long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    MpaRequest mpa;
    List<GenreRequest> genres;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasRatingId() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return genres != null;
    }
}