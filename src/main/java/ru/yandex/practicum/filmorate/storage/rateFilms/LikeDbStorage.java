package ru.yandex.practicum.filmorate.storage.rateFilms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.HashSet;
import java.util.List;

@Component
public class LikeDbStorage {

    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
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

        return jdbcTemplate.query(getPopularQuery, (rs, rowNum) ->
                        Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_Date").toLocalDate())
                .duration(rs.getInt("duration"))
                .voytedUsers(new HashSet<>(getLikes(rs.getLong("id"))))
                .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                .mpa(new MPA(rs.getInt("rating_id"),
                        mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                .directors(new HashSet<>())
                .build(),
                count);
    }

    public List<Film> findCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM films f " +
                "JOIN film_likes fl1 ON fl1.film_id = f.id AND fl1.user_id = ? " +
                "JOIN film_likes fl2 ON fl2.film_id = f.id AND fl2.user_id = ? " +
                "JOIN (SELECT film_id, COUNT(user_id) AS rate FROM film_likes GROUP BY film_id) " +
                "AS fl ON fl.film_id = f.id " +
                "ORDER BY fl.rate DESC";

        return jdbcTemplate.query(
                sqlQuery,
                new Object[]{userId, friendId},
                (rs, rowNum) -> {
                    Film film = Film.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .releaseDate(rs.getDate("release_Date").toLocalDate())
                            .duration(rs.getInt("duration"))
                            .voytedUsers(new HashSet<>(getLikes(rs.getLong("id"))))
                            .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                            .mpa(new MPA(rs.getInt("rating_id"),
                                    mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                            .directors(new HashSet<>())
                            .build();
                    return film;
                });
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
