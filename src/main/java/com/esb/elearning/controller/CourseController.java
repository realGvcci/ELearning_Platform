package com.esb.elearning.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esb.elearning.dto.CoursePopularityView;
import com.esb.elearning.dto.CourseRequest;
import com.esb.elearning.model.Course;
import com.esb.elearning.model.CourseLevel;
import com.esb.elearning.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
    }

    @GetMapping
    public List<Course> findAll() {
        return courseService.findAll();
    }

    // ---------- custom queries ----------
    @GetMapping("/search")
    public List<Course> search(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return courseService.search(keyword, level, maxPrice);
    }

    @GetMapping("/popular")
    public List<CoursePopularityView> popular(@RequestParam(defaultValue = "5") int limit) {
        return courseService.findMostPopular(limit);
    }

    @GetMapping("/by-tag/{tagName}")
    public List<Course> findByTag(@PathVariable String tagName) {
        return courseService.findByTag(tagName);
    }

    @GetMapping("/instructor/{instructorId}")
    public List<Course> findByInstructor(@PathVariable Long instructorId) {
        return courseService.findByInstructor(instructorId);
    }

    @GetMapping("/category/{categoryId}")
    public List<Course> findByCategory(@PathVariable Long categoryId) {
        return courseService.findByCategory(categoryId);
    }

    // ---------- CRUD by id ----------
    @GetMapping("/{id}")
    public Course findById(@PathVariable Long id) {
        return courseService.findById(id);
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @PatchMapping("/{id}/publish")
    public Course publish(@PathVariable Long id) {
        return courseService.publish(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
