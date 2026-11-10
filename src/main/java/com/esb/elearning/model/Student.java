package com.esb.elearning.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A learner who enrolls in courses and leaves reviews. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 60, message = "Country must not exceed 60 characters")
    private String country;

    // Set automatically by the service
    private LocalDate registrationDate;

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<Review> reviews = new ArrayList<>();
}
