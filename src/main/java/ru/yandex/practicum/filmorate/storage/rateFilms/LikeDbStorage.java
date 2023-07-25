package ru.yandex.practicum.filmorate.storage.rateFilms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component("LikeStorage")
public class LikeDbStorage extends AbstractLikeStorage {
    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService,
                         DirectorService directorService) {
        super(jdbcTemplate, mpaService, genreService, directorService);
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "MERGE INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
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
        getPopularQuery += "GROUP BY films.id ORDER BY COUNT(film_likes.user_id) DESC LIMIT ?";

        log.info("Top films by count {}, genreId {}, year {}", limit, genreId, year);
        params.add(limit);
        return buildFilmFromQuery(getPopularQuery, params.toArray());
    }

    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getLong("user_id"), filmId);
    }

    @Override
    protected List<Film> buildFilmFromQuery(String sqlQuery, Object[] params) {
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
                            .genres(new HashSet<>(genreService.getGenresByFilmId(rs.getLong("id"))))
                            .mpa(new MPA(rs.getInt("rating_id"), mpaService.getMpaRateById(rs.getInt("rating_id")).getName()))
                            .directors(new HashSet<>(directorService.getDirectorByFilmId(rs.getLong("id"))))
                            .build();
                    film.setVoytedUsers(new HashSet<>(getLikes(rs.getLong("id"))));
                    return film;
                });
    }
}
