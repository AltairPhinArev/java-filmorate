package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.Event;
import ru.yandex.practicum.filmorate.storage.feed.UserFeedDbStorage;

import java.util.List;

@Service
public class HistoryService {

    UserFeedDbStorage userFeedDbStorage;

    @Autowired
    public HistoryService(UserFeedDbStorage userFeedDbStorage) {
        this.userFeedDbStorage = userFeedDbStorage;
    }

    public void updateOperation(Long userId, Event event, Long entityId) {
        userFeedDbStorage.updateOperation(userId, event, entityId);
    }

    public void addOperation(Long userId, Event event, Long entityId) {
        userFeedDbStorage.addOperation(userId, event, entityId);
    }

    public void removeOperation(Long userId, Event event, Long entityId) {
        userFeedDbStorage.removeOperation(userId, event, entityId);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        return userFeedDbStorage.getFeedByUserId(userId);
    }
}
