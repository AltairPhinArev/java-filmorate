package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.web.service.annotation.PutExchange;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public void createUser(@RequestBody User user) {
        validate(user);
        user.setId(userId++);
        users.add(user);
    }

    private User validate(User user) {
        if (user.getEmail() != null && !user.getName().isBlank() && user.getBirthday().isBefore(LocalDate.now()) && user.getLogin() != null &&
                !user.getLogin().contains(" ") && user.getEmail().contains("@")) {
            return user;
        } else {
         throw new ValidationException();
        }
    }

    @PutExchange(value = "/post/updateUser")
    public void updateUser(@RequestBody User user) {
        User existingUser = users.get(user.getId());
        validate(existingUser);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());

        users.set(user.getId(), existingUser);
    }
}