package ru.yandex.practicum.filmorate.dto.film;


import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequest;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UpdateFilmRequest {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRequest mpa;
    private List<GenreRequest> genres;

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