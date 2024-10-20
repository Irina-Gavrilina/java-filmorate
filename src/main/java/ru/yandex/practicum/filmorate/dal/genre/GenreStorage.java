package ru.yandex.practicum.filmorate.dal.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> findAll();

    Optional<Genre> getById(int id);
}