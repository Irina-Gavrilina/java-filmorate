package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MpaRating {

    int id;
    String name;
}