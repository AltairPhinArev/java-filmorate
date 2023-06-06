package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashMap;

@Qualifier
public class FilmDbStorage implements FilmStorage {

    JdbcTemplate jdbcTemplate;
    FilmStorage filmStorage;
    FilmService filmService;



    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage, FilmService filmService) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> new Film (
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration")
        )));
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("id");
        film.setId(filmService.createFilm(film).getId());
        film.setRating_MPA(film.getRating_MPA());
        if (film.getGenre() != null) {
            film.setGenre(film.getGenre());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Film getFilmById(Long id) {
        return null;
    }

    @Override
    public HashMap<Long, Film> getFilmsMap() {
        return null;
    }

    @Override
    public void deleteFilmById(Long id) {
        String sql = "DELETE" + id.toString() + "FROM films";
    }

}

