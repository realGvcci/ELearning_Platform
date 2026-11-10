package com.esb.elearning.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The central entity: a course sold on the platform. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    @Digits(integer = 6, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @NotNull(message = "Level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private CourseLevel level;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer durationHours;

    // A course is only available for enrollment once it is published
    private boolean published;

    private LocalDate createdAt;

    // Many courses -> one instructor
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // Many courses -> one category
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    // Many-to-many with Tag (Course is the owning side -> join table course_tag)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "course")
    private List<Review> reviews = new ArrayList<>();
}
