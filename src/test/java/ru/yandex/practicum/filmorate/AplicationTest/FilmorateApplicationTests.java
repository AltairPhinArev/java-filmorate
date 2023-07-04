package ru.yandex.practicum.filmorate.AplicationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.model.*;

import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewService reviewService;

    @Test
    void testCreateUser() {
        User user = User.builder()
                .id(0L)
                .name("ANtonY")
                .login("technojew")
                .birthday(LocalDate.of(1975, 11, 19))
                .email("fositik@yandex.ru").build();

        Assertions.assertEquals(user, userStorage.createUser(user));
    }

    @Test
    public void testCreateFilm() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Fighting club")
                .description("2 = 1")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .voytedUsers(new HashSet<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();

        Assertions.assertEquals(film1, filmService.createFilm(film1));
    }

    @Test
    public void testGetFilmById() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Alisa")
                .description("eat me")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .voytedUsers(new HashSet<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();
        filmService.createFilm(film1);
        Assertions.assertEquals(film1, filmService.getFilmById(film1.getId()));
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Charly and Chocolate Fabric")
                .description("Johny Depp")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .voytedUsers(new HashSet<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("SuperFast")
                .description("---")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();

        filmService.createFilm(film1);
        filmService.createFilm(film2);

        Assertions.assertEquals(2, filmService.findAll().size());
    }

    @Test
    public void testRemoveFilmById() {
        Film film1 = Film.builder()
                .id(1L)
                .name("superFast 100")
                .description("The last of last of last")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .voytedUsers(new HashSet<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("FightigngClub")
                .description("")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();

        filmService.createFilm(film1);
        filmService.createFilm(film2);

        filmService.deleteFilmById(film1.getId());
        assertThat(filmService.findAll().isEmpty());

        Assertions.assertEquals(1, filmService.findAll().size());
    }

    @Test
    public void testFindUserById() {
        User user1 = User.builder()
                .id(4L)
                .name("Dmitriy")
                .login("FRIGH")
                .email("Dmitriy@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        userService.createUser(user1);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(user1.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", user1.getId())
                );
    }

    @Test
    public void testAddLike() {
        Film film1 = Film.builder()
                .id(1L)
                .name("Rocky")
                .description("BOX")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .voytedUsers(new HashSet<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();
        User user1 = User.builder()
                .id(1L)
                .name("Dmitriy")
                .login("FRIGH")
                .email("Dmitriy@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();

        userStorage.createUser(user1);
        filmStorage.createFilm(film1);

        filmService.addLike(film1.getId(), user1.getId());
        film1 = filmStorage.getFilmById(film1.getId());

        assertThat(film1.getVoytedUsers()).contains(user1.getId());
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = User.builder()
                .id(1L)
                .name("login")
                .login("login")
                .email("login@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("login")
                .login("login")
                .email("login@mail.ru")
                .birthday(LocalDate.of(1980, 12, 24))
                .build();

        User user3 = User.builder()
                .id(3L)
                .name("login")
                .login("login")
                .email("anton@mail.ru")
                .birthday(LocalDate.of(1980, 12, 25))
                .build();

        user1 = userStorage.createUser(user1);
        user2 = userStorage.createUser(user2);
        user3 = userStorage.createUser(user3);

        userService.createFriend(user1.getId(), user2.getId());
        userService.createFriend(user1.getId(), user3.getId());
        userService.createFriend(user2.getId(), user1.getId());
        userService.createFriend(user2.getId(), user3.getId());
        assertThat(userService.findCommonFriends(user1.getId(), user2.getId())).hasSize(1);
        assertThat(userService.findCommonFriends(user1.getId(), user2.getId()))
                .contains(user3);
    }


}
