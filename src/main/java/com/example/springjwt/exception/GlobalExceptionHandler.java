package com.example.springjwt.exception;

import com.example.springjwt.dto.ApiResponse;
import com.example.springjwt.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<String> response = new ApiResponse<>("error", errorCode.getMessage(), null);
        log.error("ApiException occurred : ErrorCode = {} message = {}",
                errorCode.getStatus(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Object> response = new ApiResponse<>("error", ex.getMessage(), null);
        log.error("Exception occurred : Error message = {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
