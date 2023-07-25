package ru.yandex.practicum.filmorate.storage.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("DbMarkRecommendations")
public class DbMarkRecommendationStorage implements RecommendationStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public DbMarkRecommendationStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getNearestUserId(long toId) {
        String sqlQuery = "select uf.user_id as ufu from film_likes as uf " +
                "left join film_likes as fu on fu.film_id = uf.film_id " +
                "where fu.user_id = ? and uf.user_id <> fu.user_id " +
                "and uf.points = fu.points and uf.points > 5 " +
                "group by uf.user_id " +
                "order by count(uf.film_id) desc, uf.user_id asc " +
                "limit 1";
        List<Long> list = jdbcTemplate.getJdbcTemplate().query(sqlQuery,
                (rs, n) -> rs.getLong("ufu"), toId);
        if (list.size() > 0) { //есть еще хотя бы один пользователь
            return list.get(0); //возвращаем ближайшего по предпочтениям
        } else { //других нет
            return 0; //некому что-то рекомендовать
        }
    }

    @Override
    public List<Film> recommendFilms(long fromId, long toId) {
        String sqlQuery = "select f.*, d.*, g.id, g.name, m.name, l.user_id, l.points from films as f " +
                "left join film_likes as l on f.id = l.film_id " +
                "left join ratings_mpa as m on f.rating_id = m.id " +
                "left join film_genres as fg on fg.film_id = f.id " +
                "left join genres as g on fg.genre_id = g.id " +
                "left join film_directors as fd on fd.film_id = f.id " +
                "left join directors as d on d.id = fd.director_id " +
                "where f.id in " +
                "(select film_id from film_likes as fl where fl.user_id = ? and fl.points > 5) " +
                "and f.id not in (select film_id from film_likes where film_likes.user_id = ?)";
        Map<Long, Film> map = new HashMap<>();
        jdbcTemplate.getJdbcTemplate().query(sqlQuery, (rs) -> {
            Film.storeFullRowWithMarks(rs, map);
        }, fromId, toId);
        return map.values().stream()
                .sorted(Comparator.comparingLong(Film::getId))
                .collect(Collectors.toList());
    }
}
