package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.ratingMPA.MpaStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MpaService {

    MpaStorage mpaStorage;

    @Autowired

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MPA getMpaRateById(Integer mpaId) {
        return mpaStorage.getMPAById(mpaId);
    }

    public List<MPA> getAllMpa() {
        return mpaStorage.getAllMPA().stream().sorted(Comparator.comparing(MPA::getId)).collect(Collectors.toList());
    }
}
