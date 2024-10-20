package ru.yandex.practicum.filmorate.dto.film;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.dto.genre.GenreRequest;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequest;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFilmRequest {

    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    LocalDate releaseDate;
    @NonNull
    Integer duration;
    MpaRequest mpa;
    List<GenreRequest> genres;
}