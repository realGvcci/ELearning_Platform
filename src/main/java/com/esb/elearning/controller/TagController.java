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

import com.esb.elearning.model.Tag;
import com.esb.elearning.service.TagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<Tag> create(@Valid @RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(tag));
    }

    @GetMapping
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public Tag findById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    @PutMapping("/{id}")
    public Tag update(@PathVariable Long id, @Valid @RequestBody Tag tag) {
        return tagService.update(id, tag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
