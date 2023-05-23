package ru.yandex.practicum.filmorate.storage.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long userId = 1L;
    private static final Logger log = LogManager.getLogger(User.class);
    private final HashMap<Long, User> userById = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("Current number of users {}", userById.size());
        return userById.values();
    }

    @Override
    public User createUser(@RequestBody User user) {
        validate(user);
        user.setId(userId++);
        userById.put(user.getId(), user);
        log.info("User has been created successful");
        return user;
    }

    @Override
    public User updateUser(@RequestBody User user) {
        if (!userById.containsKey(user.getId())) {
            throw new ValidationException("Can't find User");
        } else {
            validate(user);
            userById.put(user.getId(), user);
            log.info("User has been updated with id {}", user.getId());
            return user;
        }
    }

    @Override
    public void deleteUserById(Long id) {
        if (userById.containsKey(id)) {
            userById.remove(id);
        } else {
            throw new ValidationException("Can't find User");
        }
    }

    @Override
    public User getUserById(Long id) {
        if (userById.containsKey(id)) {
            return userById.get(id);
        } else {
            throw new UserOrFilmNotFoundException("Can't find User");
        }
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