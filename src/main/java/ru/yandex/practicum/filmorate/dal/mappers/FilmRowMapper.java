package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = MpaRating.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(mpaRating)
                .build();

        try {
            do {
                int id = rs.getInt("genre_id");
                if (id == 0) {
                    continue;
                }
                Genre genre = Genre.builder()
                        .id(id)
                        .name(rs.getString("genre_name"))
                        .build();
                if (film.getGenres() == null) {
                    film.setGenres(new ArrayList<>());
                }
                film.getGenres().add(genre);
            } while (rs.next());
        } catch (SQLException e) {
            //do nothing
        }
        return film;
    }
}