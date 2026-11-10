package com.esb.elearning.dto;

/** Projection: average rating of a course. */
public interface CourseRatingView {
    Long getCourseId();
    String getTitle();
    Double getAverageRating();
    Long getReviewCount();
}
