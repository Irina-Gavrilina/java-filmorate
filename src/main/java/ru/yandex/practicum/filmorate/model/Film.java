package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    Long id;
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    LocalDate releaseDate;
    @NonNull
    Integer duration;
    Set<Long> likes;

    public void addLike(long userId) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(userId);
    }

    public void removeLike(long userId) {
        likes.remove(userId);
    }
}