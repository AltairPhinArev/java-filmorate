package ru.yandex.practicum.filmorate.AplicationTest.recommendation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationsTest {
    private static final int USER_COUNT = 10;
    private static final int FILM_COUNT = 10;
    private final RecommendationService recommendationService;
    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;
    private boolean requireSet = true;

    @BeforeEach
    public void setDatabase() {
        if (requireSet) {
            jdbcTemplate.update(TestUtils.getSqlForAddUsers(USER_COUNT));
            jdbcTemplate.update(TestUtils.getSqlForAddFilms(FILM_COUNT));

            requireSet = false; //обновить надо один раз
        }
        //сбрасываем таблицу лайков и создаем ее снова
        jdbcTemplate.update("drop table film_likes");
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS film_likes " +
                "(film_id  bigint REFERENCES films (id) ON DELETE CASCADE, " +
                "user_id bigint REFERENCES users (id) ON DELETE CASCADE);");
    }

    @Test
    public void voidSingleRecommendationTest() {
        //никаких лайков вообще нет
        List<Film> films = recommendationService.recommendBySingleUser(1);
        assertEquals(films.size(), 0);
    }

    @Test
    public void normalSingleRecommendationTest() {
        //расставляем лайки
        filmService.addLike(1L, 1L);
        filmService.addLike(2L, 1L);
        filmService.addLike(3L, 1L);
        filmService.addLike(4L, 1L);
        filmService.addLike(5L, 1L);
        filmService.addLike(2L, 2L);
        filmService.addLike(3L, 2L);
        filmService.addLike(1L, 3L);
        filmService.addLike(4L, 3L);
        filmService.addLike(1L, 4L);
        filmService.addLike(2L, 4L);
        filmService.addLike(3L, 4L);
        filmService.addLike(6L, 4L);
        filmService.addLike(7L, 4L);
        //выбираем рекомендации одного пользователя (это 4)
        List<Film> films = recommendationService.recommendBySingleUser(1);
        for (Film film : films) {
            System.out.println(film);
        }
        //проверяем рекомендации
        assertEquals(films.size(), 2);
        assertEquals(films.get(0).getId(), 6);
        assertEquals(films.get(1).getId(), 7);
    }

    @Test
    public void normalSingleRecommendationWithEqualsTest() {
        //расставляем лайки так, чтобы ближайших пользователей было несколько
        filmService.addLike(1L, 1L);
        filmService.addLike(2L, 1L);
        filmService.addLike(3L, 1L);
        filmService.addLike(4L, 1L);
        filmService.addLike(5L, 1L);
        filmService.addLike(2L, 2L);
        filmService.addLike(3L, 2L);
        filmService.addLike(6L, 2L);
        filmService.addLike(1L, 3L);
        filmService.addLike(4L, 3L);
        filmService.addLike(7L, 4L);
        filmService.addLike(2L, 4L);
        filmService.addLike(3L, 4L);
        filmService.addLike(8L, 4L);
        //выбираем рекомендации одного пользователя (их трое: 2, 3 и 4)
        List<Film> films = recommendationService.recommendBySingleUser(1);
        for (Film film : films) {
            System.out.println(film);
        }
        //проверяем рекомендации
        assertEquals(films.size(), 1);
        assertEquals(films.get(0).getId(), 6); //пользователь с наименьшим номером
    }
}
