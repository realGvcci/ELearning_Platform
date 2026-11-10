package com.esb.elearning.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.dto.CoursePopularityView;
import com.esb.elearning.dto.CourseRequest;
import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Category;
import com.esb.elearning.model.Course;
import com.esb.elearning.model.CourseLevel;
import com.esb.elearning.model.Instructor;
import com.esb.elearning.model.Tag;
import com.esb.elearning.repository.CategoryRepository;
import com.esb.elearning.repository.CourseRepository;
import com.esb.elearning.repository.EnrollmentRepository;
import com.esb.elearning.repository.InstructorRepository;
import com.esb.elearning.repository.ReviewRepository;
import com.esb.elearning.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private static final BigDecimal NO_PRICE_LIMIT = new BigDecimal("999999.99");

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;

    public Course create(CourseRequest request) {
        Course course = new Course();
        course.setCreatedAt(LocalDate.now());
        course.setPublished(false); // a new course is a draft until it is published
        applyRequest(course, request);
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    public Course update(Long id, CourseRequest request) {
        Course course = findById(id);
        applyRequest(course, request);
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        Course course = findById(id);
        if (enrollmentRepository.existsByCourseId(id) || reviewRepository.existsByCourseId(id)) {
            throw new BusinessRuleException("Cannot delete a course that has enrollments or reviews");
        }
        courseRepository.delete(course);
    }

    /** Business rule: a draft course needs a description before it can be published. */
    public Course publish(Long id) {
        Course course = findById(id);
        if (course.isPublished()) {
            throw new BusinessRuleException("Course is already published");
        }
        if (course.getDescription() == null || course.getDescription().isBlank()) {
            throw new BusinessRuleException("A course needs a description before it can be published");
        }
        course.setPublished(true);
        return courseRepository.save(course);
    }

    // ----- custom queries -----
    @Transactional(readOnly = true)
    public List<Course> search(String keyword, CourseLevel level, BigDecimal maxPrice) {
        String kw = keyword == null ? "" : keyword.trim();
        BigDecimal limit = maxPrice == null ? NO_PRICE_LIMIT : maxPrice;
        return courseRepository.search(kw, level, limit);
    }

    @Transactional(readOnly = true)
    public List<CoursePopularityView> findMostPopular(int limit) {
        if (limit < 1 || limit > 100) {
            throw new BusinessRuleException("limit must be between 1 and 100");
        }
        return courseRepository.findMostPopular(PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<Course> findByTag(String tagName) {
        return courseRepository.findByTagsNameIgnoreCase(tagName);
    }

    @Transactional(readOnly = true)
    public List<Course> findByInstructor(Long instructorId) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor", instructorId);
        }
        return courseRepository.findByInstructorId(instructorId);
    }

    @Transactional(readOnly = true)
    public List<Course> findByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", categoryId);
        }
        return courseRepository.findByCategoryId(categoryId);
    }

    // ----- helper -----
    private void applyRequest(Course course, CourseRequest request) {
        Instructor instructor = instructorRepository.findById(request.instructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", request.instructorId()));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

        Set<Tag> tags = new HashSet<>();
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            tags.addAll(tagRepository.findAllById(request.tagIds()));
            if (tags.size() != request.tagIds().size()) {
                throw new ResourceNotFoundException("One or more tags were not found");
            }
        }

        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setLevel(request.level());
        course.setDurationHours(request.durationHours());
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setTags(tags);
    }
}
