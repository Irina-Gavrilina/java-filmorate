package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

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