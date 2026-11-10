package com.esb.elearning.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esb.elearning.model.Student;
import com.esb.elearning.service.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(student));
    }

    @GetMapping
    public List<Student> findAll() {
        return studentService.findAll();
    }

    // NOTE: fixed paths are declared before "/{id}" for clarity
    @GetMapping("/without-enrollment")
    public List<Student> findWithoutEnrollment() {
        return studentService.findWithoutEnrollment();
    }

    @GetMapping("/course/{courseId}")
    public List<Student> findByCourse(@PathVariable Long courseId) {
        return studentService.findByCourse(courseId);
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student student) {
        return studentService.update(id, student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
