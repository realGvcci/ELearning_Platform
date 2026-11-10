package com.esb.elearning.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.dto.CourseRatingView;
import com.esb.elearning.dto.ReviewRequest;
import com.esb.elearning.dto.ReviewUpdateRequest;
import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.DuplicateResourceException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Course;
import com.esb.elearning.model.Enrollment;
import com.esb.elearning.model.EnrollmentStatus;
import com.esb.elearning.model.Review;
import com.esb.elearning.model.Student;
import com.esb.elearning.repository.CourseRepository;
import com.esb.elearning.repository.EnrollmentRepository;
import com.esb.elearning.repository.ReviewRepository;
import com.esb.elearning.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /** Business rules: only enrolled (non-cancelled) students can review, and only once per course. */
    public Review create(ReviewRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.studentId()));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.courseId()));

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new BusinessRuleException("Only students enrolled in the course can review it"));
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessRuleException("A student with a cancelled enrollment cannot review the course");
        }
        if (reviewRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new DuplicateResourceException("Student has already reviewed this course");
        }

        Review review = new Review();
        review.setStudent(student);
        review.setCourse(course);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setCreatedAt(LocalDate.now());
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }

    @Transactional(readOnly = true)
    public List<Review> findByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", courseId);
        }
        return reviewRepository.findByCourseId(courseId);
    }

    public Review update(Long id, ReviewUpdateRequest request) {
        Review review = findById(id);
        review.setRating(request.rating());
        review.setComment(request.comment());
        return reviewRepository.save(review);
    }

    public void delete(Long id) {
        reviewRepository.delete(findById(id));
    }

    // ----- custom query -----
    @Transactional(readOnly = true)
    public List<CourseRatingView> findTopRated(double minRating) {
        if (minRating < 1 || minRating > 5) {
            throw new BusinessRuleException("minRating must be between 1 and 5");
        }
        return reviewRepository.findTopRatedCourses(minRating);
    }
}
