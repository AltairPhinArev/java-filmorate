package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.search.SearchStorage;

@Service
public class SearchMarkService extends AbstractSearchService {
    @Autowired
    public SearchMarkService(@Qualifier("DbMarkSearchStorage") SearchStorage searchStorage) {
        this.searchStorage = searchStorage;
    }
}