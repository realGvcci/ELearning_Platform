package com.esb.elearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esb.elearning.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
