package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorMapper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DirectorDbStorage {  // Класс отвечающий за общение с хранилищем режиссеров

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Director createDirector(Director director) {        // Добавляем нового режиссера в хранилище
        String sqlQuery = "INSERT INTO directors (id, name)" +
                "VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, director.getId(), director.getName());
        return director;
    }

    public Director updateDirector(Director director) {  // Обновляем данные о режиссере который в хранилище уже лежал
        String sqlQuery = "UPDATE directors " +
                "SET id=?, name=? " +
                "WHERE id=?";
        jdbcTemplate.update(sqlQuery, director.getId(), director.getName(), director.getId());
        return director;
    }

    public Set<Director> getDirectorsSet() {        // Получаем список всех режиссеров из хранилища
        String sqlQuery = "SELECT * " +
                "FROM directors";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new DirectorMapper()));
    }

    public Optional<Director> getDirectorById(int id) {       // Достаем одного режиссера по ID
        String sqlQuery = "SELECT * " +
                "FROM directors " +
                "WHERE id=?";

        return jdbcTemplate.query(sqlQuery, new DirectorMapper()).stream().findAny();
    }

    public void removeDirectorById(int id) {        // Удаляем режиссера по ID
        String sqlQuery = "DELETE FROM directors WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
