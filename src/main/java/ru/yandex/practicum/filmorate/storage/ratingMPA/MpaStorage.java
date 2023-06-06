package ru.yandex.practicum.filmorate.storage.ratingMPA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.UserOrFilmNotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public class MpaStorage {

    JdbcTemplate jdbcTemplate;


    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MPA> getAllMPA() {
        String sql = "SELECT * FROM rating_mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MPA(
                rs.getLong("id"),
                rs.getString("name"))
        );
    }

    public MPA getMpaById(Long mpaId) {
        if (mpaId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        MPA mpaRating;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM ratings_mpa WHERE id = ?", mpaId);
        if (sqlRowSet.first()) {
            mpaRating = new MPA(
                    sqlRowSet.getLong("id"),
                    sqlRowSet.getString("name")
            );
        } else {
            throw new UserOrFilmNotFoundException(mpaId + " не найден!");
        }
        return mpaRating;
    }
}
