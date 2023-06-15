package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Component
public class GenreStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Genre> getAllGenre () {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getByte("id"),
                rs.getString("name"))
        );
    }

    public Genre getGenreById (Byte genreId) {
        if (genreId == null) {
            throw new ValidationException("id was not selected");
        }

        Genre genre;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE id = ?", genreId);
        if (sqlRowSet.first()) {
            genre = new Genre(sqlRowSet.getByte("id"), sqlRowSet.getString("name"));
        } else {
            throw new UserOrFilmNotFoundException(genreId + " not founded");
        }
        return genre;
    }

    public void addGenreToFilm(Film film) {
        String sql = "SELECT genre_id, name FROM film_genres" +
                "INNER JOIN genres ON genre_id = id WHERE film_id = ?";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                         genre.getId(), film.getId());
            }
        }
    }
}

