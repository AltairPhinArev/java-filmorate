package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.Exceptions.CreateReviewException;
import ru.yandex.practicum.filmorate.Exceptions.LikeReviewException;
import ru.yandex.practicum.filmorate.Exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.Exceptions.RemoveReviewException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Review> reviewRowMapper = createRowMapper();

    private RowMapper<Review> createRowMapper() {
        return (rs, rowNum) -> Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful_count"))
                .build();
    }

    @Override
    public Review createReview(Review newReview) {
        try {
            newReview.setUseful(0);
            String sql = ReviewSqlQueries.CREATE_REVIEW;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(
                    connection -> {
                        PreparedStatement prSt = connection.prepareStatement(
                                sql, new String[]{"review_id"});
                        prSt.setString(1, newReview.getContent());
                        prSt.setBoolean(2, newReview.getIsPositive());
                        prSt.setLong(3, newReview.getUserId());
                        prSt.setLong(4, newReview.getFilmId());
                        prSt.setInt(5, newReview.getUseful());
                        return prSt;
                    }, keyHolder);
            newReview.setReviewId(keyHolder.getKey().longValue());
            if (rowsAffected > 0) {
                log.info("Отзыв создан: {}", newReview);
                return newReview;
            } else {
                log.warn("Не удалось создать отзыв: {}", newReview);
                return null;
            }
        } catch (DataAccessException ex) {
            log.error("Ошибка при создании отзыва: {}", newReview, ex);
            throw new CreateReviewException("Ошибка при создании ревью под id: " + newReview.getReviewId(), ex);
        }
    }

    @Override
    public Review updateReview(Review updatedReview) {
        log.info("Обновление отзыва под id:{}", updatedReview.getReviewId());
        String sql = ReviewSqlQueries.UPDATE_REVIEW;
        int affectedRows = jdbcTemplate.update(sql,
                updatedReview.getContent(),
                updatedReview.getIsPositive(),
                updatedReview.getReviewId());
        if (affectedRows == 0) {
            log.warn("Попытка обновить отзыв с id: {}. Отзыв не найден", updatedReview.getReviewId());
            throw new NotFoundException("Отзыв с указанным ID не найден: " + updatedReview.getReviewId());
        } else {
            String sqlQuery = ReviewSqlQueries.GET_REVIEW_BY_ID;
            Review updatedReviewInDb = jdbcTemplate.queryForObject(sqlQuery, reviewRowMapper, updatedReview.getReviewId());
            log.info("Отзыв под id:{} обновлен", updatedReviewInDb.getReviewId());
            return updatedReviewInDb;
        }
    }

    @Override
    public boolean removeReview(Long deletedReviewId) {
        try {
            String sql = ReviewSqlQueries.REMOVE_REVIEW;
            int rowsAffected = jdbcTemplate.update(sql, deletedReviewId);
            if (rowsAffected > 0) {
                log.info("Отзыв под id: {} удален", deletedReviewId);
                return true;
            } else {
                log.warn("Отзыв под id: {} уже удален или не существует", deletedReviewId);
                return false;
            }
        } catch (DataAccessException ex) {
            log.error("Произошла ошибка при удалении отзыва под id: {}", deletedReviewId, ex);
            throw new RemoveReviewException("Ошибка при удалении отзыва под id: " + deletedReviewId, ex);
        }
    }

    @Cacheable("reviews")
    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        try {
            String sql = ReviewSqlQueries.GET_REVIEW_BY_ID;
            Review review = jdbcTemplate.queryForObject(sql, reviewRowMapper, reviewId);
            log.info("Отзыв под id:{} получен", reviewId);
            return Optional.of(review);
        } catch (EmptyResultDataAccessException ex) {
            log.error("Произошла ошибка при получении отзыва по id: {}", reviewId, ex);
            return Optional.empty();
        }
    }

    @Cacheable("allReviews")
    @Override
    public List<Review> getAllReviews(Long filmId, int count) {
        String sql;
        if (filmId == null) {
            sql = ReviewSqlQueries.GET_ALL_REVIEWS;
            List<Review> allReviewsWithoutFilmId = jdbcTemplate.query(sql, reviewRowMapper, count);
            log.info("Получен список всех отзывов, независимо от идентификатора фильма");
            return allReviewsWithoutFilmId;
        } else {
            sql = ReviewSqlQueries.GET_ALL_REVIEWS_BY_FILM_ID;
            log.info("Получен список всех отзывов, для фильма под id% {}", filmId);
            return jdbcTemplate.query(sql, reviewRowMapper, filmId, count);
        }
    }

    @Transactional
    @Override
    public void likeReview(Long reviewId, Long userId) {
        try {
            String sql = ReviewSqlQueries.LIKE_REVIEW;
            jdbcTemplate.update(sql, userId, reviewId);
            incrementUsefulCount(reviewId); //count+1
            log.info("Добавлен 'лайк' от пользователя с id: {} к отзыву под id: {}", userId, reviewId);
        } catch (DataAccessException ex) {
            log.error("Произошла ошибка при добавлении лайка к отзыву с id: {}", reviewId, ex);
            throw new LikeReviewException("Ошибка при добавлении лайка к отзыву с id: " + reviewId, ex);
        }
    }

    @Transactional
    @Override
    public void dislikeReview(Long reviewId, Long userId) {
        try {
            String sql = ReviewSqlQueries.DISLIKE_REVIEW;
            jdbcTemplate.update(sql, userId, reviewId);
            decrementUsefulCount(reviewId); //count-1
            log.info("Добавлен 'дизлайк' от пользователя с id: {} к отзыву под id: {}", userId, reviewId);
        } catch (DataAccessException ex) {
            log.error("Произошла ошибка при добавлении дизлайка к отзыву с id: {}", reviewId, ex);
            throw new LikeReviewException("Ошибка при добавлении дизлайка к отзыву с id: " + reviewId, ex);
        }
    }

    @Transactional
    @Override
    public void removeLike(Long reviewId, Long userId) {
        try {
            String sql = ReviewSqlQueries.REMOVE_LIKE;
            int rowsAffected = jdbcTemplate.update(sql, userId, reviewId);
            if (rowsAffected > 0) {
                decrementUsefulCount(reviewId); //count-1
            }
            log.info("'Лайк' от пользователя с id: {} к отзыву под id: {} был удален", userId, reviewId);
        } catch (DataAccessException ex) {
            log.error("Произошла ошибка при удалении лайка с отзыва под id: {}", reviewId, ex);
            throw new LikeReviewException("Ошибка удалении лайка с отзыва под id: " + reviewId, ex);
        }
    }

    @Transactional
    @Override
    public void removeDislike(Long reviewId, Long userId) {
        try {
            String sql = ReviewSqlQueries.REMOVE_DISLIKE;
            int rowsAffected = jdbcTemplate.update(sql, userId, reviewId);
            if (rowsAffected > 0) {
                incrementUsefulCount(reviewId); //count+1
            }
            log.info("'Дизлайк' от пользователя с id: {} к отзыву под id: {} был удален", userId, reviewId);
        } catch (DataAccessException ex) {
            log.error("Произошла ошибка при удалении дизлайка с отзыва под id: {}", reviewId, ex);
            throw new LikeReviewException("Ошибка удалении дизлайка с отзыва под id: " + reviewId, ex);
        }
    }

    private void incrementUsefulCount(Long reviewId) {
        String sql = ReviewSqlQueries.UPDATE_REVIEW_INCREMENT_USEFUL_COUNT;
        jdbcTemplate.update(sql, reviewId);
    }

    private void decrementUsefulCount(Long reviewId) {
        String sql = ReviewSqlQueries.UPDATE_REVIEW_DECREMENT_USEFUL_COUNT;
        jdbcTemplate.update(sql, reviewId);
    }

    public boolean reviewExists(Long reviewId) {
        String sql = ReviewSqlQueries.CHECK_REVIEW_EXIST;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count != null && count > 0;
    }
}
