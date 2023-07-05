package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.ratingMPA.MpaDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MpaService {

    MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public MPA getMpaRateById(Integer mpaId) {
        return mpaDbStorage.getMPAById(mpaId);
    }

    public List<MPA> getAllMpa() {
        return mpaDbStorage.getAllMPA().stream()
                .sorted(Comparator.comparing(MPA::getId))
                .collect(Collectors.toList());
    }
}
