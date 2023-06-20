package ru.yandex.practicum.filmorate.storage.ratingMPA;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Component
public class MpaDbStorage {

    JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MPA> getAllMPA() {
        String sql = "SELECT * FROM ratings_mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MPA(
                rs.getInt("id"),
                rs.getString("name"))
        );
    }

    public MPA getMPAById(Integer id) {
        if (id == null) {
            throw new ValidationException("id was not selected, or id was too long");
        }

        MPA mpa;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM ratings_mpa WHERE id = ?", id);
        if (sqlRowSet.first()) {
            mpa = new MPA(sqlRowSet.getInt("id"), sqlRowSet.getString("name"));
        } else {
            throw new NotFoundException(id + "not founded");
        }
        return mpa;
    }
}
