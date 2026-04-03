package com.example.smsauth.dto;

import com.example.smsauth.common.ResponseCode;

public class ApiResponse {

    private boolean success;
    private String code;
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public static ApiResponse success(ResponseCode responseCode) {
        return new ApiResponse(true, responseCode.getCode(), responseCode.getDefaultMessage());
    }

    public static ApiResponse success(ResponseCode responseCode, String message) {
        return new ApiResponse(true, responseCode.getCode(), message);
    }

    public static ApiResponse fail(ResponseCode responseCode) {
        return new ApiResponse(false, responseCode.getCode(), responseCode.getDefaultMessage());
    }

    public static ApiResponse fail(ResponseCode responseCode, String message) {
        return new ApiResponse(false, responseCode.getCode(), message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}