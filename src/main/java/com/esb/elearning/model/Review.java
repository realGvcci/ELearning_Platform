package com.esb.elearning.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A rating (1 to 5) and an optional comment left by a student on a course. */
@Entity
@Table(name = "review",
        uniqueConstraints = @UniqueConstraint(columnNames = { "student_id", "course_id" }))
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 1000)
    @Column(name = "review_comment", length = 1000)
    private String comment;

    private LocalDate createdAt;
}
