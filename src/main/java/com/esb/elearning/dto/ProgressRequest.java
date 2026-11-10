package com.esb.elearning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProgressRequest(
        @NotNull(message = "Progress is required")
        @Min(value = 0, message = "Progress must be between 0 and 100")
        @Max(value = 100, message = "Progress must be between 0 and 100")
        Integer progress) {
}
