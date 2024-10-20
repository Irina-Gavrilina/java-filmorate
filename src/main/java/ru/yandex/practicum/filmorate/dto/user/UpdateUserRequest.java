package ru.yandex.practicum.filmorate.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {

    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasLogin() {
        return login != null;
    }

    public boolean hasName() {
        return name != null;
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}