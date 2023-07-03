package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rateFilms.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.ratingMPA.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class DirectorDbStorage {  // Класс отвечающий за общение с хранилищем режиссеров

    private final JdbcTemplate jdbcTemplate;

    GenreDbStorage genreStorage;

    LikeDbStorage likeDbStorage;

    MpaDbStorage mpaStorage;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate,
                            GenreDbStorage genreDbStorage,
                            LikeDbStorage likeDbStorage,
                            MpaDbStorage mpaDbStorage)
    {this.jdbcTemplate = jdbcTemplate;
     this.genreStorage = genreDbStorage;
     this.likeDbStorage = likeDbStorage;
     this.mpaStorage = mpaDbStorage;}

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

        log.info("Добавили нового режиссера с ID: {}", director.getId());
        jdbcTemplate.update(sqlQuery, director.getId(), director.getName());
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
        return director;
    }

    /*
     Получаем список всех режиссеров из хранилища
     */

    public Set<Director> getDirectorsSet() {
        String sqlQuery = "SELECT * " +
                "FROM directors";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new DirectorMapper()));
    }

    /*
     Достаем режиссера по ID
     */
    public Optional<Director> getDirectorById(int id) {
        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "WHERE id=?";

        return jdbcTemplate.query(sqlQuery, new DirectorMapper(), id).stream().findAny();
    }

    /*
        удаляем режиссера по ид
     */
    public void removeDirectorById(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
    }

    /*
     Добавляем в хранилище film_directors ид фильма и ид его режиссера
     */
    public void addDirectorToFilm(Film film) {      // Добавляем в хранилище ид фильма и ид режиссера
        String sqlQuery = "INSERT INTO film_directors (film_id, director_id)" +
                " VALUES(?, ?)";
        film.getDirectors().forEach(director -> jdbcTemplate.update(sqlQuery, film.getId(), director.getId()));
    }

    /*
     Достаем из хранилища список режиссеров по ид фильма
     */
    public Set<Director> getDirectorsByFilmId(long filmId) {
        String sqlQuery = "SELECT * FROM film_directors AS fd" +
                " LEFT JOIN directors AS d ON fd.director_id=d.id" +
                " WHERE film_id=?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new DirectorMapper(), filmId));
    }

    public void removeDirectorByFilmId(long filmId) {
        String sqlQuery = "DELETE FROM film_directors WHERE film_id=?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    /*
     смотрим корректные ид в хранилище
     */

    private List<Integer> getDirectorsId() {
        String sqlQuery = "SELECT * " +
                "FROM directors";
        return jdbcTemplate.query(sqlQuery, (rs, rowNun) -> getId(rs));

    }

    private int getId(ResultSet rs) throws SQLException {
        return rs.getInt("id");
    }



}
