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

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.model.feedTypes.*;
import ru.yandex.practicum.filmorate.storage.film.FilmMarkDbStorage;
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
    private final FilmMarkDbStorage filmStorage;
    private final FilmMarkService filmService;
    private final UserService userService;
    private final DirectorService directorService;
    private final ReviewService reviewService;
    private final FeedService feedService;


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
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
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
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
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
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("SuperFast")
                .description("---")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
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
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
                .build();

        Film film2 = Film.builder()
                .id(2L)
                .name("FightigngClub")
                .description("")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
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
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .directors(Set.of())
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

        filmService.addMark(film1.getId(), new LikeInputDto(user1.getId(), 6));
        film1 = filmStorage.getFilmById(film1.getId());

        assertThat(film1.getPoints().containsKey(user1.getId()));
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

    @Test
    public void directorTests() {
        Director director1 = new Director();
        director1.setName("Director");

        Director director2 = new Director();
        director2.setName("Other Director");

        Director directorToUpdate = new Director();
        directorToUpdate.setId(1);
        directorToUpdate.setName("Updated Director");

        directorService.createDirector(director1);

        assertThat(directorService.getDirectorById(1)).hasFieldOrPropertyWithValue("name", "Director");

        Assertions.assertEquals(directorService.getDirectorSet().size(), 1);
        Assertions.assertTrue(directorService.getDirectorSet().contains(director1));

        directorService.createDirector(director2);

        Assertions.assertEquals(directorService.getDirectorById(2), director2);
        Assertions.assertEquals(directorService.getDirectorSet().size(), 2);
        Assertions.assertTrue(directorService.getDirectorSet().contains(director2));

        directorService.updateDirector(directorToUpdate);

        Assertions.assertEquals(directorService.getDirectorById(1), directorToUpdate);
        Assertions.assertEquals(directorService.getDirectorSet().size(), 2);
        Assertions.assertTrue(directorService.getDirectorSet().contains(directorToUpdate));
        Assertions.assertFalse(directorService.getDirectorSet().contains(director1));

        directorService.removeDirectorById(2);

        Assertions.assertThrows(NotFoundException.class, () -> directorService.getDirectorById(2));
    }

    @Test
    void updateReview_shouldReturnUpdatedReview() {
        User user1 = User.builder()
                .id(1L)
                .name("login")
                .login("login")
                .email("login@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        userService.createUser(user1);
        Film film1 = Film.builder()
                .id(1L)
                .name("Rocky")
                .description("BOX")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();
        filmService.createFilm(film1);
        Review review = Review.builder()
                .reviewId(1L)
                .content("Review text")
                .isPositive(false)
                .userId(user1.getId())
                .filmId(film1.getId())
                .useful(199)
                .build();
        reviewService.createReview(review);
        assertEquals(reviewService.getReviewById(1L).getReviewId(), review.getReviewId());
        Review updateReview = Review.builder()
                .reviewId(1L)
                .content("Review text updated")
                .isPositive(true)
                .userId(2L)
                .filmId(2L)
                .useful(10)
                .build();
        reviewService.updateReview(updateReview);
        System.out.println(reviewService.getReviewById(1L).toString());
        assertEquals(reviewService.getReviewById(1L).getUserId(), 1L);
        assertEquals(reviewService.getReviewById(1L).getFilmId(), 1L);
    }

    @Test
    public void testFeed_shouldReturnFeedUser() {
        User user1 = User.builder()
                .id(1L)
                .name("login")
                .login("login")
                .email("login@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        userService.createUser(user1);
        Film film1 = Film.builder()
                .id(1L)
                .name("Rocky")
                .description("BOX")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();
        filmService.createFilm(film1);
        Review review = Review.builder()
                .reviewId(1L)
                .content("Review text")
                .isPositive(false)
                .userId(user1.getId())
                .filmId(film1.getId())
                .useful(199)
                .build();
        reviewService.createReview(review);

        Assertions.assertEquals(1, feedService.getFeedByUserId(user1.getId()).size());
        Assertions.assertEquals(user1.getId(), feedService.getFeedByUserId(user1.getId()).get(0).getUserId());
        Assertions.assertEquals(Operation.ADD, feedService.getFeedByUserId(user1.getId()).get(0).getOperation());
        Assertions.assertEquals(Event.REVIEW, feedService.getFeedByUserId(user1.getId()).get(0).getEventType());
        Assertions.assertEquals(user1, userService.getUserById(feedService.getFeedByUserId(user1.getId())
                .get(0)
                .getUserId()));
        Assertions.assertEquals(film1.getId(), feedService.getFeedByUserId(user1.getId()).get(0).getEntityId());
    }

    @Test
    public void testCommonFilms() {
        User user1 = User.builder()
                .id(1L)
                .name("login")
                .login("login")
                .email("login@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        userService.createUser(user1);

        User user2 = User.builder()
                .id(2L)
                .name("login")
                .login("login")
                .email("login@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        userService.createUser(user2);
        userService.createFriend(user1.getId(), user2.getId());
        userService.createFriend(user2.getId(), user1.getId());
        Film film1 = Film.builder()
                .id(1L)
                .name("Rocky")
                .description("BOX")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .points(new HashMap<>())
                .genres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))))
                .mpa(new MPA(4, "R"))
                .build();
        filmService.createFilm(film1);
        filmService.addMark(film1.getId(), new LikeInputDto(user1.getId(), 6));
        filmService.addMark(film1.getId(), new LikeInputDto(user2.getId(), 7));

        assertEquals(filmService.getFilmById(film1.getId()),
                filmService.getFilmById(filmService.commonFilms(user1.getId(), user2.getId()).get(0).getId()));
        assertEquals(1, filmService.commonFilms(user1.getId(), user2.getId()).get(0).getId());
    }
}
