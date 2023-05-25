package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class UserController {

    UserStorage userStorage;
    UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userStorage.getUserById(id);
    }

    @GetMapping(value = "/users/{id}/friends")
    public ArrayList<User> findAllFriends(@PathVariable Long id) {
        ArrayList<User> friends = new ArrayList<>();
        for (Long friendId : userService.findAllFriend(userStorage.getUserById(id))) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public ArrayList<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
       ArrayList<User> commonFriends = new ArrayList<>();
       for (Long commonId :
        userService.findCommonFriends(userStorage.getUserById(id), userStorage.getUserById(otherId))) {
           commonFriends.add(userStorage.getUserById(commonId));
        }
       return commonFriends;
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.createFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFromFriends(userStorage.getUserById(id), userStorage.getUserById(friendId));
    }

    @DeleteMapping(value = "/users/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userStorage.deleteUserById(id);
    }

    @ExceptionHandler(UserOrFilmNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpStatus handleNegativeCount(final UserOrFilmNotFoundException e) {
        return HttpStatus.NOT_FOUND;
    }
}