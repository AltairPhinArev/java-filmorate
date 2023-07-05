package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> findAllFriends(@PathVariable Integer id) {
        return userService.findAllFriend(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.createFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        userService.deleteUserById(id);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNegativeCount(final NotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}