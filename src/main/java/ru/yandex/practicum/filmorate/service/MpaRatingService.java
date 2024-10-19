package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaRatingStorage;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRatingResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MpaRatingService {

    private MpaRatingStorage mpaRatingStorage;

    public List<MpaRatingResponse> findAll() {
        return mpaRatingStorage.findAll().stream().map(MpaRatingMapper::mapToMpaRatingResponse).toList();
    }

    public MpaRatingResponse getById(int id) {
        return mpaRatingStorage.getById(id).map(MpaRatingMapper::mapToMpaRatingResponse)
                .orElseThrow(() -> new NotFoundException(String.format("Рейтинг с id = %d не найден", id)));
    }
}