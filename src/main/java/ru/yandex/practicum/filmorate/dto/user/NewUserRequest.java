package ru.yandex.practicum.filmorate.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    String email;
    String login;
    String name;
    LocalDate birthday;
}