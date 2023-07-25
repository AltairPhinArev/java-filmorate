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
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component("FilmDbStorage")
public class FilmDbStorage extends AbstractFilmDbStorage {
    LikeDbStorage likeDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                         DirectorDbStorage directorStorage, LikeDbStorage likeDbStorage) {
        super(jdbcTemplate, mpaService, genreService, directorStorage);
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> {
            Film film = Film.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_Date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                    .mpa(new MPA(rs.getInt("rating_id"),
                            mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                    .directors(directorStorage.getDirectorsByFilmId(rs.getInt("id")))
                    .build();
            film.setVoytedUsers(new HashSet<>(likeDbStorage.getLikes(rs.getLong("id"))));
            return film;
        }
        ));
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
                    .genres(new HashSet<>(genreService.getGenresByFilmId(filmId)))
                    .mpa(new MPA(filmRows.getInt("rating_id"),
                            mpaService.getMpaRateById(filmRows.getInt("rating_id")).getName()))
                    .directors(directorStorage.getDirectorsByFilmId(filmId))
                    .build();
            film.setVoytedUsers(new HashSet<>(likeDbStorage.getLikes(filmId)));
        } else {
            throw new NotFoundException("Film Not founded by id" + filmId);
        }
        return film;
    }
}

