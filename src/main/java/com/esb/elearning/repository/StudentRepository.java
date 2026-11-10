package com.esb.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esb.elearning.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    /** CUSTOM QUERY 1 - students registered but who never enrolled in any course (marketing target). */
    @Query("SELECT s FROM Student s WHERE s.enrollments IS EMPTY ORDER BY s.fullName")
    List<Student> findStudentsWithoutEnrollment();

    /** CUSTOM QUERY 2 - students currently following (or who finished) a given course. */
    @Query("""
            SELECT e.student FROM Enrollment e
            WHERE e.course.id = :courseId
              AND e.status <> com.esb.elearning.model.EnrollmentStatus.CANCELLED
            ORDER BY e.student.fullName
            """)
    List<Student> findStudentsByCourse(@Param("courseId") Long courseId);
}
