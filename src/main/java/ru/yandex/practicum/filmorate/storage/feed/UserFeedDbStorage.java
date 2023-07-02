package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserFeedDbStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateOperation(Long userId, Event event, Long entityId) {
            String sqlQuery = "INSERT INTO feeds (feed_timestamp, user_id, event_type, Operation, entityId) " +
                    "VALUES (?,?,?,?,?)";
            LocalDateTime dateTimeOfOperation = LocalDateTime.now();
            jdbcTemplate.update(sqlQuery, dateTimeOfOperation, userId, event.toString(),
                    Operation.UPDATE.toString(), entityId);
    }

    public void addOperation(Long userId, Event event, Long entityId) {
            String sqlQuery = "INSERT INTO feeds (feed_timestamp, user_id, event_type, Operation, entityId) " +
                    "VALUES (?,?,?,?,?)";
            LocalDateTime dateTimeOfOperation = LocalDateTime.now();
            jdbcTemplate.update(sqlQuery, dateTimeOfOperation, userId, event.toString(),
                    Operation.ADD.toString(), entityId);
    }

    public void removeOperation(Long userId, Event event, Long entityId) {
            String sqlQuery = "INSERT INTO feeds (feed_timestamp, user_id, event_type, Operation, entityId) " +
                    "VALUES (?,?,?,?,?)";
            LocalDateTime dateTimeOfOperation = LocalDateTime.now();
            jdbcTemplate.update(sqlQuery, dateTimeOfOperation, userId, event.toString(),
                    Operation.REMOVE.toString(), entityId);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        String sqlQuery = "SELECT * FROM feeds WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, new Object[]{userId}, (rs, rowNum) -> new Feed(
                        rs.getObject("feed_timestamp", LocalDateTime.class),
                        rs.getLong("user_id"),
                        rs.getString("event_type"),
                        rs.getString("operation"),
                        rs.getLong("eventId"),
                        rs.getLong("entityId")
                )
        );
    }
}
/*
CREATE TABLE IF NOT EXISTS feeds
(
    feed_timestamp TIMESTAMP,
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    event_type varchar(255),
    operation varchar(255),
    eventId bigint generated always as identity primary key,
    entityId bigint
);
 */