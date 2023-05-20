package ru.yandex.practicum.filmorate.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class UserService {

    private static final Logger log = LogManager.getLogger(User.class);

    public void createFriend(User user , User userFriend) {
        if ((user != null && userFriend != null && user.getId() != userFriend.getId())) {
            user.getFriends().add(userFriend.getId());
            userFriend.getFriends().add(user.getId());
            log.info("FreandShip has been created");
        } else {
            throw new ValidationException("Cannot find User");
        }
    }

    public ArrayList<Integer> findAllFriend(User user) {
        return user.getFriends();
    }

    public ArrayList<Integer> findCommonFriends(User user , User friendUser) {
        ArrayList<Integer> commonFriends = new ArrayList<>();

        for(Integer friendId : user.getFriends()) {
            for(Integer commonFriendId : friendUser.getFriends()) {
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
            throw  new ValidationException("Users not friends");
        } else {
            user.getFriends().remove(otherUser.getId());
            otherUser.getFriends().remove(user.getId());
        }
    }
}
