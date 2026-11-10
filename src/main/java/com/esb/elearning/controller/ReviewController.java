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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esb.elearning.dto.CourseRatingView;
import com.esb.elearning.dto.ReviewRequest;
import com.esb.elearning.dto.ReviewUpdateRequest;
import com.esb.elearning.model.Review;
import com.esb.elearning.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(request));
    }

    @GetMapping
    public List<Review> findAll() {
        return reviewService.findAll();
    }

    @GetMapping("/top-rated")
    public List<CourseRatingView> topRated(@RequestParam(defaultValue = "4.0") double minRating) {
        return reviewService.findTopRated(minRating);
    }

    @GetMapping("/course/{courseId}")
    public List<Review> findByCourse(@PathVariable Long courseId) {
        return reviewService.findByCourse(courseId);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @Valid @RequestBody ReviewUpdateRequest request) {
        return reviewService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
