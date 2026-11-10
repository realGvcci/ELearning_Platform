package com.esb.elearning.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esb.elearning.exception.BusinessRuleException;
import com.esb.elearning.exception.DuplicateResourceException;
import com.esb.elearning.exception.ResourceNotFoundException;
import com.esb.elearning.model.Tag;
import com.esb.elearning.repository.CourseRepository;
import com.esb.elearning.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;

    public Tag create(Tag tag) {
        tag.setName(tag.getName().trim().toLowerCase());
        if (tagRepository.existsByNameIgnoreCase(tag.getName())) {
            throw new DuplicateResourceException("Tag '" + tag.getName() + "' already exists");
        }
        tag.setId(null);
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    public Tag update(Long id, Tag data) {
        Tag existing = findById(id);
        String name = data.getName().trim().toLowerCase();
        if (tagRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Tag '" + name + "' already exists");
        }
        existing.setName(name);
        return tagRepository.save(existing);
    }

    public void delete(Long id) {
        Tag existing = findById(id);
        if (courseRepository.existsByTagsId(id)) {
            throw new BusinessRuleException("Cannot delete a tag that is used by courses");
        }
        tagRepository.delete(existing);
    }
}
