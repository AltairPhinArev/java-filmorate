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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;

import java.sql.*;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;
    LikeDbStorage likeDbStorage;
    DirectorDbStorage directorStorage;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService,
                         LikeDbStorage likeDbStorage, GenreService genreService,
                         DirectorDbStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.likeDbStorage = likeDbStorage;
        this.genreService = genreService;
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                        Film.builder()
                                .id(rs.getLong("id"))
                                .name(rs.getString("name"))
                                .description(rs.getString("description"))
                                .releaseDate(rs.getDate("release_Date").toLocalDate())
                                .duration(rs.getInt("duration"))
                                //.points(new HashSet<>(likeDbStorage.getLikes(rs.getLong("id"))))
                                .points(likeDbStorage.getLikes(rs.getLong("id")))
                                .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                                .mpa(new MPA(rs.getInt("rating_id"),
                                        mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                                .directors(directorStorage.getDirectorsByFilmId(rs.getInt("id")))
                                .build())
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
            film.setGenres(film.getGenres().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors
                    .toCollection(LinkedHashSet::new)));
            film.getGenres().forEach(genre -> genreService.addGenreToFilm(film));
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> directorStorage.addDirectorToFilm(film));
        }

        log.info("film has been created");
        return film;
    }

    @Override
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

            if (film.getDirectors() != null) {
                if (!film.getDirectors().isEmpty()) {
                    Set<Director> directors = new HashSet<>();
                    for (Director director : film.getDirectors()) {
                        Optional<Director> oDirector = directorStorage.getDirectorById(director.getId());
                        oDirector.ifPresent(directors::add);
                    }
                    film.setDirectors(directors);
                    directorStorage.addDirectorToFilm(film);
                }
            } else {
                directorStorage.removeDirectorByFilmId(film.getId());
            }

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
            film = Film.builder()
                    .id(filmRows.getLong("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_Date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    //.points(new HashSet<>(likeDbStorage.getLikes(filmId)))
                    .points(likeDbStorage.getLikes(filmId))
                    .genres(new HashSet<>(genreService.getGenresByFilmId(filmId)))
                    .mpa(new MPA(filmRows.getInt("rating_id"),
                            mpaService.getMpaRateById(filmRows.getInt("rating_id")).getName()))
                    .directors(directorStorage.getDirectorsByFilmId(filmId))
                    .build();
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

