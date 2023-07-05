package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.ratingMPA.MpaDbStorage;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorDbStorage {  // Класс отвечающий за общение с хранилищем режиссеров

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private final LikeDbStorage likeDbStorage;

    @Autowired
    private final MpaDbStorage mpaStorage;

    /*
     Добавляем нового режиссера в хранилище
     */

    public Director createDirector(Director director) {
        List<Integer> directorsId = getDirectorsId();
        if (!directorsId.isEmpty()) {  // проверяем есть ли ид в хранилище
            int currentId = Collections.max(directorsId);  // генерим ид в зависимости от того что лежит в хранилище
            director.setId(++currentId);   // присваиваем корректный ид режиссеру
        } else {
            director.setId(1);  // если хранилище пусто, то ид - 1
        }

        String sqlQuery = "INSERT INTO directors (id, name)" +
                        " VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, director.getId(), director.getName());
        log.info("Добавили нового режиссера с ID: {}", director.getId());
        return director;
    }

    /*
     Обновляем данные о режиссере который в хранилище уже лежал
     */

    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE directors " +
                "SET id=?, name=? " +
                "WHERE id=?";
        jdbcTemplate.update(sqlQuery, director.getId(), director.getName(), director.getId());
        log.info("Обновили режиссера с ID: {}", director.getId());
        return director;
    }

    /*
     Получаем список всех режиссеров из хранилища
     */

    public Set<Director> getDirectorsSet() {
        String sqlQuery = "SELECT * " +
                "FROM directors";
        HashSet<Director> directors = new HashSet<>(jdbcTemplate.query(sqlQuery, new DirectorMapper()));
        log.info("Достали список режиссеров");
        return directors;
    }

    /*
     Достаем режиссера по ID
     */
    public Optional<Director> getDirectorById(int id) {
        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "WHERE id=?";
        Director director = jdbcTemplate.query(sqlQuery, new DirectorMapper(), id).stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Не нашли режиссера с ID: %d", id)));
        log.info("Достали режиссера с ID: {}", id);
        return Optional.of(director);
    }

    /*
        удаляем режиссера по ид
     */
    public void removeDirectorById(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Удалили режиссера с ID: {}", id);
    }

    /*
     Добавляем в хранилище film_directors ид фильма и ид его режиссера
     */
    public void addDirectorToFilm(Film film) {      // Добавляем в хранилище ид фильма и ид режиссера
        String sqlQuery = "INSERT INTO film_directors (film_id, director_id)" +
                " VALUES(?, ?)";
        film.getDirectors().forEach(director -> jdbcTemplate.update(sqlQuery, film.getId(), director.getId()));
        log.info("Закинули режиссеров фильма с ID: {} в хранилище", film.getId());
    }

    /*
     Достаем из хранилища список режиссеров по ид фильма
     */
    public Set<Director> getDirectorsByFilmId(long filmId) {
        String sqlQuery = "SELECT * FROM film_directors AS fd" +
                " LEFT JOIN directors AS d ON fd.director_id=d.id" +
                " WHERE film_id=?";
        HashSet<Director> directors = new HashSet<>(jdbcTemplate.query(sqlQuery, new DirectorMapper(), filmId));
        log.info("Достали список режиссеров фильма с ID: {}", filmId);
        return directors;
    }

    public void removeDirectorByFilmId(long filmId) {
        String sqlQuery = "DELETE FROM film_directors WHERE film_id=?";
        jdbcTemplate.update(sqlQuery, filmId);
        log.info("Удалили режиссера у фильма с ID: {}", filmId);
    }

    /*
     смотрим корректные ид в хранилище
     */

    private List<Integer> getDirectorsId() {
        String sqlQuery = "SELECT * " +
                "FROM directors";
        return jdbcTemplate.query(sqlQuery, (rs, rowNun) -> rs.getInt("id"));

    }
}
