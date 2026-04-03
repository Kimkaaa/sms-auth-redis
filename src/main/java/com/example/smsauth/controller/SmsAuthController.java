package com.example.smsauth.controller;

import com.example.smsauth.dto.ApiResponse;
import com.example.smsauth.dto.SmsAuthConfigResponse;
import com.example.smsauth.dto.SmsSendAvailabilityResponse;
import com.example.smsauth.dto.SmsSendRequest;
import com.example.smsauth.dto.SmsVerifyRequest;
import com.example.smsauth.service.SmsAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsAuthController {

    private final SmsAuthService smsAuthService;

    @PostMapping("/send")
    public ApiResponse send(@Valid @RequestBody SmsSendRequest request) {
        return smsAuthService.sendVerificationCode(request.getPhoneNumber());
    }

    @PostMapping("/verify")
    public ApiResponse verify(@Valid @RequestBody SmsVerifyRequest request) {
        return smsAuthService.verifyCode(
                request.getPhoneNumber(),
                request.getVerificationCode()
        );
    }

    @GetMapping("/status")
    public ApiResponse status(@RequestParam String phoneNumber) {
        return smsAuthService.checkVerified(phoneNumber);
    }

    @GetMapping("/config")
    public SmsAuthConfigResponse config() {
        return new SmsAuthConfigResponse(
                smsAuthService.getCodeExpireSeconds(),
                smsAuthService.getSendLimitCount()
        );
    }

    @GetMapping("/send-availability")
    public SmsSendAvailabilityResponse sendAvailability(@RequestParam String phoneNumber) {
        return smsAuthService.getSendAvailability(phoneNumber);
    }
}