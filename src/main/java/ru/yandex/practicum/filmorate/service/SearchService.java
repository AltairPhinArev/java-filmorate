package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.search.SearchStorage;

@Service
public class SearchService extends AbstractSearchService {
    @Autowired
    public SearchService(@Qualifier("DbSearchStorage") SearchStorage searchStorage) {
        this.searchStorage = searchStorage;
    }
}