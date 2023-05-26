package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private static final Logger log = LogManager.getLogger(User.class);

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
       return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    public void createFriend(Long userId, Long userFriendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        if ((user != null && userFriend != null && !Objects.equals(user.getId(), userFriend.getId()))) {
            user.getFriends().add(userFriend.getId());
            userFriend.getFriends().add(user.getId());
            log.info("FriendShip has been created");
        } else {
            throw new ValidationException("Cannot find User");
        }
    }

    public List<User> findAllFriend(Long user) {
        return userStorage.getUserById(user).getFriends().stream()
                .map(friendId -> userStorage.getUserById(friendId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<User> findCommonFriends(Long user, Long friendUser) {

        Set<Long> commonFriendsIds = userStorage.getUserById(user).getFriends().stream()
                .filter(friendId -> userStorage.getUserById(friendUser).getFriends().contains(friendId))
                .collect(Collectors.toSet());

        if (commonFriendsIds.size() == 0) {
            log.info("You don't have common friends");
        }

        return commonFriendsIds.stream()
                .map(commonId -> userStorage.getUserById(commonId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void deleteFromFriends(Long user, Long otherUser) {
        if (userStorage.getUserById(user).getFriends().size() == 0
                || userStorage.getUserById(otherUser).getFriends().size() == 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.toString());
        } else {
            userStorage.getUserById(user).getFriends().remove(otherUser);
            userStorage.getUserById(otherUser).getFriends().remove(user);
        }
    }
}
