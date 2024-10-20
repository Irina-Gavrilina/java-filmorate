package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreService {

    private GenreStorage genreStorage;

    public List<GenreResponse> findAll() {
        return genreStorage.findAll().stream()
                .map(GenreMapper::mapToGenreResponse)
                .toList();
    }

    public GenreResponse getById(int id) {
        return genreStorage.getById(id)
                .map(GenreMapper::mapToGenreResponse)
                .orElseThrow(() -> new NotFoundException(String.format("Жанр с id = %d не найден", id)));
    }
}