package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDbStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Genre> getAllGenre () {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name"))
        );
    }

    public Genre getGenreById (Integer genreId) {

        if (genreId == null) {
            throw new ValidationException("Illegal Arguments!");
        }

        Genre genre;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE id = ?", genreId);
        if (sqlRowSet.first()) {
            genre = new Genre(sqlRowSet.getInt("id"), sqlRowSet.getString("name"));
        } else {
            throw new UserOrFilmNotFoundException(genreId + " Not founded");
        }
        return genre;
    }

    public void addGenreToFilm(Film film) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
        }
    }

    public void deleteGenreFromFilm (Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
    }

    public List<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT genre_id, name FROM film_genres" +
                " INNER JOIN genres ON genre_id = id WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"), rs.getString("name")), filmId
        );
    }
}

