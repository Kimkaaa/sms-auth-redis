package com.example.smsauth.exception;

import com.example.smsauth.common.ResponseCode;
import com.example.smsauth.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred. code={}, message={}",
                e.getResponseCode().getCode(),
                e.getMessage());

        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(e.getResponseCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : ResponseCode.INVALID_REQUEST.getDefaultMessage();

        log.warn("Validation exception occurred. message={}", message);

        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(ResponseCode.INVALID_REQUEST, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred.", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}