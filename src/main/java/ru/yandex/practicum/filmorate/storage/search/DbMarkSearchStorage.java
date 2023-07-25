package ru.yandex.practicum.filmorate.storage.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("DbMarkSearchStorage")
public class DbMarkSearchStorage implements SearchStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Comparator<Object> filmComparator;

    @Autowired
    public DbMarkSearchStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmComparator = Comparator
                .comparingDouble(f -> ((Film) f).calcRating()).reversed();
    }

    @Override
    public List<Film> searchFilmByName(String partialName) {
        String sqlQuery = "select f.*, d.*, g.id, g.name, m.name, l.user_id, l.points from films as f " +
                "left join film_likes as l on f.id = l.film_id " +
                "left join ratings_mpa as m on f.rating_id = m.id " +
                "left join film_genres as fg on fg.film_id = f.id " +
                "left join genres as g on fg.genre_id = g.id " +
                "left join film_directors as fd on fd.film_id = f.id " +
                "left join directors as d on d.id = fd.director_id " +
                "where lower(f.name) like concat('%',lower(?),'%') ";
        Map<Long, Film> map = new HashMap<>();
        jdbcTemplate.query(sqlQuery, (rs) -> {
            Film.storeFullRowWithMarks(rs, map);
        }, partialName);
        return map.values().stream()
                .sorted(filmComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmByDirector(String partialName) {
        String sqlQuery = "select f.*, d.*, g.id, g.name, m.name, l.user_id, l.points from films as f " +
                "left join film_likes as l on f.id = l.film_id " +
                "left join ratings_mpa as m on f.rating_id = m.id " +
                "left join film_genres as fg on fg.film_id = f.id " +
                "left join genres as g on fg.genre_id = g.id " +
                "left join film_directors as fd on fd.film_id = f.id " +
                "left join directors as d on d.id = fd.director_id " +
                "where lower(d.name) like concat('%',lower(?),'%')";
        Map<Long, Film> map = new HashMap<>();
        jdbcTemplate.query(sqlQuery, (rs) -> {
            Film.storeFullRowWithMarks(rs, map);
        }, partialName);
        return map.values().stream()
                .sorted(filmComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> searchFilmByNameAndDirector(String partialName) {
        String sqlQuery = "select f.*, d.*, g.id, g.name, m.name, l.user_id, l.points from films as f " +
                "left join film_likes as l on f.id = l.film_id " +
                "left join ratings_mpa as m on f.rating_id = m.id " +
                "left join film_genres as fg on fg.film_id = f.id " +
                "left join genres as g on fg.genre_id = g.id " +
                "left join film_directors as fd on fd.film_id = f.id " +
                "left join directors as d on d.id = fd.director_id " +
                "where lower(f.name) like concat('%',lower(?),'%') or lower(d.name) like concat('%',lower(?),'%')";
        Map<Long, Film> map = new HashMap<>();
        jdbcTemplate.query(sqlQuery, (rs) -> {
            Film.storeFullRowWithMarks(rs, map);
        }, partialName, partialName);
        return map.values().stream()
                .sorted(filmComparator)
                .collect(Collectors.toList());
    }
}
