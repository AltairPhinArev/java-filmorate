package ru.yandex.practicum.filmorate.AplicationTest.search;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestUtils {

    //////////////////////////// Поддержка фильмов ///////////////////////////

    //создает фильм с заданным номером
    public static Film generateFilm(long number, String name, Integer directorId, String directorName) {
        Film film = Film.builder()
                .id(number)
                .name(name)
                .description("Description" + number)
                .releaseDate(LocalDate.of(1940, 12, 9))
                .duration(17)
                .mpa(new MPA(1,"G"))
                .build();
        if (directorId == null) {
            film.setDirectors(null);
        } else {
            Set<Director> directors = new HashSet<>();
            directors.add(new Director(directorId, directorName));
            film.setDirectors(directors);
        }
        return film;
    }

    //создает коллекцию фильмов
    public static List<Film> generateFilms() {
        List<Film> films = new ArrayList<>();
        films.add(generateFilm(1, "Всемирная история", 1, "Всеволод Иванов"));
        films.add(generateFilm(2, "Учеба в сем практикуме", 2, "Андрей Громов"));
        films.add(generateFilm(3, "Совсем пропащий", 3,"Владимир Иванов"));
        films.add(generateFilm(4, "Новый фильм", 4, "Иван Овсеенко"));
        return films;
    }

    //создает коллекцию фильмов как в Postman
    public static List<Film> generateFilmsAsPostman() {
        List<Film> films = new ArrayList<>();
        films.add(generateFilm(1, "Film updated", null, null));
        films.add(generateFilm(2, "New film", null, null));
        films.add(generateFilm(3, "New film with director", 1,"Director updated"));
        return films;
    }

    public static String getSqlForResetFilms(int count, List<Film> films) {
        StringBuilder stringBuilder = new StringBuilder();
        //удаляем таблицу films, а также связанные с ними likes film_genre и directors
        stringBuilder.append("DROP TABLE IF EXISTS " +
                "films, film_likes, film_genres, directors, film_directors CASCADE; ");
        //воссоздаем эти таблицы из схемы (уже пустые)
        try {
            stringBuilder.append(new String(Files.readAllBytes(
                    Path.of("./src/main/resources/schema.sql"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 1; i <= count; i++) {
            //вставляем в films очередной фильм
            Film film = films.get(i - 1);
            stringBuilder.append(
                    "INSERT INTO films (name, description, release_date, duration, rating_id) ");
            stringBuilder.append(String.format(
                    "VALUES ('%s', 'description%d', '1940-12-09', 17, %d);",
                    film.getName(), i, (i - 1) % 5 + 1));
            //вставляем в film_genres жанр для него
            stringBuilder.append(
                    "INSERT INTO film_genres (film_id, genre_id) ");
            stringBuilder.append(String.format(
                    "VALUES (%d, %d);", i, (i - 1) % 6 + 1));
            //вставляем режиссеров
            Set<Director> directors = film.getDirectors();
            if (directors != null) {
                for (Director director : directors) {
                    //вставляем режиссера в таблицу directors
                    stringBuilder.append(
                            "INSERT INTO directors (id, name) ");
                    stringBuilder.append(String.format(
                            "VALUES (%d, '%s');", director.getId(), director.getName()));
                    //вставляем ссылку на режиссера в таблицу film_directors
                    stringBuilder.append(
                            "INSERT INTO film_directors (film_id, director_id) ");
                    stringBuilder.append(String.format(
                            "VALUES (%d, '%d');", film.getId(), director.getId()));
                }
            }
        }
        return stringBuilder.toString();
    }
}