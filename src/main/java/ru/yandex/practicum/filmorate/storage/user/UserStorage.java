package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Collection<User> findAll();
    User createUser(User film);
    User updateUser(User film);
    User getUserById(Long id);
    void deleteUserById(Long id);
}

