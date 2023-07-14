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
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.*;

@Component
public class LikeDbStorage {

    JdbcTemplate jdbcTemplate;
    MpaService mpaService;
    GenreService genreService;
    DirectorService directorService;

    private static final Logger log = LogManager.getLogger(Film.class);

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                         DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    public void addLike(Long filmId, Long userId, int points) {
        String sql = "INSERT INTO film_likes (film_id, user_id, points) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, points);
        log.info("you just liked film");
    }

    public List<Film> getRateFilmsByCount(int limit, Integer genreId, Integer year) {

        String getPopularQuery = "SELECT films.* " +
                "FROM films " +
                "LEFT JOIN film_likes ON films.id = film_likes.film_id " +
                "LEFT JOIN FILM_GENRES FG on FILMS.ID = FG.FILM_ID  ";
        List<Object> params = new ArrayList<>();
        if (genreId != null) {
            getPopularQuery += "WHERE GENRE_ID = ? ";
            params.add(genreId);
        }
        if (year != null) {
            if (params.isEmpty()) {
                getPopularQuery += "WHERE YEAR(FILMS.RELEASE_DATE) = ? ";
            } else {
                getPopularQuery += "AND YEAR(FILMS.RELEASE_DATE) = ? ";
            }
            params.add(year);
        }
        //getPopularQuery += "GROUP BY films.id ORDER BY COUNT(film_likes.user_id) DESC LIMIT ?";
        getPopularQuery += "GROUP BY films.id ORDER BY AVG(film_likes.points) DESC LIMIT ?";

        log.info("Top films by count {}, genreId {}, year {}", limit, genreId, year);
        params.add(limit);
        return buildFilmFromQuery(getPopularQuery, params.toArray());
    }


    public List<Film> findCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT f.* " +
                "FROM films f " +
                "JOIN film_likes fl1 ON fl1.film_id = f.id AND fl1.user_id = ? " +
                "JOIN film_likes fl2 ON fl2.film_id = f.id AND fl2.user_id = ? " +
                "JOIN (SELECT film_id, COUNT(user_id) AS rate FROM film_likes GROUP BY film_id) " +
                "AS fl ON fl.film_id = f.id " +
                "ORDER BY fl.rate DESC";
        return buildFilmFromQuery(sqlQuery, new Object[]{userId, friendId});
    }

    public Map<Long, Integer> getLikes(Long filmId) {
        String sql = "SELECT user_id, points FROM film_likes WHERE film_id = ?";
        Map<Long, Integer> likes = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            likes.put(rs.getLong("user_id"), rs.getInt("points"));
        }, filmId);
        return likes;
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

    private List<Film> buildFilmFromQuery(String sqlQuery, Object[] params) {
        return jdbcTemplate.query(
                sqlQuery,
                params,
                (rs, rowNum) -> {
                    Film film = Film.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .releaseDate(rs.getDate("release_Date").toLocalDate())
                            .duration(rs.getInt("duration"))
                            //.points(new HashSet<>(getLikes(rs.getLong("id"))))
                            .points(getLikes(rs.getLong("id")))
                            .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                            .mpa(new MPA(rs.getInt("rating_id"), mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                            .directors(new HashSet<>(directorService.getDirectorByFilmId(rs.getLong("id"))))
                            .build();
                    return film;
                });
    }
}
