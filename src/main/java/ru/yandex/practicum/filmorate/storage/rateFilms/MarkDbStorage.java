package ru.yandex.practicum.filmorate.storage.rateFilms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.*;

@Component("MarkStorage")
public class MarkDbStorage extends AbstractLikeStorage {
    @Autowired
    public MarkDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                         DirectorService directorService) {
        super(jdbcTemplate, mpaService, genreService, directorService);
    }

    public void addMark(Long filmId, Long userId, int mark) {
        String sql = "INSERT INTO film_likes (film_id, user_id, points) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, mark);
        log.info("you just liked film");
    }

    public List<Film> getRateFilmsByAVG(int limit, Integer genreId, Integer year) {

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
        getPopularQuery += "GROUP BY films.id ORDER BY AVG(film_likes.points) DESC LIMIT ?";

        log.info("Top films by count {}, genreId {}, year {}", limit, genreId, year);
        params.add(limit);
        return buildFilmFromQuery(getPopularQuery, params.toArray());
    }

    public Map<Long, Integer> getPoints(Long filmId) {
        String sql = "SELECT user_id, points FROM film_likes WHERE film_id = ?";
        Map<Long, Integer> points = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            points.put(rs.getLong("user_id"), rs.getInt("points"));
        }, filmId);
        return points;
    }

    @Override
    protected List<Film> buildFilmFromQuery(String sqlQuery, Object[] params) {
        return jdbcTemplate.query(
                sqlQuery,
                params,
                (rs, rowNum) -> Film.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_Date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .points(new HashMap<>(getPoints(rs.getLong("id"))))
                        .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                        .mpa(new MPA(rs.getInt("rating_id"), mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                        .directors(new HashSet<>(directorService.getDirectorByFilmId(rs.getLong("id"))))
                        .build());
    }
}

