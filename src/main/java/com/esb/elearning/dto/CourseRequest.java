package com.esb.elearning.dto;

import java.math.BigDecimal;
import java.util.Set;

import com.esb.elearning.model.CourseLevel;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Payload used to create or update a course (relations are given by id). */
public record CourseRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        @Digits(integer = 6, fraction = 2, message = "Price format is invalid")
        BigDecimal price,

        @NotNull(message = "Level is required")
        CourseLevel level,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be at least 1 hour")
        Integer durationHours,

        @NotNull(message = "Instructor id is required")
        Long instructorId,

        @NotNull(message = "Category id is required")
        Long categoryId,

        Set<Long> tagIds) {
}
