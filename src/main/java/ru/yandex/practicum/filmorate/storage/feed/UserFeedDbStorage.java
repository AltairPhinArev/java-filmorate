package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserFeedDbStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    //Изменил enum теперь не нужно все переводить в String в методах
    //Все теперь проходит через один метод что бы избегать дублирования
    public void setOperation(Long userId, Event event, Operation operation , Long entityId) {
        String sqlQuery = "INSERT INTO feeds (feed_timestamp, user_id, event_type, Operation, entityId) " +
                "VALUES (?,?,?,?,?)";
        LocalDateTime dateTimeOfOperation = LocalDateTime.now();
        jdbcTemplate.update(sqlQuery, dateTimeOfOperation, userId, event.name(),
                operation.name(), entityId);

    }

    public List<Feed> getFeedByUserId(Long userId) {
        String sqlQuery = "SELECT * FROM feeds WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, new Object[]{userId}, (rs, rowNum) -> new Feed(
                        rs.getObject("feed_timestamp", LocalDateTime.class),
                        rs.getLong("user_id"),
                        Event.valueOf(rs.getString("event_type")),
                        Operation.valueOf(rs.getString("operation")),
                        rs.getLong("eventId"),
                        rs.getLong("entityId")
                )
        );
    }
}