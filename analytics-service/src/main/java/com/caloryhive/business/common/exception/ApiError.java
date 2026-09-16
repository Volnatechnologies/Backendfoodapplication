package com.caloryhive.business.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private boolean success;
    private String message;
    private String errorCode;
    private Integer status;
    private OffsetDateTime timestamp;
    private String requestId;
    private Map<String, String> validationErrors;
}
