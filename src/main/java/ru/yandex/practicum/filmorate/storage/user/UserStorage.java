package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User createUser(User film);

    User updateUser(User film);

    User getUserById(Long id);

    boolean isUserPresent(Long id);

    void deleteUserById(Long id);

    boolean userExists(Long userId);
}

