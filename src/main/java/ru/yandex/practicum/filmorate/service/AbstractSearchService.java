package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.search.SearchStorage;

import java.util.List;

@Slf4j
public abstract class AbstractSearchService {
    private static final int BAD_BY_PARAM = 0;
    private static final int BY_TITLE = 1;
    private static final int BY_DIRECTOR = 2;
    private static final int BY_TITLE_AND_DIRECTOR = 3;
    protected SearchStorage searchStorage;

    public List<Film> searchFilms(String query, String by) {
        int searchMode = getSearchMode(by);
        switch (searchMode) {
            case BY_DIRECTOR:
                return searchFilmByDirector(query);
            case BY_TITLE:
                return searchFilmByName(query);
            case BY_TITLE_AND_DIRECTOR:
                return searchFilmByNameAndDirector(query);
            default:
                throw new ValidationException(HttpStatus.BAD_REQUEST, "Неверно задан режим поиска");
        }
    }

    private List<Film> searchFilmByName(String partialName) {
        return searchStorage.searchFilmByName(partialName);
    }

    private List<Film> searchFilmByDirector(String partialName) {
        return searchStorage.searchFilmByDirector(partialName);
    }

    private List<Film> searchFilmByNameAndDirector(String partialName) {
        return searchStorage.searchFilmByNameAndDirector(partialName);
    }

    private int getSearchMode(String by) {
        if ("".equals(by)) {
            return BAD_BY_PARAM; //режим поиска должен быть
        }
        String[] byItems = by.split(",");
        if (byItems.length == 1) {
            switch (by) {
                case "title":
                    return BY_TITLE;
                case "director":
                    return BY_DIRECTOR;
                default:
                    return BAD_BY_PARAM;
            }
        }
        if ((byItems.length == 2) && ("title".equals(byItems[0]) && "director".equals(byItems[1]) ||
                "title".equals(byItems[1]) && "director".equals(byItems[0]))) {
            return BY_TITLE_AND_DIRECTOR;
        } else {
            return BAD_BY_PARAM; //ошибочный параметр
        }
    }
}