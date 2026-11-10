package com.esb.elearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esb.elearning.model.Instructor;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
