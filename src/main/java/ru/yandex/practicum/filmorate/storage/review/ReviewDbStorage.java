package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Exceptions.CreateReviewException;
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
            String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful_count) VALUES (?, ?, ?, ?, ?);";
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
        // С точки зрения логики, отсальные поля не нужны, так как если добавить film_id & user_id
        //то будет подразумеваться, что отзыв будет перенесен на другой фильм или прсвоен другому пользователю
        //поле useful меняется только при добавлении или удалении лайка, а не при обнолении отзыва
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id =?;";
        int affectedRows = jdbcTemplate.update(sql,
                updatedReview.getContent(),
                updatedReview.getIsPositive(),
                updatedReview.getReviewId());
        if (affectedRows == 0) {
            log.warn("Попытка обновить отзыв с id: {}. Отзыв не найден", updatedReview.getReviewId());
            throw new NotFoundException("Отзыв с указанным ID не найден: " + updatedReview.getReviewId());
        } else {
            log.info("Отзыв под id:{} обновлен", updatedReview.getReviewId());
            return updatedReview;
        }
    }

    @Override
    public boolean removeReview(Long deletedReviewId) {
        try {
            String sql = "DELETE FROM reviews WHERE review_id = ?";
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

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        try {
            String sql = "SELECT r.review_id, r.content, r.is_positive, r.user_id, r.film_id, " +
                    //Так как у нас всего 2 варианта реакций (лайк/дизлайк), то используем
                    //CASE выражение внутри SUM функции для подсчета лайков (1) и дизлайков (-1)
                    "SUM(CASE WHEN rl.reaction = 'LIKE' THEN 1 WHEN rl.reaction = 'DISLIKE' THEN -1 ELSE 0 END) " +
                    "AS useful_count " +
                    "FROM reviews r " +
                    "LEFT JOIN review_reactions rl on r.review_id = rl.review_id " +
                    "WHERE r.review_id = ? " +
                    "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id";
            Review review = jdbcTemplate.queryForObject(sql, reviewRowMapper, reviewId);
            log.info("Отзыв под id:{} получен", reviewId);
            return Optional.of(review);
        } catch (EmptyResultDataAccessException ex) {
            log.error("Произошла ошибка при получении отзыва по id: {}", reviewId, ex);
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAllReviews(Long filmId, int count) {
        String sql;
        if (filmId == null) {
            sql = "SELECT * FROM reviews LIMIT ?";
            List<Review> allReviewsWithoutFilmId = jdbcTemplate.query(sql, reviewRowMapper, count);
            log.info("Получен список всех отзывов, независимо от идентификатора фильма");
            return allReviewsWithoutFilmId;
        } else {
            sql = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
            log.info("Получен список всех отзывов, для фильма под id% {}", filmId);
            return jdbcTemplate.query(sql, reviewRowMapper, filmId, count);
        }
    }


    //Мне необходимо предусмотреть, тот факт, что каждый пользователь может в любое время пометь свое мнение с 'LIKE'
    //на 'DISLIKE' без необходимости перед этим удалять свою реацию. Для этого я буду использовать запрос с 'MERGE',
    //который будет работать как "вставить или обновить", в записимости от того, есть ли запись в таблице.
    //Ссылка на источник: https://www.tutorialspoint.com/h2_database/h2_database_merge.htm
    @Override
    public void likeReview(Long reviewId, Long userId) {
        String sql = "MERGE INTO review_reactions (user_id, review_id, reaction) KEY (user_id, review_id) VALUES (?, ?, 'LIKE')";
        jdbcTemplate.update(sql, userId, reviewId);
        incrementUsefulCount(reviewId); //count+1
        log.info("Добавлен 'лайк' от пользователя с id: {} к отзыву под id: {}", userId, reviewId);
    }


    @Override
    public void dislikeReview(Long reviewId, Long userId) {
        String sql = "MERGE INTO review_reactions (user_id, review_id, reaction) KEY (user_id, review_id) VALUES (?, ?, 'DISLIKE')";
        jdbcTemplate.update(sql, userId, reviewId);
        decrementUsefulCount(reviewId); //count-1
        log.info("Добавлен 'дизлайк' от пользователя с id: {} к отзыву под id: {}", userId, reviewId);
    }

    //Здесь мы, кроме всего прочего, обрабатываем ситуацию, когда пользователь пытается удалить лайк, но на
    //данный момент у него стоит дизлайк, и наоборот.
    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_reactions WHERE user_id = ? AND review_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, reviewId);
        if (rowsAffected > 0) {
            decrementUsefulCount(reviewId); //count-1
        }
        log.info("'Лайк' от пользователя с id: {} к отзыву под id: {} был удален", userId, reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_reactions WHERE user_id = ? AND review_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, reviewId);
        if (rowsAffected > 0) {
            incrementUsefulCount(reviewId); //count+1
        }
        log.info("'Дизлайк' от пользователя с id: {} к отзыву под id: {} был удален", userId, reviewId);
    }

    //Для избежания дублирования кода, добавил 2 приватных метода
    private void incrementUsefulCount(Long reviewId) {
        String sql = "UPDATE reviews SET useful_count = useful_count + 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    private void decrementUsefulCount(Long reviewId) {
        String sql = "UPDATE reviews SET useful_count = useful_count - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    public boolean reviewExists(Long reviewId) {
        String sql = "SELECT COUNT(*) FROM REVIEWS " +
                "WHERE REVIEW_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count != null && count > 0;
    }
}
