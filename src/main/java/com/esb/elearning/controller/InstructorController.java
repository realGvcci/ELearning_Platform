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

import com.esb.elearning.model.Instructor;
import com.esb.elearning.service.InstructorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<Instructor> create(@Valid @RequestBody Instructor instructor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(instructorService.create(instructor));
    }

    @GetMapping
    public List<Instructor> findAll() {
        return instructorService.findAll();
    }

    @GetMapping("/{id}")
    public Instructor findById(@PathVariable Long id) {
        return instructorService.findById(id);
    }

    @PutMapping("/{id}")
    public Instructor update(@PathVariable Long id, @Valid @RequestBody Instructor instructor) {
        return instructorService.update(id, instructor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        instructorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
