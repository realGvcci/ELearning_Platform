package com.esb.elearning.dto;

/** Projection: number of (non-cancelled) enrollments of a course. */
public interface CoursePopularityView {
    Long getCourseId();
    String getTitle();
    Long getEnrollmentCount();
}
