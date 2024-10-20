package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaRequest {

    int id;
}