package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendShip.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    UserStorage userStorage;
    FriendDbStorage friendDbStorage;

    public UserService(UserStorage userStorage, FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    private static final Logger log = LogManager.getLogger(User.class);

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        validate(user);
       return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    public void createFriend(Long userId, Long userFriendId) {
        friendDbStorage.createFriend(userId, userFriendId);
    }

    public List<User> findAllFriend(Long user) {
        return friendDbStorage.getFriends(user);
    }

    public List<User> findCommonFriends(Long firstUserId, Long secondUserId) {

        User user = getUserById(firstUserId);
        User commonUser = getUserById(secondUserId);

        Set<User> commonFriends = new HashSet<>();

        if ((user != null) && (commonUser != null)) {
            commonFriends = new HashSet<>(friendDbStorage.getFriends(firstUserId));
            commonFriends.retainAll(friendDbStorage.getFriends(secondUserId));
        }
        return new ArrayList<User>(commonFriends);
    }

    public void deleteFromFriends(Long user, Long otherUser) {
        friendDbStorage.deleteFromFriends(user, otherUser);
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
