package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAll() {
        return genreStorage.getAllGenre().stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toList());
    }

    public Genre getGenreById(Long id) {
        return genreStorage.getGenreById(id);
    }

    public void addGenreToFilm(Film film) {
        genreStorage.addGenreToFilm(film);
    }
}
