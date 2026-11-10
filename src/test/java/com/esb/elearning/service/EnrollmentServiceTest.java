package com.esb.elearning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esb.elearning.dto.EnrollmentRequest;
import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.DuplicateResourceException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Course;
import com.esb.elearning.model.Enrollment;
import com.esb.elearning.model.EnrollmentStatus;
import com.esb.elearning.model.Student;
import com.esb.elearning.repository.CourseRepository;
import com.esb.elearning.repository.EnrollmentRepository;
import com.esb.elearning.repository.StudentRepository;

/** Unit tests of the business rules of EnrollmentService (repositories are mocked). */
@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        course = new Course();
        course.setId(10L);
        course.setTitle("Spring Boot");
        course.setPrice(new BigDecimal("49.90"));
        course.setPublished(true);
    }

    @Test
    void enroll_createsActiveEnrollmentWithCoursePrice() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 10L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentService.enroll(new EnrollmentRequest(1L, 10L));

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(result.getProgress()).isZero();
        assertThat(result.getPricePaid()).isEqualByComparingTo("49.90");
    }

    @Test
    void enroll_unpublishedCourse_throwsBusinessRuleException() {
        course.setPublished(false);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("not published");
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enroll_alreadyEnrolled_throwsDuplicateResourceException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void enroll_unknownStudent_throwsResourceNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(99L, 10L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProgress_to100_completesEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentService.updateProgress(5L, 100);

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(result.getProgress()).isEqualTo(100);
    }

    @Test
    void updateProgress_onCancelledEnrollment_throwsBusinessRuleException() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.updateProgress(5L, 50))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cancel_completedEnrollment_throwsBusinessRuleException() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        when(enrollmentRepository.findById(5L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.cancel(5L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("completed");
    }
}
