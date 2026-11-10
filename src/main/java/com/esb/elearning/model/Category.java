package com.esb.elearning.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A subject area used to group courses (Programming, Design, ...). */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 80, message = "Category name must not exceed 80 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    // One category -> many courses
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Course> courses = new ArrayList<>();
}
