package ru.yandex.practicum.filmorate.storage.friendShip;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

@Component
public class FriendDbStorage {

    JdbcTemplate jdbcTemplate;
    UserDbStorage userService;

    public FriendDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }

    public void createFriend(Integer userId, Integer friendId) {
        boolean friendStatus = false;

        User user  = userService.getUserById(userId);
        User friendUser = userService.getUserById(friendId);

        if (friendUser.getFriends().contains(user)) {
            friendStatus = true;

            String sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ? " +
                    "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, friendId, userId, true, friendId, userId);
        }
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        user.getFriends().add(friendUser);
        jdbcTemplate.update(sql, userId, friendId, friendStatus);
    }

    public void deleteFromFriends(Integer userId, Integer userFriendId) {
        if ((userService.getUserById(userFriendId) != null) && (userService.getUserById(userFriendId) != null)) {
            String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, userFriendId);

            if (userService.getUserById(userFriendId).getFriends().contains(userId)) {
                sql = "UPDATE friends SET user_id = ? AND friend_id = ? AND status = ?" +
                        "WHERE user_id = ? AND friend_id = ?";

                jdbcTemplate.update(sql, userFriendId, userId, false, userFriendId, userId);
            }
        }
    }    

    public List<User> getFriends(Integer userId) {
        String sql = "SELECT users.id, users.name, users.email, users.login, users.birthday ,FROM friends " +
                " INNER JOIN users ON friends.friend_id = users.id WHERE friends.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new User(rs.getInt("id"),rs.getString("email"),
                    rs.getString("login"),rs.getString("name"),
                    rs.getDate("birthday").toLocalDate());
        }, userId);
        } else {
            throw new NotFoundException("User with id" + userId + "doesn't exist");
        }
    }
}
