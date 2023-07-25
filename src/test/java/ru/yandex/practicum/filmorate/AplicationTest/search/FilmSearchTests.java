package ru.yandex.practicum.filmorate.AplicationTest.search;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeInputDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmMarkService;
import ru.yandex.practicum.filmorate.service.SearchMarkService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmSearchTests {
    private final SearchMarkService service;
    private final FilmMarkService filmService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void searchByNameTest() {
        List<Film> filmsIn = TestUtils.generateFilms();
        jdbcTemplate.update(TestUtils.getSqlForResetFilms(4, filmsIn));
        List<Film> films = service.searchFilms("все", "title");
        for (Film film : films) {
            System.out.println(film);
        }
        assertEquals(films.size(), 2);
        assertEquals(films.get(0).getId(), 1);
        assertEquals(films.get(1).getId(), 3);
    }

    @Test
    public void searchByNameAsPostmanTest() {
        List<Film> filmsIn = TestUtils.generateFilmsAsPostman();
        jdbcTemplate.update(TestUtils.getSqlForResetFilms(3, filmsIn));
        userService.createUser(new User(
                1L, "a@bc", "aa", "xx", LocalDate.of(1970, 12, 1)));
        filmService.addMark(3L, new LikeInputDto(1L, 6));
        List<Film> films = service.searchFilms("upDatE", "title,director");
        for (Film film : films) {
            System.out.println(film);
        }
        assertEquals(films.size(), 2);
        assertEquals(films.get(0).getId(), 3);
        assertEquals(films.get(1).getId(), 1);
    }

    @Test
    public void searchByDirectorTest() {
        List<Film> filmsIn = TestUtils.generateFilms();
        jdbcTemplate.update(TestUtils.getSqlForResetFilms(4, filmsIn));
        List<Film> films = service.searchFilms("все", "director");
        for (Film film : films) {
            System.out.println(film);
        }
        assertEquals(films.size(), 2);
        assertEquals(films.get(0).getId(), 1);
        assertEquals(films.get(1).getId(), 4);
    }

    @Test
    public void searchByNameOrDirectorTest() {
        List<Film> filmsIn = TestUtils.generateFilms();
        jdbcTemplate.update(TestUtils.getSqlForResetFilms(4, filmsIn));
        List<Film> films = service.searchFilms("все", "title,director");
        for (Film film : films) {
            System.out.println(film);
        }
        assertEquals(films.size(), 3);
        assertEquals(films.get(0).getId(), 1);
        assertEquals(films.get(1).getId(), 3);
        assertEquals(films.get(2).getId(), 4);
    }
}
