package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

    @RestController
    public class UserController {

        private int userId = 1;
        private static final Logger log = LogManager.getLogger(User.class);
        private final HashMap<Integer, User> userById = new HashMap<>();

        @GetMapping(value = "/users")
        public Collection<User> findAll() {
            log.info("Текущее количество юзеров {}", userById.size());
            return userById.values();
        }

        @PostMapping(value = "/users")
        public User createUser(@RequestBody User user) {
            validate(user);
            user.setId(userId++);
            userById.put(user.getId(), user);
            log.info("Пользователь успшно добавлен");
            return user;
        }

        @PutMapping(value = "/users")
        public User updateUser(@RequestBody User user) {
            if (!userById.containsKey(user.getId())) {
                throw new ValidationException();
            } else {
                validate(user);
                userById.put(user.getId(), user);
                log.info("Данные пользователя обновлены с id {}", user.getId());
                return user;
            }
        }

        private void validate(User user) {
            if (user.getEmail() != null && user.getBirthday().isBefore(LocalDate.now()) && user.getLogin() != null &&
                    !user.getLogin().contains(" ") && user.getEmail().contains("@")) {
                    if (user.getName() == null || user.getName().isBlank()) {
                        user.setName(user.getLogin());
                    }
            } else {
                    log.error("Не верно укзаны данные Пользователя");
                    throw new ValidationException();
                }
            }
        }