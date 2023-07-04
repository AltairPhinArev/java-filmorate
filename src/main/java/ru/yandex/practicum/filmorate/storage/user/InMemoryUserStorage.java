package ru.yandex.practicum.filmorate.storage.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
        user.setId(userId++);
        userById.put(user.getId(), user);
        log.info(user.getName() + ", has been created successful with id {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(@RequestBody User user) {
        if (!userById.containsKey(user.getId())) {
            throw new ValidationException("Can't find User");
        } else {
            userById.put(user.getId(), user);
            log.info(user.getName() + " has been updated with id {}", user.getId());
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
            throw new NotFoundException("Can't find User");
        }
    }

    @Override
    public boolean isUserPresent(Long id) {
        return userById.containsKey(id);
    }
}