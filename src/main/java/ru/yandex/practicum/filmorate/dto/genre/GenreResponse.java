package ru.yandex.practicum.filmorate.dto.genre;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    int id;
    String name;
}
