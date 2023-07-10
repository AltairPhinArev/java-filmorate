package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Set<Genre> genres;

    private MPA mpa;

    private Set<Long> voytedUsers;

    private Set<Director> directors;

    ////////////////////////// Обновление коллекций //////////////////////////

    public boolean isLikeNew(long likeId) {
        for (long like : voytedUsers) {
            if (like == likeId) {
                return false;
            }
        }
        return true;
    }

    public boolean isGenreNew(long genreId) {
        for (Genre genre : genres) {
            if (genre.getId() == genreId) {
                return false;
            }
        }
        return true;
    }

    public boolean isDirectorNew(long directorId) {
        if (directors == null) {
            directors = new HashSet<>();
        }
        for (Director director : directors) {
            if (director.getId() == directorId) {
                return false;
            }
        }
        return true;
    }

    public void addLike(long like) {
        if (voytedUsers == null) {
            voytedUsers = new HashSet<>();
        }
        voytedUsers.add(like);
    }

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new HashSet<>();
        }
        genres.add(genre);
    }

    public void addDirector(Director director) {
        if (directors == null) {
            directors = new HashSet<>();
        }
        directors.add(director);
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
        //сохраняем лайки
        if (film.getVoytedUsers() == null) {
            film.setVoytedUsers(new HashSet<>());
        }
        int likeId = rs.getInt("film_likes.user_id");
        if ((likeId > 0) && (film.isLikeNew(likeId))) { //он есть и новый
            film.addLike(likeId);
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
        //подгружаем режиссеров
        if (film.getDirectors() == null) {
            film.setDirectors(new HashSet<>());
        }
        int directorId = rs.getInt("directors.id");
        if ((directorId > 0) && (film.isDirectorNew(directorId))) { //он есть и новый
            //создаем объект-режиссер
            Director director = new Director(directorId, rs.getString("directors.name"));
            //добавляем его к фильму
            film.addDirector(director);
        }
        //пишем фильм в хранилище
        map.put(filmId, film);
    }
}
