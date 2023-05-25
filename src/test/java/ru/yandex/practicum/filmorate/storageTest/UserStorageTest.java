package ru.yandex.practicum.filmorate.storageTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Controllers.UserController;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class UserStorageTest {

    UserController userController;
    UserStorage userStorage;
    UserService userService;


    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userStorage, userService);
    }

    @Test
    public void shouldNotValidateUserWithEmptyLogin() {
        User user = new User("andrrsd@mail.ru", null, "",
                LocalDate.of(1999, 10, 10));
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    public void shouldCreateNotCreateUserWithWrongEmail() {
        User user = new User("andrrmail.ru", null, "",
                LocalDate.of(1999, 10, 10));
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    public void shouldCreateCreateUserWithEmptyName() {
        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));
        userController.createUser(user);

        Assertions.assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void shouldCreateNotCreateUserWithEmptyEmail() {
        User user = new User("", null, "",
                LocalDate.of(1999, 10, 10));
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    public void shouldCreateNotCreateUserWithEmptyEmailNull() {
        User user = new User(null, null, "",
                LocalDate.of(1999, 10, 10));
        Assertions.assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    public void shouldCreateCreateUser() {
        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));
        user.setId(1L);

        Assertions.assertEquals(user, userController.createUser(user));
    }
}
