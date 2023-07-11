package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__ (@Autowired))
public class DirectorService {

    private final DirectorDbStorage storage;

    /*
     Добавляем нового режиссера, при условии, что он имеет корректные поля,
     в противном случае бросаем ValidationException
     */
    public Director createDirector(Director director) {
        return storage.createDirector(director);
    }

    /*
     Обновляем уже лежащего у нас режиссера, если новый имеет корректные поля
     */
    public Director updateDirector(Director director) {
        storage.getDirectorById(director.getId())
                .orElseThrow(()
                        -> new NotFoundException(String.format("Режиссер с ID: %d не найден", director.getId())));
        return storage.updateDirector(director);
    }

    /*
     Если есть, собрали и вернули список режиссеров
     */
    public Set<Director> getDirectorSet() {
        try {
            Set<Director> directors = storage.getDirectorsSet();
            if (directors != null) {
                log.info("Собрали и вернули список режиссеров");
                return directors;
            } else {
                throw new RuntimeException("Не смогли собрать и вернуть список режиссеров");
            }
        } catch (RuntimeException exception) {
            throw new NotFoundException(exception.getMessage());
        }
    }

    public Set<Director> getDirectorByFilmId(Long filmId) {
        return storage.getDirectorsByFilmId(filmId);
    }

    /*
     Если нашли возвращаем режиссера по ID, если нет бросаем NotFoundException
    */
    public Optional<Director> getDirectorById(int id) {
        Optional<Director> director = storage.getDirectorById(id);
        if (director.isPresent()) {
            log.info("Нашли и вернули режиссера с ID: {}", id);
            return director;
        } else {
            throw new NotFoundException(String.format("Режиссер с ID: %d не найден", id));
        }
    }

    /*
     Если есть в хранилище, то удаляем режиссера по ид
     */
    public void removeDirectorById(int id) {
        storage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Режиссер с ID: %d не найден", id)));
        storage.removeDirectorById(id);
        log.info("Удалили режиссера с ID: {}", id);
    }
}
