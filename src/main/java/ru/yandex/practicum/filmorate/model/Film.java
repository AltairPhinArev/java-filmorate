package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Film {
    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Integer rate = 0;

    private Set<Genre> genres;

    private MPA mpa;

    private Set<Long> voytedUsers;

    private Director director;

    @Builder
    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration,
                Set<Long> voytedUsers, Set<Genre> genres, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.voytedUsers = voytedUsers;
        this.genres = genres;
        this.mpa = mpa;
        director = null;
    }

    ////////////////////////// Обновление коллекций //////////////////////////

    public boolean isGenreNew(long genreId) {
        for (Genre genre : genres) {
            if (genre.getId() == genreId) {
                return false;
            }
        }
        return true;
    }

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new HashSet<>();
        }
        genres.add(genre);
    }

    /////////////////////////////// Конвертация //////////////////////////////

    //распаковка строки таблицы films в фильм (без связей)
    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("films.id"))
                .name(resultSet.getString("films.name"))
                .description(resultSet.getString("films.description"))
                .releaseDate(resultSet.getDate("films.release_date").toLocalDate())
                .duration(resultSet.getInt("films.duration"))
                .mpa(new MPA(resultSet.getInt("films.rating_id"), null))
                .build();
        film.setGenres(new HashSet<>());
        return film;
    }

    //распаковка строки со всеми связями в фильм
    public static void storeFullRow(ResultSet rs, Map<Long, Film> map) throws SQLException {
        Film film;
        //читаем идентификатор
        long filmId = rs.getLong("films.id");
        if (!map.containsKey(filmId)) { //фильма еще не было в map
            film = mapRowToFilm(rs, 0); //создаем его из набора
        } else { //он уже был
            film = map.get(filmId); //читаем его из map
        }
        //подгружаем имя рейтинга
        film.getMpa().setName(rs.getString("ratings_mpa.name"));
        //читаем из сводной таблицы жанр
        int genreId = rs.getInt("genres.id");
        if ((genreId > 0) && (film.isGenreNew(genreId))) { //он есть и новый
            //создаем объект-жанр
            Genre genre = new Genre(genreId, rs.getString("genres.name"));
            //добавляем его к фильму
            film.addGenre(genre);
        }
        //подгружаем директора
        Director director = new Director(rs.getInt("directors.id"),
                rs.getString("directors.name"));
        film.setDirector(director);
        //пишем фильм в хранилище
        map.put(filmId, film);
    }
}
