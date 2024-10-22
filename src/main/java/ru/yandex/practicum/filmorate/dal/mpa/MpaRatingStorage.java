package ru.yandex.practicum.filmorate.dal.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.List;
import java.util.Optional;

public interface MpaRatingStorage {

    List<MpaRating> findAll();

    Optional<MpaRating> getById(int id);
}