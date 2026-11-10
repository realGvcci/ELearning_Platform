package com.esb.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esb.elearning.dto.CourseRatingView;
import com.esb.elearning.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentId(Long studentId);

    boolean existsByCourseId(Long courseId);

    List<Review> findByCourseId(Long courseId);

    /** CUSTOM QUERY 7 - best rated courses (average rating >= minRating). */
    @Query("""
            SELECT c.id AS courseId, c.title AS title,
                   AVG(r.rating) AS averageRating, COUNT(r) AS reviewCount
            FROM Review r JOIN r.course c
            GROUP BY c.id, c.title
            HAVING AVG(r.rating) >= :minRating
            ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
            """)
    List<CourseRatingView> findTopRatedCourses(@Param("minRating") double minRating);
}
