package ru.yandex.practicum.filmorate.AplicationTest.recommendation;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestUtils {

    ///////////////////////// Поддержка пользователей ////////////////////////

    //создает пользователя с заданным номером
    public static User generateUser(long number) {
        User user = User.builder()
                .id(number)
                .login("user" + number)
                .name("name" + number)
                .email("user" + number + "@yandex.ru")
                .birthday(LocalDate.of(1940, 12, 9))
                .build();
        user.setFriends(new HashSet<>());
        return user;
    }

    //создает коллекцию пользователей
    public static List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(generateUser(i + 1));
        }
        return users;
    }

    public static String getSqlForAddUsers(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        List<User> users = generateUsers(count);
        for (int i = 1; i <= count; i++) {
            User user = users.get(i - 1);
            stringBuilder.append(
                    "INSERT INTO users (name, login, email, birthday) ");
            stringBuilder.append(String.format(
                    "VALUES ('%s', '%s', '%s', '1940-12-09');",
                    user.getName(), user.getLogin(), user.getEmail()));
        }
        return stringBuilder.toString();
    }

    //////////////////////////// Поддержка фильмов ///////////////////////////

    //создает фильм с заданным номером
    public static Film generateFilm(int number) {
        return Film.builder()
                .name("Film" + number)
                .description("Description" + number)
                .releaseDate(LocalDate.of(1940, 12, 9))
                .duration(17)
                .genres(new HashSet<>())
                .mpa(new MPA(1, "G"))
                .build();
    }

    //создает коллекцию фильмов
    public static List<Film> generateFilms(int count) {
        List<Film> films = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            films.add(generateFilm(i + 1));
        }
        return films;
    }

    public static String getSqlForAddFilms(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Film> films = generateFilms(count);
        for (int i = 1; i <= count; i++) {
            //вставляем в films очередной фильм
            Film film = films.get(i - 1);
            stringBuilder.append(
                    "INSERT INTO films (name, description, release_date, duration, rating_id) ");
            stringBuilder.append(String.format(
                    "VALUES ('%s', 'description%d', '1940-12-09', 17, %d);",
                    film.getName(), i, (i - 1) % 5 + 1));
        }
        return stringBuilder.toString();
    }
}
