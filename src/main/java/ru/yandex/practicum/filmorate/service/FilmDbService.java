package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@Service
public class FilmDbService {

    FilmDbStorage filmDbStorage;
    UserDbStorage userDbStorage;


}
