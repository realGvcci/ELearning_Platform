package com.esb.elearning.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.DuplicateResourceException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Category;
import com.esb.elearning.repository.CategoryRepository;
import com.esb.elearning.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public Category create(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new DuplicateResourceException("Category '" + category.getName() + "' already exists");
        }
        category.setId(null);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public Category update(Long id, Category data) {
        Category existing = findById(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(data.getName(), id)) {
            throw new DuplicateResourceException("Category '" + data.getName() + "' already exists");
        }
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        Category existing = findById(id);
        if (courseRepository.existsByCategoryId(id)) {
            throw new BusinessRuleException("Cannot delete a category that still contains courses");
        }
        categoryRepository.delete(existing);
    }
}
