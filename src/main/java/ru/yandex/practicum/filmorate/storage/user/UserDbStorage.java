package ru.yandex.practicum.filmorate.storage.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(User.class);

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, ((rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        )));
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday)" +
                "VALUES (?, ?, ?, ?)";

        validate(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;

        }, keyHolder);

        Number userId = keyHolder.getKey();
        if (userId != null) {
            user.setId(userId.longValue());
        }

        return user;
    }

    @Override
    public User updateUser(User user) {
        validate(user);

        if (user.getId() != null) {
            String sqlQuery = "UPDATE users SET " +
                    "email = ?, login = ?, name = ?, birthday = ?";

            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            });

        } else {
            throw new UserOrFilmNotFoundException("Film by name" + user.getName() + "doesn't exist");
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (resultSet, rowNum) -> {

                User user = new User(null, null,null,null,null);

                user.setId(resultSet.getLong("id"));
                user.setEmail(resultSet.getString("email"));
                user.setLogin(resultSet.getString("login"));
                user.setName(resultSet.getString("name"));
                user.setBirthday(resultSet.getDate("birthday").toLocalDate());
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            throw new UserOrFilmNotFoundException(e.getMessage());
        }
    }

    @Override
    public void deleteUserById(Long id) {
        String sqlQuery = "DELETE FROM users";
        if (getUserById(id) != null) {
         jdbcTemplate.update(sqlQuery, id);
        } else {
            throw new UserOrFilmNotFoundException("NOT FOUND");
        }
    }

    public void createFriend(Long userId, Long userFriendId) {
        boolean friendStatus = false;

        if ((getUserById(userId) != null) && (getUserById(userFriendId) != null)) {
            if (getUserById(userFriendId).getFriends().contains(userId)) {
                friendStatus = true;
                String sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                        "WHERE user_id = ? AND friend_id = ?";
                jdbcTemplate.update(sql, userFriendId, userId, true, userFriendId, userId);
            }

            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, userId, userFriendId, friendStatus);
        }
    }

    public void deleteFromFriends(Long userId, Long userFriendId) {
        if ((getUserById(userFriendId) != null) && (getUserById(userFriendId) != null)) {
            String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, userFriendId);
            if (getUserById(userFriendId).getFriends().contains(userId)) {
                sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ?" +
                        "WHERE user_id = ? AND friend_id = ?";

                jdbcTemplate.update(sql, userFriendId, userId, false, userFriendId, userId);
            }
        }
    }

    public List<User> getFriends(Long userId) {
        if (getUserById(userId) != null) {
            String sql = "SELECT friend_id, email, login, name, birthday FROM friends" +
                    " INNER JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate())
            );
        } else {
            return null;
        }
    }

    private User validate(User user) {
        if (user.getEmail() != null && user.getBirthday().isBefore(LocalDate.now()) && user.getLogin() != null &&
                !user.getLogin().contains(" ") && user.getEmail().contains("@")) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            return user;
        } else {
            log.error("Illegal arguments for user");
            throw new ValidationException("Illegal arguments for user");
        }
    }
}
