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
public class DirectorService {

    private final DirectorDbStorage storage;

    @Autowired
    public DirectorService(DirectorDbStorage storage) {
        this.storage = storage;
    }

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
        getDirectorById(director.getId());
        return storage.updateDirector(director);
    }

    /*
     Если есть, собрали и вернули список режиссеров
     */
    public Set<Director> getDirectorSet() {
        try {
            return storage.getDirectorsSet();
        } catch (RuntimeException exception) {
            throw new NotFoundException(exception.getMessage());
        }
    }

    /*
     Если нашли возвращаем режиссера по ID, если нет бросаем NotFoundException
    */
    public Director getDirectorById(int id) {
        return storage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Не нашли режиссера с ID: %d", id)));
    }

    /*
     Если есть в хранилище, то удаляем режиссера по ид
     */
    public void removeDirectorById(int id) {
        storage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Не нашли режиссера с ID: %d", id)));
        storage.removeDirectorById(id);
        log.info("Удалили режиссера с ID: {}", id);
    }
}
