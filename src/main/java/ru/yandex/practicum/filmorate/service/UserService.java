package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    UserStorage userStorage;

    public UserService(@Qualifier("UserDbStorage")UserStorage userStorage) {
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
        userStorage.createFriend(userId, userFriendId);
    }

    public List<User> findAllFriend(Long user) {
        return userStorage.getFriends(user);
    }
/*
    public List<User> findCommonFriends(Long user, Long friendUser) {

    }
 */
    public void deleteFromFriends(Long user, Long otherUser) {
        userStorage.deleteFromFriends(user, otherUser);
    }
}
