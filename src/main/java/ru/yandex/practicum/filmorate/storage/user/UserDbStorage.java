package ru.yandex.practicum.filmorate.storage.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Component
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
        log.info("User has been created with ID={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {

        if (getUserById(user.getId()) != null) {

            String sqlQuery = "UPDATE users SET " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "WHERE id = ?";

            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("User updated {}", user.getId());
            return user;
        } else {
            throw new NotFoundException("User with ID=" + user.getId() + "NOT FOUND");
        }
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, (resultSet, rowNum) -> {
                User user = new User(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate()
                );
                return user;
            });
        } catch (EmptyResultDataAccessException e) {
            log.error("NOT FOUNDED USER");
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public void deleteUserById(Long id) {
        String sqlQuery = "DELETE FROM users";
        if (getUserById(id) != null) {
         jdbcTemplate.update(sqlQuery, id);
         log.info("User has been deleted with ID={}", id);
        } else {
            throw new NotFoundException("NOT FOUND");
        }
    }

    public boolean userExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM users " +
                "WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}
