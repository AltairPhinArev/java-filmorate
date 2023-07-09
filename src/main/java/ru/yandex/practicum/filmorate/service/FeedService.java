package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.Event;
import ru.yandex.practicum.filmorate.storage.feed.Operation;
import ru.yandex.practicum.filmorate.storage.feed.UserFeedDbStorage;

import java.util.List;

@Service
public class FeedService {

    UserFeedDbStorage userFeedDbStorage;

    @Autowired
    public FeedService(UserFeedDbStorage userFeedDbStorage) {
        this.userFeedDbStorage = userFeedDbStorage;
    }

    public void setOperation(Long userId, Event event, Operation operation, Long entityId) {
        userFeedDbStorage.setOperation(userId, event, operation, entityId);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        return userFeedDbStorage.getFeedByUserId(userId);
    }
}
