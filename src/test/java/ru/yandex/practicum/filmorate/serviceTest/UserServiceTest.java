package ru.yandex.practicum.filmorate.serviceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class UserServiceTest {

    UserStorage userStorage;
    UserService userService;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    public void shouldAddFriendToFilm() {
        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        User user1 = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        userStorage.createUser(user);
        userStorage.createUser(user1);

        userService.createFriend(user.getId(), user1.getId());

        Long userId = 0L;
        for (Long id : user.getFriends()) {
            userId = id;
        }
        Assertions.assertEquals(userId, user1.getId());

        userId = 0L;
        for (Long id : user1.getFriends()) {
            userId = id;
        }
        Assertions.assertEquals(userId, user.getId());
    }

    @Test
    public void shouldFindCommonFriendToFilm() {
        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        User user1 = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        User user2 = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        userStorage.createUser(user);
        userStorage.createUser(user1);
        userStorage.createUser(user2);

        userService.createFriend(user.getId(), user1.getId());
        userService.createFriend(user.getId(), user2.getId());
        userService.createFriend(user1.getId(), user2.getId());


        Long commonUserId = 0L;
        for (Long id : userService.findCommonFriends(user, user1)) {
            commonUserId = id;
        }
        Assertions.assertEquals(commonUserId, user2.getId());
    }

    @Test
    public void shouldNotAddFriendToFilm() {
        User user = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        User user1 = new User("andrr@mail.ru", "null", "",
                LocalDate.of(1999, 10, 10));

        userStorage.createUser(user);
        userStorage.createUser(user1);
        user1.setId(user.getId());
        Assertions.assertThrows(ValidationException.class,() -> {
            userService.createFriend(user.getId(), user1.getId());
        });
    }
}
