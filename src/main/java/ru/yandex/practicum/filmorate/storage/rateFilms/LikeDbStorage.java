package ru.yandex.practicum.filmorate.storage.rateFilms;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaService mpaService;

    private final GenreService genreService;

    private final DirectorDbStorage directorStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate,
                         MpaService mpaService,
                         GenreService genreService,
                         DirectorDbStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorStorage = directorStorage;
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("you just liked film");
    }

    public List<Film> getRateFilmsByCount(int count) {
        String getPopularQuery = "SELECT id, name, description, release_date, duration, rating_id " +
                "FROM films LEFT JOIN film_likes ON films.id = film_likes.film_id " +
                "GROUP BY films.id ORDER BY COUNT(film_likes.user_id) DESC LIMIT ?";

        log.info("Top films by count{}", count);
        return jdbcTemplate.query(getPopularQuery, (rs, rowNum) -> new Film(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        new HashSet<>(getLikes(rs.getLong("id"))),
                        new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))),
                        new MPA(rs.getInt("rating_id"),
                        mpaService.getMpaRateById(rs.getInt("rating_id")).getName()),
                        directorStorage.getDirectorsByFilmId(rs.getInt("id"))),
                count);

    }

    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getLong("user_id"), filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String checkQuery = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";

        try {
          int count = jdbcTemplate.queryForObject(checkQuery, Integer.class, filmId, userId);
          if (count > 0) {
              String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
              jdbcTemplate.update(sql, filmId, userId);
          } else {
              throw new NotFoundException(HttpStatus.NOT_FOUND.toString());
          }
        } catch (NullPointerException e) {
          e.getMessage();
          e.fillInStackTrace();
        }
    }
}
