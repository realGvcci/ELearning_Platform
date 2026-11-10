package com.esb.elearning.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.dto.EnrollmentRequest;
import com.esb.elearning.dto.InstructorRevenueView;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    /**
     * Business rules: the course must be published, a student can enroll only once
     * in a given course, and the price paid is the course price at that moment.
     */
    public Enrollment enroll(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.studentId()));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.courseId()));

        if (!course.isPublished()) {
            throw new BusinessRuleException("Course '" + course.getTitle() + "' is not published yet");
        }
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setProgress(0);
        enrollment.setPricePaid(course.getPrice());
        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Enrollment findById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id));
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", courseId);
        }
        return enrollmentRepository.findByCourseId(courseId);
    }

    /** Reaching 100% automatically completes the enrollment. A cancelled enrollment cannot progress. */
    public Enrollment updateProgress(Long id, int progress) {
        Enrollment enrollment = findById(id);
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot update the progress of a cancelled enrollment");
        }
        enrollment.setProgress(progress);
        enrollment.setStatus(progress == 100 ? EnrollmentStatus.COMPLETED : EnrollmentStatus.ACTIVE);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment cancel(Long id) {
        Enrollment enrollment = findById(id);
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new BusinessRuleException("A completed enrollment cannot be cancelled");
        }
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessRuleException("Enrollment is already cancelled");
        }
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        return enrollmentRepository.save(enrollment);
    }

    public void delete(Long id) {
        enrollmentRepository.delete(findById(id));
    }

    // ----- custom query -----
    @Transactional(readOnly = true)
    public List<InstructorRevenueView> revenueByInstructor() {
        return enrollmentRepository.findRevenueByInstructor();
    }
}
