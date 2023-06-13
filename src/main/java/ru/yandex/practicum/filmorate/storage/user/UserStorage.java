package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> findAll();

    User createUser(User film);

    User updateUser(User film);

    User getUserById(Long id);

    void createFriend(Long userId, Long userFriendId);

    List<User> getFriends(Long userId);

    void deleteFromFriends(Long userId, Long userFriendId);

    void deleteUserById(Long id);
}

