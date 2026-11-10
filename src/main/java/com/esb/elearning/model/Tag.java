package com.esb.elearning.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A keyword attached to courses (java, spring, sql...). Many-to-many with Course. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(max = 40, message = "Tag name must not exceed 40 characters")
    @Column(nullable = false, unique = true)
    private String name;

    // Inverse side of the many-to-many (the owning side is Course.tags)
    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Course> courses = new HashSet<>();
}
