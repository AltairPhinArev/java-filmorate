package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feedTypes.Event;
import ru.yandex.practicum.filmorate.model.feedTypes.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedService feedService;

    public Review createReview(Review newReview) {
        validateFilmIdAndUserId(newReview.getFilmId(), newReview.getUserId());
        Review review = reviewStorage.createReview(newReview);
        feedService.setOperation(newReview.getUserId(), Event.REVIEW, Operation.ADD, review.getReviewId());
        return review;
    }

    public Review updateReview(Review updatedReview) {
        validateReviewId(updatedReview.getReviewId());
        feedService.setOperation(updatedReview.getUserId() - 1, Event.REVIEW, Operation.UPDATE,
                updatedReview.getReviewId());
        return reviewStorage.updateReview(updatedReview);
    }

    public void removeReview(Long deletedReviewId) {
        validateReviewId(deletedReviewId);
        feedService.setOperation(getReviewById(deletedReviewId).getUserId(),
                Event.REVIEW, Operation.REMOVE, getReviewById(deletedReviewId).getFilmId());
        reviewStorage.removeReview(deletedReviewId);
    }

    public Review getReviewById(Long reviewId) {
        validateReviewId(reviewId);
        return reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с указанным ID не найден: " + reviewId));
    }

    public List<Review> getAllReviews(Long filmId, int count) {
        log.warn("Фильм с id {} не найден", filmId);
        return reviewStorage.getAllReviews(filmId, count);
    }

    public void likeReview(Long reviewId, Long userId) {
        validateReviewIdAndUserId(reviewId, userId);
        reviewStorage.likeReview(reviewId, userId);
      //  feedService.setOperation(userId, Event.REVIEW, Operation.UPDATE, reviewId);
    }

    public void dislikeReview(Long reviewId, Long userId) {
        validateReviewIdAndUserId(reviewId, userId);
        reviewStorage.dislikeReview(reviewId, userId);
       // feedService.setOperation(userId, Event.REVIEW, Operation.UPDATE, reviewId);
    }

    public void removeLike(Long reviewId, Long userId) {
        validateReviewIdAndUserId(reviewId, userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        validateReviewIdAndUserId(reviewId, userId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private void validateFilmId(Long filmId) {
        if (!filmStorage.filmExists(filmId)) {
            log.warn("Фильм с id {} не найден", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void validateUserId(Long userId) {
        if (!userStorage.userExists(userId)) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }

    private void validateReviewId(Long reviewId) {
        if (!reviewStorage.reviewExists(reviewId)) {
            log.warn("Отзыв с id {} не найден", reviewId);
            throw new NotFoundException("Отзыв с id " + reviewId + " не найден.");
        }
    }

    private void validateReviewIdAndUserId(Long reviewId, Long userId) {
        validateReviewId(reviewId);
        validateUserId(userId);
    }

    private void validateFilmIdAndUserId(Long filmId, Long userId) {
        validateFilmId(filmId);
        validateUserId(userId);
    }
}
