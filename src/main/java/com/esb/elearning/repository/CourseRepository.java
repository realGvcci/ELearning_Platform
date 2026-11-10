package com.esb.elearning.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esb.elearning.dto.CoursePopularityView;
import com.esb.elearning.model.Course;
import com.esb.elearning.model.CourseLevel;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // ---- derived queries (used for relationship checks / filters) ----
    boolean existsByInstructorId(Long instructorId);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByTagsId(Long tagId);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByCategoryId(Long categoryId);

    /** CUSTOM QUERY 3 - courses carrying a given tag (navigates the many-to-many). */
    List<Course> findByTagsNameIgnoreCase(String tagName);

    /** CUSTOM QUERY 4 - catalogue search: keyword + optional level + max price, published courses only. */
    @Query("""
            SELECT c FROM Course c
            WHERE c.published = true
              AND LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND (:level IS NULL OR c.level = :level)
              AND c.price <= :maxPrice
            ORDER BY c.price ASC
            """)
    List<Course> search(@Param("keyword") String keyword,
            @Param("level") CourseLevel level,
            @Param("maxPrice") BigDecimal maxPrice);

    /** CUSTOM QUERY 5 - most popular courses (number of non-cancelled enrollments). */
    @Query("""
            SELECT c.id AS courseId, c.title AS title, COUNT(e) AS enrollmentCount
            FROM Course c LEFT JOIN c.enrollments e
                 ON e.status <> com.esb.elearning.model.EnrollmentStatus.CANCELLED
            GROUP BY c.id, c.title
            ORDER BY COUNT(e) DESC, c.title ASC
            """)
    List<CoursePopularityView> findMostPopular(Pageable pageable);
}
