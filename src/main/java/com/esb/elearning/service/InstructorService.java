package com.esb.elearning.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.DuplicateResourceException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Instructor;
import com.esb.elearning.repository.CourseRepository;
import com.esb.elearning.repository.InstructorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public Instructor create(Instructor instructor) {
        if (instructorRepository.existsByEmailIgnoreCase(instructor.getEmail())) {
            throw new DuplicateResourceException("An instructor with email '" + instructor.getEmail() + "' already exists");
        }
        instructor.setId(null);
        return instructorRepository.save(instructor);
    }

    @Transactional(readOnly = true)
    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", id));
    }

    public Instructor update(Long id, Instructor data) {
        Instructor existing = findById(id);
        if (instructorRepository.existsByEmailIgnoreCaseAndIdNot(data.getEmail(), id)) {
            throw new DuplicateResourceException("An instructor with email '" + data.getEmail() + "' already exists");
        }
        existing.setFirstName(data.getFirstName());
        existing.setLastName(data.getLastName());
        existing.setEmail(data.getEmail());
        existing.setBio(data.getBio());
        return instructorRepository.save(existing);
    }

    public void delete(Long id) {
        Instructor existing = findById(id);
        if (courseRepository.existsByInstructorId(id)) {
            throw new BusinessRuleException("Cannot delete an instructor who still owns courses");
        }
        instructorRepository.delete(existing);
    }
}
