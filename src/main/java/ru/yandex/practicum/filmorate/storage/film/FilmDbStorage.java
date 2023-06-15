package ru.yandex.practicum.filmorate.storage.film;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.Date;
import java.sql.PreparedStatement;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new Film (
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new MPA(rs.getInt("rating_id"))

        )
        ));
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        validate(film);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            film.setMpa(mpaService.getMpaRateById(mpaService.getMpaRateById(film.getMpa().getId()).getId()));
            stmt.setInt(5, film.getMpa().getId());
            film.setRate(0);


            return stmt;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            film.setId(generatedId.longValue());
        }
        for (Genre genre : film.getGenres()) {
            genreService.addGenreToFilm(film);
        }
        log.info("film has been created");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);

        if (film.getId() != null) {
            String sqlQuery = "UPDATE films SET " +
                    "name = ?, description = ?, release_date = ?, duration = ?, " +
                    "rating_id = ? WHERE id = ?";

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setString(3, film.getReleaseDate().toString());
                stmt.setString(4, film.getDuration().toString());
                stmt.setInt(5, film.getRate());
                stmt.setInt(6,film.getMpa().getId());

                return stmt;
            });

        } else {
            throw new UserOrFilmNotFoundException("Film by name" + film.getName() + "doesn't exist");
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (resultSet, rowNum) -> {
                Film film = new Film(null, null,null,null,null ,null);

                film.setId(resultSet.getLong("id"));
                film.setName(resultSet.getString("name"));
                film.setDescription(resultSet.getString("description"));
                film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
                film.setDuration(resultSet.getInt("duration"));
                film.setMpa(mpaService.getMpaRateById(resultSet.getInt("rating_id")));
                return film;
            });
        } catch (EmptyResultDataAccessException e) {
            throw new UserOrFilmNotFoundException(e.getMessage());
        }
    }

    @Override
    public void deleteFilmById(Long filmId) {
        String sqlQuery = "DELETE FROM films ";
        if (jdbcTemplate.update(sqlQuery, filmId) == 0) {
            throw new UserOrFilmNotFoundException("Film with id" + filmId + "not founded");
        } else {
            jdbcTemplate.update(sqlQuery, filmId);
            log.info("film with id=" + filmId + "has been deleted");
        }
    }

    private Film validate(Film film) {
        if (film.getName() != null &&
                !film.getName().isEmpty() &&
                film.getReleaseDate().isAfter(LocalDate.of(1895, 1, 28)) &&
                film.getDescription().length() < 200 && film.getDuration() > 0) {
            return film;
        } else {
            log.error("Illegal arguments for Film");
            throw new ValidationException("Illegal arguments for Film");
        }
    }
}

