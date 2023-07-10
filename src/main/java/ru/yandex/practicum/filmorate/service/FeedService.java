package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;
import ru.yandex.practicum.filmorate.storage.feed.UserFeedDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FeedService {

    UserStorage userStorage;
    UserFeedDbStorage userFeedDbStorage;

    @Autowired
    public FeedService(UserFeedDbStorage userFeedDbStorage, UserStorage userStorage) {
        this.userFeedDbStorage = userFeedDbStorage;
        this.userStorage = userStorage;
    }

    public void setOperation(Long userId, Event event, Operation operation, Long entityId) {
        userFeedDbStorage.setOperation(userId, event, operation, entityId);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("user with id ="+ userId + " Doesn't exist");
        }
        return userFeedDbStorage.getFeedByUserId(userId);
    }
}
