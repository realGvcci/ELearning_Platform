package com.esb.elearning.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esb.elearning.dto.EnrollmentRequest;
import com.esb.elearning.dto.InstructorRevenueView;
import com.esb.elearning.dto.ProgressRequest;
import com.esb.elearning.model.Enrollment;
import com.esb.elearning.service.EnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<Enrollment> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(request));
    }

    @GetMapping
    public List<Enrollment> findAll() {
        return enrollmentService.findAll();
    }

    @GetMapping("/revenue-by-instructor")
    public List<InstructorRevenueView> revenueByInstructor() {
        return enrollmentService.revenueByInstructor();
    }

    @GetMapping("/student/{studentId}")
    public List<Enrollment> findByStudent(@PathVariable Long studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<Enrollment> findByCourse(@PathVariable Long courseId) {
        return enrollmentService.findByCourse(courseId);
    }

    @GetMapping("/{id}")
    public Enrollment findById(@PathVariable Long id) {
        return enrollmentService.findById(id);
    }

    @PatchMapping("/{id}/progress")
    public Enrollment updateProgress(@PathVariable Long id, @Valid @RequestBody ProgressRequest request) {
        return enrollmentService.updateProgress(id, request.progress());
    }

    @PatchMapping("/{id}/cancel")
    public Enrollment cancel(@PathVariable Long id) {
        return enrollmentService.cancel(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
