package com.example.smsauth.dto;

import jakarta.validation.constraints.NotBlank;

public class SmsSendRequest {

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}