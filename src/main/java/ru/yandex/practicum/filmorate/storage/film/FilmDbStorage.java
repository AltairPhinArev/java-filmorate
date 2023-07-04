package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;

import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;
    LikeDbStorage likeDbStorage;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService,
                         LikeDbStorage likeDbStorage, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new Film(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(likeDbStorage.getLikes(rs.getLong("id"))),
                        new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))),
                        new MPA(rs.getInt("rating_id"),
                                mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                )
        );
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());

            film.setMpa(mpaService.getMpaRateById(film.getMpa().getId()));
            return stmt;

        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            film.setId(generatedId.longValue());
        }
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .forEach(genre -> genreService.addGenreToFilm(film));
        }
        log.info("film has been created");
        return film;
    }


    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, " + "rating_id = ? WHERE id = ?";

        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) != 0) {
            film.setMpa(mpaService.getMpaRateById(film.getMpa().getId()));

            if (film.getGenres() != null) {
                Collection<Genre> sortGenres = film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toList());
                film.setGenres(new LinkedHashSet<>(sortGenres));
                for (Genre genre : film.getGenres()) {
                    genre.setName(genreService.getGenreById(genre.getId()).getName());
                }
            }
            genreService.reNewGenre(film);
            log.info("film has been updated");
            return film;
        } else {
            throw new NotFoundException("Film with id ==>" + film.getId() + "<== not founded");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";

        if (filmId == null) {
            throw new ValidationException("Illegal arguments");
        }
        Film film;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (filmRows.first()) {
            film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new HashSet<>(likeDbStorage.getLikes(filmRows.getLong("id"))),
                    new HashSet<>(genreService.getGenresByFilmId(filmId)),
                    mpaService.getMpaRateById(filmRows.getInt("rating_id")));
        } else {
            throw new NotFoundException("Film Not founded by id" + filmId);
        }
        return film;
    }

    @Override
    public void deleteFilmById(Long filmId) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sqlQuery, filmId);

        if (rowsAffected == 0) {
            throw new NotFoundException("Film with id " + filmId + " not found");
        } else {
            log.info("Film with id " + filmId + " has been deleted");
        }
    }

    @Override
    public boolean filmExists(long filmId) {
        String checkSql = "SELECT COUNT(*) " +
                "FROM films " +
                "WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId);
        return count != null && count > 0;
    }
}

