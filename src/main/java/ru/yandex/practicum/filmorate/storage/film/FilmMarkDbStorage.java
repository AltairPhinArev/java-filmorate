package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.MarkDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component("FilmMarkDbStorage")
public class FilmMarkDbStorage extends AbstractFilmDbStorage {
    MarkDbStorage markDbStorage;

    @Autowired
    public FilmMarkDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                             DirectorDbStorage directorStorage, MarkDbStorage markDbStorage) {
        super(jdbcTemplate, mpaService, genreService, directorStorage);
        this.markDbStorage = markDbStorage;
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
                        .points(markDbStorage.getPoints(rs.getLong("id")))
                        .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                        .mpa(new MPA(rs.getInt("rating_id"),
                                mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                        .directors(directorStorage.getDirectorsByFilmId(rs.getInt("id")))
                        .build())
        );
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
                    .points(markDbStorage.getPoints(filmId))
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
}

