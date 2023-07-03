package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.ValidationException;
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
        if (validate(director).isPresent()) {
            return storage.createDirector(director);
        } else {
            return null;
        }
    }

    /*
     Обновляем уже лежащего у нас режиссера, если новый имеет корректные поля
     */
    public Director updateDirector(Director director) {
        if (storage.getDirectorById(director.getId()).isPresent()) {
            log.info("Режиссер с ID: {} обновлен", director.getId());
            if (validate(director).isPresent()) {
                return storage.updateDirector(validate(director).get());
            }
        } else {
            throw new NotFoundException(String.format("Can not found director by ID : %d", director.getId()));
        }
        return null;
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
        if (storage.getDirectorById(id).isPresent()) {
            storage.removeDirectorById(id);
            log.info("Удалили режиссера с ID: {}", id);
        } else {
            throw new NotFoundException(String.format("Can not found director by ID: %d", id));
        }
    }

    /*
     Проверяем входящего режиссера на корректность полей
    */
    private Optional<Director> validate(@NotNull Director director) {
        if (!director.getName().isEmpty() && !director.getName().isBlank()) {
            if (director.getId() > 0) {
                return Optional.of(director);
            } else {
                log.error("Некорректный ID режиссера: {}", director.getId());
                throw new ValidationException("Illegal id from director");
            }
        } else {
            log.error("Некорректное name режиссера: {}", director.getName());
            throw new ValidationException("Illegal name from director");
        }
    }
}
