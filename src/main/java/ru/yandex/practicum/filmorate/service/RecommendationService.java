package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@Service
public class RecommendationService extends AbstractRecommendationService {
    @Autowired
    public RecommendationService(UserDbStorage userStorage,
                                 @Qualifier("DbRecommendations") RecommendationStorage recommendationStorage) {
        super(userStorage);
        this.recommendationStorage = recommendationStorage;
    }
}
