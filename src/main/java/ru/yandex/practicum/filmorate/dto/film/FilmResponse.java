package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponse;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRatingResponse;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
    MpaRatingResponse mpa;
    List<GenreResponse> genres;
}