package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAll() {
        return genreDbStorage.getAllGenre()
                .stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public Genre getGenreById(Integer id) {
        return genreDbStorage.getGenreById(id);
    }

    public void addGenreToFilm(Film film) {
        genreDbStorage.addGenreToFilm(film);
    }

    public void reNewGenre(Film film) {
        genreDbStorage.deleteGenreFromFilm(film);
        genreDbStorage.addGenreToFilm(film);
    }

    public List<Genre> getGenresByFilmId(Long filmId) {
        return genreDbStorage.getFilmGenres(filmId);
    }
}
