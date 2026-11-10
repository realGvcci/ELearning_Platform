package com.esb.elearning.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.DuplicateResourceException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Student;
import com.esb.elearning.repository.EnrollmentRepository;
import com.esb.elearning.repository.ReviewRepository;
import com.esb.elearning.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;

    public Student create(Student student) {
        if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
            throw new DuplicateResourceException("A student with email '" + student.getEmail() + "' already exists");
        }
        student.setId(null);
        student.setRegistrationDate(LocalDate.now());
        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    public Student update(Long id, Student data) {
        Student existing = findById(id);
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(data.getEmail(), id)) {
            throw new DuplicateResourceException("A student with email '" + data.getEmail() + "' already exists");
        }
        existing.setFullName(data.getFullName());
        existing.setEmail(data.getEmail());
        existing.setCountry(data.getCountry());
        return studentRepository.save(existing);
    }

    public void delete(Long id) {
        Student existing = findById(id);
        if (enrollmentRepository.existsByStudentId(id) || reviewRepository.existsByStudentId(id)) {
            throw new BusinessRuleException("Cannot delete a student who has enrollments or reviews");
        }
        studentRepository.delete(existing);
    }

    // ----- custom queries -----
    @Transactional(readOnly = true)
    public List<Student> findWithoutEnrollment() {
        return studentRepository.findStudentsWithoutEnrollment();
    }

    @Transactional(readOnly = true)
    public List<Student> findByCourse(Long courseId) {
        return studentRepository.findStudentsByCourse(courseId);
    }
}
