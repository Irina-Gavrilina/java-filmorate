package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequest;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class NewFilmRequest {
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private Integer duration;
    private MpaRequest mpa;
    private List<GenreRequest> genres;
}