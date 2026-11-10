package com.esb.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.esb.elearning.dto.InstructorRevenueView;
import com.esb.elearning.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentId(Long studentId);

    boolean existsByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    /** CUSTOM QUERY 6 - revenue and number of sales per instructor (cancelled enrollments excluded). */
    @Query("""
            SELECT i.id AS instructorId,
                   CONCAT(i.firstName, ' ', i.lastName) AS instructorName,
                   COUNT(e) AS enrollmentCount,
                   SUM(e.pricePaid) AS totalRevenue
            FROM Enrollment e
                 JOIN e.course c
                 JOIN c.instructor i
            WHERE e.status <> com.esb.elearning.model.EnrollmentStatus.CANCELLED
            GROUP BY i.id, i.firstName, i.lastName
            ORDER BY SUM(e.pricePaid) DESC
            """)
    List<InstructorRevenueView> findRevenueByInstructor();
}
