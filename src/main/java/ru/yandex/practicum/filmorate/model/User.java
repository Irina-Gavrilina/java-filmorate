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
public class User {

    Long id;
    @NonNull
    String email;
    @NonNull
    String login;
    String name;
    @NonNull
    LocalDate birthday;
    Set<Long> friends;

    public void addFriend(long userId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(userId);
    }

    public void removeFriend(long userId) {
        friends.remove(userId);
    }
}