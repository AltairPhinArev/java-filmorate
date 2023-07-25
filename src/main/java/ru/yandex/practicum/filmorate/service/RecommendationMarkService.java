package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@Slf4j
@Service
public class RecommendationMarkService extends AbstractRecommendationService {
    @Autowired
    public RecommendationMarkService(UserDbStorage userStorage,
                                     @Qualifier("DbMarkRecommendations") RecommendationStorage recommendationStorage) {
        super(userStorage);
        this.recommendationStorage = recommendationStorage;
    }
}
