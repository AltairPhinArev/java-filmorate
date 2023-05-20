package ru.yandex.practicum.filmorate.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


@RestController
public class UserController {

    UserStorage userStorage;
    UserService userService;

    @Autowired
    public UserController(UserStorage userStorage , UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    @GetMapping(value = "/users/{id}")
    public User getUserById(@PathVariable int id) {
        return userStorage.getUserById(id);
    }

    @GetMapping(value = "/users/{id}/friends")
    public ArrayList<User> findAllFriends(@PathVariable int id) {
        ArrayList <User>friends = new ArrayList<>();
        for (Integer friendId : userService.findAllFriend(userStorage.getUserById(id))) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public ArrayList<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        ArrayList<User> comonFriends = new ArrayList<>();
       for(Integer commonId :
        userService.findCommonFriends(userStorage.getUserById(id) , userStorage.getUserById(otherId))) {
           comonFriends.add(userStorage.getUserById(commonId));
        }
       return comonFriends;
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
    public void addFriend(@PathVariable int id , @PathVariable int friendId) {
        userService.createFriend(userStorage.getUserById(id) , userStorage.getUserById(friendId));
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFromFriends(userStorage.getUserById(id) , userStorage.getUserById(friendId));
    }

    @DeleteMapping(value = "/users/{id}")
    public void deleteUserById(@PathVariable int id) {
        userStorage.deleteUserById(id);
    }
}