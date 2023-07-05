package ru.yandex.practicum.filmorate.AplicationTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final FilmService filmService;
	private final UserService userService;

	private final DirectorService directorService;

	@Test
	void testCreateUser() {
		User user = User.builder()
				.id(0L)
				.name("ANtonY")
				.login("technojew")
				.birthday(LocalDate.of(1975, 11, 19))
				.email("fositik@yandex.ru").build();

		Assertions.assertEquals(user,  userStorage.createUser(user));
	}

	@Test
	public void testCreateFilm() {
		Film film1 = new Film(
				1L,
				"Fighting club",
				"2 = 1",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Arrays.asList(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());


		Assertions.assertEquals(film1, filmService.createFilm(film1));
	}

	@Test
	public void testGetFilmById() {
		Film film1 = new Film(
				1L,
				"Alisa",
				"eat me",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Set.of(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());

		filmService.createFilm(film1);
		Assertions.assertEquals(film1, filmService.getFilmById(film1.getId()));
	}

	@Test
	public void testGetAllFilms() {
		Film film1 = new Film(
				1L,
				"Charly and Chocolate Fabric",
				"Johny Depp",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Arrays.asList(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());

		Film film2 = new Film(
				2L,
				"SuperFast",
				"---",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Arrays.asList(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());


		filmService.createFilm(film1);
		filmService.createFilm(film2);

		Assertions.assertEquals(2, filmService.findAll().size());
	}

	@Test
	public void testRemoveFilmById() {
		Film film1 = new Film(
				1L,
				"superFast 100",
				"The last of last of last",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Arrays.asList(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());

		Film film2 = new Film(
				2L,
				"FightigngClub",
				"",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Arrays.asList(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());


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
		Film film1 = new Film(
				1L,
				"Rocky",
				"BOX",
				LocalDate.of(1975, 11, 19),
				133,
				new HashSet<>(Arrays.asList(new Genre(2, "Драма"))),
				new MPA(4, "R"),
				new HashSet<>(),
				new HashSet<>());

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

		assertThat(directorService.getDirectorById(1))
				.isPresent()
						.hasValueSatisfying(director -> assertThat(director)
								.hasFieldOrPropertyWithValue("name", "Director"));

		Assertions.assertEquals(directorService.getDirectorSet().size(), 1);
		Assertions.assertTrue(directorService.getDirectorSet().contains(director1));

		directorService.createDirector(director2);

		Assertions.assertEquals(directorService.getDirectorById(2).get(), director2);
		Assertions.assertEquals(directorService.getDirectorSet().size(), 2);
		Assertions.assertTrue(directorService.getDirectorSet().contains(director2));

		directorService.updateDirector(directorToUpdate);

		Assertions.assertEquals(directorService.getDirectorById(1).get(), directorToUpdate);
		Assertions.assertEquals(directorService.getDirectorSet().size(), 2);
		Assertions.assertTrue(directorService.getDirectorSet().contains(directorToUpdate));
		Assertions.assertFalse(directorService.getDirectorSet().contains(director1));

		directorService.removeDirectorById(2);

		Assertions.assertThrows(NotFoundException.class, () -> directorService.getDirectorById(2));
	}
}
