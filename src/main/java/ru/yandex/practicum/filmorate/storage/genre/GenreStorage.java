package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public class GenreStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Genre> getAllGenre () {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getLong("id"),
                rs.getString("name"))
        );
    }

    public Genre getGenreById (Long genreId) {
        if (genreId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        Genre genre;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM ratings_mpa WHERE id = ?", genreId);
        if (sqlRowSet.first()) {
            genre = new Genre(sqlRowSet.getLong("id"), sqlRowSet.getString("name"));
        } else {
            throw new UserOrFilmNotFoundException(genreId + " не найден!");
        }
        return genre;
    }
}

