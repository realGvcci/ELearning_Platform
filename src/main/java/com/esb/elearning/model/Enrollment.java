package com.esb.elearning.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Association entity between Student and Course (a student can follow many courses,
 * a course has many students). It carries its own data: status, progress, price paid.
 */
@Entity
@Table(name = "enrollment",
        uniqueConstraints = @UniqueConstraint(columnNames = { "student_id", "course_id" }))
@Getter
@Setter
@NoArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Min(0)
    @Max(100)
    private int progress;

    // Price at the moment of enrollment (kept even if the course price changes later)
    private BigDecimal pricePaid;
}
