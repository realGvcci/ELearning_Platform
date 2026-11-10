package com.esb.elearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esb.elearning.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
