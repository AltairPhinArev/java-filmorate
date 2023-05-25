package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {

    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private static final Logger log = LogManager.getLogger(User.class);

    public void createFriend(Long userId , Long userFriendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(userFriendId);
        if ((user != null && userFriend != null && user.getId() != userFriend.getId())) {
            user.getFriends().add(userFriend.getId());
            userFriend.getFriends().add(user.getId());
            log.info("FriendShip has been created");
        } else {
            throw new ValidationException("Cannot find User");
        }
    }

    public Set<Long> findAllFriend(User user) {
        return user.getFriends();
    }

    public Set<Long> findCommonFriends(User user , User friendUser) {
        Set<Long> commonFriends = new HashSet<>();

        for(Long friendId : user.getFriends()) {
            for(Long commonFriendId : friendUser.getFriends()) {
                if (Objects.equals(friendId, commonFriendId)) {
                    commonFriends.add(commonFriendId);
                } else {
                    log.info("You don't have common friends");
                }
            }
        }
        return commonFriends;
    }

    public void deleteFromFriends(User user , User otherUser) {
        if (user.getFriends().size() == 0 || otherUser.getFriends().size() == 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST.toString());
        } else {
            user.getFriends().remove(otherUser.getId());
            otherUser.getFriends().remove(user.getId());
        }
    }
}
