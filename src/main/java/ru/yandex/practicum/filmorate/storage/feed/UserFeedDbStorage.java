package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class UserFeedDbStorage {

    JdbcTemplate jdbcTemplate;
    UserStorage userStorage;

    @Autowired
    public UserFeedDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void setOperation(Long userId, Event event, Operation operation, Long entityId) {
        String sqlQuery = "INSERT INTO feeds (feed_timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?,?,?,?,?)";

        Long timestamp = Instant.now().toEpochMilli();
        jdbcTemplate.update(sqlQuery, timestamp, userId, event.name(), operation.name(), entityId);
        log.info("Действе юзера с id{} были сохранены с данными " + event + " " + operation + entityId, userId);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        String sqlQuery = "SELECT * FROM feeds WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, new Object[]{userId}, (rs, rowNum) -> new Feed(
                        rs.getLong("feed_timestamp"),
                        rs.getLong("user_id"),
                        Event.valueOf(rs.getString("event_type")),
                        Operation.valueOf(rs.getString("operation")),
                        rs.getLong("event_id"),
                        rs.getLong("entity_id")
                )
        );
    }
}