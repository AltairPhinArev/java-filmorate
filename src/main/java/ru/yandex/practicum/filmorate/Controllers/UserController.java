package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
public class UserController {

    private int userId = 1;
    private static final Logger log = LogManager.getLogger(User.class);
    private final List<User> users = new ArrayList<>();

    @GetMapping(value = "/users")
    public List<User> findAll() {
        log.debug("Текущее количество юзеров {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        validate(user);
        user.setId(userId++);
        users.add(user);
        return user;
    }

    private User validate(User user) {
        if (user.getEmail() != null && user.getBirthday().isBefore(LocalDate.now()) && user.getLogin() != null &&
                !user.getLogin().contains(" ") && user.getEmail().contains("@")) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            return user;
        } else {
         throw new ValidationException("Данные не верно указаны");
        }
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        if (userId - 1 < user.getId()) {
            throw new ValidationException("Такого пользователя пока нет(");
        } else {
            validate(user);
            users.set(user.getId() - 1, user);
            return user;
        }
    }
}